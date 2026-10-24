/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


import org.apache.log4j.Logger;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.logger.LoggerSigered;

/**
 *
 * @author wochoa
 */
public final class UtilPropertiesArquetipo {

    private static UtilPropertiesArquetipo utilProperties;
    private Properties prop;
    private static final Logger LOG = LoggerSigered.getLogger(UtilPropertiesArquetipo.class);

    public UtilPropertiesArquetipo() throws ServiceException {
        String dir = System.getProperty("SigeredProperties");
        
        try (InputStream in = new FileInputStream(dir)) {

            this.prop = new Properties();
            prop.load(in);
        } catch (IOException ex) {
            LOG.error("Error al cargar archivo de propiedades", ex);
            throw new ServiceException("Archivo de configuracion de propiedades no encontrado", ex);
        } 
    }

    public static final UtilPropertiesArquetipo getIntances() {
        try {
            utilProperties = new UtilPropertiesArquetipo();
        } catch (ServiceException ex) {
            LOG.error("Error al cargar archivo de propiedades " + ex.getMensajeTecnico(), ex);
        }
        return utilProperties;
    }

    public String obtenerParametro(String nombreProps) {
        return prop.getProperty(nombreProps);
    }

    public Map<String, String> obtenerParametrosPropiedades(String parametro) {
        Map<String, String> propiedadesMapS = new HashMap<>();
        Set<Object> keys = prop.keySet();
        for (Object key : keys) {
            if (((String) key).contains(parametro)) {
                propiedadesMapS.put((String) key, prop.getProperty((String) key));
            }
        }
        return propiedadesMapS;
    }
    
    public String getProperties(String namePropertie) {
    	return System.getProperty(namePropertie);
    }
}
