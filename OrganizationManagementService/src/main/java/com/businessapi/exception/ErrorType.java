package com.businessapi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorType
{
    INVALID_TOKEN(9010, "Invalid Token", HttpStatus.BAD_REQUEST),
    BAD_REQUEST_ERROR(9001, "Bad Request", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(9000, "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED(9017, "Unauthorized", HttpStatus.UNAUTHORIZED),
    DEPARTMENT_NOT_FOUND(9100, "Department not found", HttpStatus.BAD_REQUEST),
    MANAGER_NOT_FOUND( 9101, "Manager not found",  HttpStatus.BAD_REQUEST ),
    EMPLOYEE_NOT_FOUND(9102,"Employee not found", HttpStatus.BAD_REQUEST );

    private Integer code;
    private String message;
    private HttpStatus httpStatus;
}
