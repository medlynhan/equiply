package com.example.equiply.model;

public class BorrowRequest {
    private String id;
    private String toolId;
    private String toolName;
    private String userId;
    private String borrowDate;
    private String returnDate;
    private String reason; //alesan pinjam
    private String status; // pending, approved, rejected
    private long createdAt;

    public BorrowRequest() {
    }

    public BorrowRequest(String id, String toolId, String toolName, String userId, String borrowDate, String returnDate, String reason, String status, long createdAt) {
        this.id = id;
        this.toolId = toolId;
        this.toolName = toolName;
        this.userId = userId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }
    public String getToolId() {
        return toolId;
    }
    public String getToolName() {
        return toolName;
    }
    public String getUserId() {
        return userId;
    }
    public String getBorrowDate() {
        return borrowDate;
    }
    public String getReturnDate() {
        return returnDate;
    }
    public String getReason() {
        return reason;
    }
    public String getStatus() {
        return status;
    }
    public long getCreatedAt() {
        return createdAt;
    }
}