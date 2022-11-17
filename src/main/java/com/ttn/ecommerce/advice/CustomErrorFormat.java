package com.ttn.ecommerce.advice;

import lombok.Data;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;


@Data
public class CustomErrorFormat {
    private LocalDateTime timeStamp;
    private String message;
    private String description;
    private HttpStatus status;
    public CustomErrorFormat(LocalDateTime timeStamp, String message, String description, HttpStatus status) {
        this.timeStamp = timeStamp;
        this.message = message;
        this.description = description;
        this.status = status;
    }
}
