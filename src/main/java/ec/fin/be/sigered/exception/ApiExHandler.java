/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.fin.be.sigered.exception;

import java.sql.SQLException;
import java.time.ZoneId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.ZonedDateTime;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 *
 * @author jorquera
 */
@ControllerAdvice
public class ApiExHandler {

    @ExceptionHandler(value = {ApiRequestEx.class})
    public ResponseEntity<Object> handleApiRequestException(ApiRequestEx e) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        ApiEx apiEx = new ApiEx(e.getMessage(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z")));

        return new ResponseEntity<>(apiEx, badRequest);

    }
    @ExceptionHandler({SQLException.class, DataAccessException.class})
    public ResponseEntity<Object>  databaseError(ApiRequestEx e) {
        
          HttpStatus badRequest = HttpStatus.BAD_REQUEST;
         ApiEx apiEx = new ApiEx(e.getMessage(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z")));

        return new ResponseEntity<>(apiEx, badRequest);
    }

}
