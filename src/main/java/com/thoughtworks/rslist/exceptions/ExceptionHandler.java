package com.thoughtworks.rslist.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {
    public static Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @org.springframework.web.bind.annotation.ExceptionHandler({IndexOutOfBoundsException.class,
            InvalidIndexException.class})
    public ResponseEntity<CommentError> handleIndexOutOfBoundsException(Exception ex) {
        CommentError commentError = new CommentError();
        commentError.setError(ex.getMessage());
        logger.error("invalid request param");
        return ResponseEntity.status(400).body(commentError);
    }
}
