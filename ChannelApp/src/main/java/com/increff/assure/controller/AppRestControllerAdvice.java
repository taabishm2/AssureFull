package com.increff.assure.controller;

import com.increff.assure.model.data.MessageData;
import com.increff.assure.service.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class AppRestControllerAdvice {

    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MessageData handle(ApiException e) {
        MessageData data = new MessageData();
        data.setMessage(e.getMessage());
        return data;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MessageData handle(MethodArgumentNotValidException e) {
        MessageData data = new MessageData();
        data.setMessage("Invalid Input ");
        System.out.println("Exception:MethodArgumentNotValidException-"+e.getMessage());
        return data;
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MessageData handle(HttpClientErrorException e) {
        MessageData data = new MessageData();
        data.setMessage(e.getResponseBodyAsString());
        e.printStackTrace();
        System.out.println("Exception:HttpClientErrorException-"+e.getMessage());
        return data;
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public MessageData handle(Throwable e) {
        MessageData data = new MessageData();
        data.setMessage("An unknown error has occurred" + e.getMessage());
        e.printStackTrace();
        System.out.println("Exception:INTERNAL_SERVER_ERROR-"+e.getMessage());
        return data;
    }
}