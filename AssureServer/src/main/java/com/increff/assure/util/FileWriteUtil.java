package com.increff.assure.util;

import com.increff.assure.service.ApiException;
import model.data.MessageData;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileWriteUtil {
    public static String writeErrorsToFile(String filename, List<MessageData> messageDataList) throws ApiException {
        FileWriter fileWriter;
        String fileUrl = "C://Users//Tabish//Documents//Repos//Increff//AssureServer//src//main//resources//output//"+filename+".txt";
        try{
            fileWriter = new FileWriter(fileUrl);
            for(MessageData message:messageDataList)
                fileWriter.write(message.getMessage());
            fileWriter.close();
        } catch(IOException e){
            throw new ApiException("Could not generate Error File.");
        }
        return fileUrl;
    }
}
