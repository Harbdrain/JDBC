package com.danil.crud.model;

public enum WriterStatus {
    ACTIVE(1), DELETED(2);

    private int code;

    WriterStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public static WriterStatus getStatus(String status) {
        switch (status) {
            case "ACTIVE":
                return ACTIVE;
            case "DELETED":
                return DELETED;
            default:
                return null;
        }
    }
}
