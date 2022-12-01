package com.ttn.ecommerce.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;


@Data
public class CustomErrorFormat {
    private LocalDateTime timeStamp;
    private String message;
    private String path;
    private HttpStatus status;
    public CustomErrorFormat(LocalDateTime timeStamp, String message, String path, HttpStatus status) {
        this.timeStamp = timeStamp;
        this.message = message;
        this.path = path;
        this.status = status;
    }
}
