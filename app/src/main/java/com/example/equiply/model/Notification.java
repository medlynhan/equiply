package com.example.equiply.model;

public class Notification {
    private String title;
    private String message;
    private String timeStamp;

    public Notification(String title, String message, String timeStamp) {
        this.title = title;
        this.message = message;
        this.timeStamp = timeStamp;
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
}
