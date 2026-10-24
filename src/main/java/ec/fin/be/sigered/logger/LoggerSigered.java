package ec.fin.be.sigered.logger;

import org.apache.log4j.Logger;

import ec.fin.be.sigered.UtilPropertiesArquetipo;
import ec.fin.be.sigered.utils.Constantes;

public final class LoggerSigered {
    private static final UtilPropertiesArquetipo propFile = UtilPropertiesArquetipo.getIntances();
    private static final String LOGGER_IMPL = propFile.obtenerParametro(Constantes.LOGGER_IMPLEMENTACION);

    private LoggerSigered() {
    }
    
    public static Logger getLogger(Class<?> clazz) {
        if ("log4j".equalsIgnoreCase(LOGGER_IMPL)) {
            return NativeLogger.getLogger(clazz);
        }

        return CustomLogger.getCustomLogger(clazz);
        
    }
}