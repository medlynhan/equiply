package com.example.equiply.model;

public class Tool {
    private String id;
    private String name;
    private String description;
    private String status;
    private String toolStatus;
    private String returnDate;
    private String imageUrl;

    public Tool() {}

    public Tool(String id, String name, String description, String status, String imageUrl, String toolStatus) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.imageUrl = imageUrl;
        this.toolStatus = toolStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getToolStatus() {
        return toolStatus;
    }

    public void setToolStatus(String toolStatus) {
        this.toolStatus = toolStatus;
    }

    public String getReturnDate() { return returnDate; }

    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }
}
