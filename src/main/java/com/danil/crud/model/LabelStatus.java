package com.danil.crud.model;

public enum LabelStatus {
    ACTIVE(1), DELETED(2);

    private int code;

    LabelStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public static LabelStatus getStatus(String status) {
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
