package ec.fin.be.sigered.logger;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.LogLog;

public final class NativeLogger {

    private static final ConcurrentHashMap<String, Logger> CACHE = new ConcurrentHashMap<>();

    private NativeLogger() {
        
    }

    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    public static Logger getLogger(String loggerName) {
        return CACHE.computeIfAbsent(loggerName,
                NativeLogger::createLogger);
    }

    private static Logger createLogger(String loggerName) {

        try {

            String baseDir = System.getProperty("jboss.banecuador.logsApps", "/opt/logs");

            String folderPath = baseDir + "/logsSigered";
            String archivo = folderPath + "/Sigered.log";

            File dir = new File(folderPath);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            PatternLayout layout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss,SSS};[%t];[%-5p];%c.%M - %m%n");//NOSONAR

            DailyRollingFileAppender appender = new DailyRollingFileAppender();
            appender.setName("SIGERED_APPENDER_" + loggerName);
            appender.setFile(archivo);
            appender.setLayout(layout);
            appender.setDatePattern("'.'yyyy-MM-dd");
            appender.setAppend(true);
            appender.activateOptions();

            @SuppressWarnings("squid:S1312")
            Logger logInstance = buildLogger(loggerName);
            logInstance.setAdditivity(false);

            // Si no existe el appender lo agrega
            if (logInstance.getAppender(appender.getName()) == null) {
                logInstance.addAppender(appender);
            }

            return logInstance;

        } catch (Exception e) {

            LogLog.error("Error creating logger " + loggerName, e);
            return buildLogger(loggerName);
        }
    }

    @SuppressWarnings("squid:S1312")
    private static Logger buildLogger(String loggerName) {
        Logger logInstance = Logger.getLogger(loggerName);
        logInstance.setLevel(getLogLevel());
        return logInstance;
    }

    private static Level getLogLevel() {

        String nivel = System.getProperty("jboss.banecuador.nivellog", "INFO");

        switch (nivel.toUpperCase()) {

            case "DEBUG":
                return Level.DEBUG;

            case "WARN":
                return Level.WARN;

            case "ERROR":
                return Level.ERROR;

            default:
                return Level.INFO;
        }
    }
}