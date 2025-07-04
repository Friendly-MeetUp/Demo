//package com.defect.defectTracker.exceptionHandler;
//
//import com.defect.defectTracker.utils.StandardResponse;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//public class GlobalExceptionHandler extends RuntimeException {
//    public GlobalExceptionHandler(String message) {
//        super(message);
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<StandardResponse> handleException(Exception ex) {
//        return new ResponseEntity<>(
//                new StandardResponse("failure", ex.getMessage(), null, 4000),
//                HttpStatus.BAD_REQUEST
//        );
//    }
//}
//
//