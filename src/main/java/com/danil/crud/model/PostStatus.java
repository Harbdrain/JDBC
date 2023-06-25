package com.danil.crud.model;

public enum PostStatus {
    ACTIVE(1), UNDER_REVIEW(2), DELETED(3);

    private int code;

    PostStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public static PostStatus getStatus(String status) {
        switch (status) {
            case "ACTIVE":
                return ACTIVE;
            case "UNDER_REVIEW":
                return UNDER_REVIEW;
            case "DELETED":
                return DELETED;
            default:
                return null;
        }
    }
}
