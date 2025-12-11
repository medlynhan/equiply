package com.example.equiply.model;

public class BorrowHistory {
    private String id;
    private String userId;
    private String toolId;
    private String toolName;
    private String reason;
    private String status; // Dipinjam / Menunggu Konfirmasi / Dikembalikan
    private String requestDate;
    private String borrowDate;
    private String returnDate;
    private String initialCondition; // keadaan barang awal", Baik / Rusak
    private String finalCondition; // keadaan barang terakhir, Baik / Rusak
    private String imageUrl;
    private long createdAt;

    public BorrowHistory() {}
    public BorrowHistory(String userId, String toolId, String toolName, String reason,
                         String status, String imageUrl, String requestDate, String borrowDate,
                         String returnDate, String initialCondition, long createdAt) {
        this.userId = userId;
        this.toolId = toolId;
        this.toolName = toolName;
        this.reason = reason;
        this.status = status;
        this.imageUrl = imageUrl;
        this.requestDate = requestDate;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.initialCondition = initialCondition;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToolId() {
        return toolId;
    }

    public void setToolId(String toolId) {
        this.toolId = toolId;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getInitialCondition() {
        return initialCondition;
    }

    public void setInitialCondition(String initialCondition) { this.initialCondition = initialCondition; }

    public String getFinalCondition() {
        return finalCondition;
    }

    public void setFinalCondition(String finalCondition) {
        this.finalCondition = finalCondition;
    }

    public long getCreatedAt() { return createdAt; }

    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public String getRequestDate() { return requestDate; }

    public void setRequestDate(String requestDate) { this.requestDate = requestDate; }
}
