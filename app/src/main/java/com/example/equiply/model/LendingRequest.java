package com.example.equiply.model;

public class LendingRequest {

    private String requestId;
    private String toolId;
    private String toolName;
    private String userId;
    private String condition; // "Baik", "Rusak"
    private String returnDate;
    private String proofImage; // URL from Firebase Storage
    private String status; // "pending", "approved", "rejected"
    private long timestamp;

    public LendingRequest() {
    }

    public LendingRequest(String requestId, String toolId, String toolName, String userId, String condition, String returnDate, String proofImage, String status, long timestamp) {
        this.requestId = requestId;
        this.toolId = toolId;
        this.toolName = toolName;
        this.userId = userId;
        this.condition = condition;
        this.returnDate = returnDate;
        this.proofImage = proofImage;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getRequestId() { return requestId; }
    public String getToolId() { return toolId; }
    public String getToolName() { return toolName; }
    public String getUserId() { return userId; }
    public String getCondition() { return condition; }
    public String getReturnDate() { return returnDate; }
    public String getProofImage() { return proofImage; }
    public String getStatus() { return status; }
    public long getTimestamp() { return timestamp; }
}
