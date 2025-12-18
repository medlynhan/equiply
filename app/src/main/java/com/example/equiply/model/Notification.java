package com.example.equiply.model;

public class Notification {
    private String borrowId;
    private String title;
    private String message;
    private String timeStamp;

    public Notification(String title, String message, String timeStamp, String borrowId) {
        this.title = title;
        this.message = message;
        this.timeStamp = timeStamp;
        this.borrowId = borrowId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
    public String getBorrowId() { return borrowId; }
}
