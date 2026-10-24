package ec.fin.be.sigered.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;

public class CustomLogger extends Logger {
    private final String className;
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS");
    private static final DateTimeFormatter DATE_FILE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final Object FILE_LOCK = new Object();
    private static final ConcurrentHashMap<String, CustomLogger> CACHE = new ConcurrentHashMap<>();

    protected CustomLogger(String className) {
        super(className);
        this.className = className;

        File dir = new File(getFolderPath());

        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static CustomLogger getCustomLogger(Class<?> clazz) {
        return CACHE.computeIfAbsent(clazz.getName(), CustomLogger::new);
    }

    @Override
    public void info(Object message) {
        writeLog("INFO", String.valueOf(message));
    }

    @Override
    public void debug(Object message) {
        writeLog("DEBUG", String.valueOf(message));
    }

    @Override
    public void warn(Object message) {
        writeLog("WARN", String.valueOf(message));
    }

    @Override
    public void error(Object message) {
        writeLog("ERROR", String.valueOf(message));
    }

    @Override
    public void fatal(Object message) {
            writeLog("FATAL", String.valueOf(message));
    }

    //throwables
    @Override
    public void warn(Object message, Throwable t) {
        writeThrowable("WARN", message, t);
    }

    @Override
    public void info(Object message, Throwable t) {
        writeThrowable("INFO", message, t);
    }

    @Override
    public void debug(Object message, Throwable t) {
        writeThrowable("DEBUG", message, t);
    }

    @Override
    public void error(Object message, Throwable t) {
        writeThrowable("ERROR", message, t);
    }

    @Override
    public void fatal(Object message, Throwable t) {
        writeThrowable("FATAL", message, t);
    }

    private void writeThrowable(String level, Object message, Throwable t) {

        StringBuilder sb = new StringBuilder();

        if (message != null) {
            sb.append(message);
        }

        if (t != null) {

            sb.append(System.lineSeparator())
            .append(t);

            for (StackTraceElement ste : t.getStackTrace()) {
                sb.append(System.lineSeparator())
                .append("\tat ")
                .append(ste);
            }
        }

        writeLog(level, sb.toString());
    }

    private void writeLog(String logLevel, String message) {

        synchronized (FILE_LOCK) {
        
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(getLogFileName(), true))) {

                writer.write(
                    String.format(
                        "%s;[%s];[%s];%s - %s",
                        getFechaHora(),
                        Thread.currentThread().getName(),
                        logLevel,
                        className,
                        message));

                writer.newLine();

            } catch (IOException e) {
                LogLog.error("Unable to write custom log file: " + getLogFileName(), e);
            }
        }
    }


    private static String getFolderPath() {
        String baseDir = System.getProperty("jboss.banecuador.logsApps", "/opt/logs");
        return baseDir + "/logsSigered";
    }

    private static String getLogFileName() {
        return getFolderPath() + "/Sigered-" + getFechaArchivo() + ".log";
    }

    private static String getFechaHora() {
        return LocalDateTime.now().format(DATE_TIME);
    }

    public static String getFechaArchivo() {
        return LocalDate.now().format(DATE_FILE);
    }
}
