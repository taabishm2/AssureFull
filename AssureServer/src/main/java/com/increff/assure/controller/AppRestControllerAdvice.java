package com.increff.assure.controller;

import com.increff.assure.service.ApiException;
import model.data.MessageData;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppRestControllerAdvice {

    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public MessageData handle(ApiException e) {
        MessageData data = new MessageData();
        data.setMessage(e.getMessage());
        System.out.println("Exception:ApiException-"+e.getMessage());
        return data;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MessageData handle(MethodArgumentNotValidException e) {
        MessageData data = new MessageData();
        data.setMessage("Invalid Input");
        System.out.println("Exception:MethodArgumentNotValidException-"+e.getMessage());
        return data;
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public MessageData handle(Throwable e) {
        MessageData data = new MessageData();
        data.setMessage("An unknown error has occurred in Assure Server- " + e);
        e.printStackTrace();
        System.out.println("Exception:InternalServerError-"+e.getMessage());
        return data;
    }
}