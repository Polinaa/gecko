package com.example.gecko.exception;

public class RecordNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Record not found";

    public RecordNotFoundException() {
        super(MESSAGE);
    }
}
