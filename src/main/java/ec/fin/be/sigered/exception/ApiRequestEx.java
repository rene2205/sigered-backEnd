/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.exception;

/**
 *
 * @author jorquera
 */
public class ApiRequestEx extends RuntimeException{

    public ApiRequestEx(String message) {
        super(message);
    }

    public ApiRequestEx(String message, Throwable cause) {
        super(message, cause);
    }
    
    
    
    
    
}
