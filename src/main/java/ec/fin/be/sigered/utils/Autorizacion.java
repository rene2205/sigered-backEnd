/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.utils;

import ec.fin.be.comunes.excepciones.ServiceException;
import ec.fin.be.sigered.logger.LoggerSigered;

import java.io.File;

import java.io.IOException;

import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;


import org.apache.log4j.Logger;


/**
 *
 * @author rmoran
 */
public class Autorizacion {
    private static final Logger LOG = LoggerSigered.getLogger(Autorizacion.class);
    
     String token="";
     String userEncrpt="";
     String passEncrpt="";
     String ip="";
     
    

    
     public void obtenerCredencialesEncriptada(String urlKey, String usuario, String clave)throws ServiceException{
            
                
            SecureRandom key = new SecureRandom();
            byte[] bytes= new byte[16]; 
            key.nextBytes(bytes);
            String strkey = DatatypeConverter.printBase64Binary(bytes);
            PublicKey pbk=getPublic(urlKey);
            
                
                token = asymmetricEncrypt(strkey, pbk);
                String user= usuario;
                String pass= clave;
                userEncrpt=symmetricEncrypt(user, strkey);
                passEncrpt=symmetricEncrypt(pass, strkey);
            
    }
     
    public static PublicKey getPublic(String filename)throws ServiceException  {
        try{
            byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            
            return kf.generatePublic(spec);
            
        }catch(NoSuchAlgorithmException | InvalidKeySpecException | IOException ex){
            throw new ServiceException(ex);
        }
    }
	
    public static PrivateKey getPrivate(String filename) throws ServiceException {
        try{
            byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            
            return kf.generatePrivate(spec);
            
         }catch(NoSuchAlgorithmException | InvalidKeySpecException | IOException ex){
            throw new ServiceException(ex);
        }   
    }

    public static String asymmetricEncrypt(String data, PublicKey publicKey) throws  ServiceException {
        try{
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            
            return DatatypeConverter.printBase64Binary(cipher.doFinal(data.getBytes()));
            
        }catch(NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException ex){
                    throw new ServiceException(ex);
        }            
    }
    
    public static String symmetricEncrypt(String text, String secretKey) throws ServiceException {
        try{
            byte[] encryptText = text.getBytes();
            byte[] raw = DatatypeConverter.parseBase64Binary(secretKey);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, 0, 16, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        
            return DatatypeConverter.printBase64Binary(cipher.doFinal(encryptText));
            
        }catch(NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException ex){
                throw new ServiceException(ex);
        }            
    }
    
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserEncrpt() {
        return userEncrpt;
    }

    public void setUserEncrpt(String userEncrpt) {
        this.userEncrpt = userEncrpt;
    }

    public String getPassEncrpt() {
        return passEncrpt;
    }

    public void setPassEncrpt(String passEncrpt) {
        this.passEncrpt = passEncrpt;
    }
    
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}
