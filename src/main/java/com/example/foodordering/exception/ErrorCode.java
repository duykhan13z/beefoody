package com.example.foodordering.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {

    USER_EXISTED(1001, "User already existed", HttpStatus.INTERNAL_SERVER_ERROR),;


    int code;
    String message;
    HttpStatusCode statusCode;
}
