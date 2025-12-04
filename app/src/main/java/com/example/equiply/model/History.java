package com.example.equiply.model;

public class History {
    private String id;
    private String userId;
    private String toolId;
    private String borrowDate;
    private String returnDate;
    private String initialToolStatus;//keadaan barang awal", baik / rusak
    private String finalToolStatus;//keadaan barang terakhir, baik / rusak

    public History(){}
    public History(String id, String userId, String toolId, String borrowDate, String returnDate, String initialToolStatus) {
        this.id = id;
        this.userId = userId;
        this.toolId = toolId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.initialToolStatus = initialToolStatus;
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

    public String getInitialToolStatus() {
        return initialToolStatus;
    }

    public void setInitialToolStatus(String initialToolStatus) {
        this.initialToolStatus = initialToolStatus;
    }

    public String getFinalToolStatus() {
        return finalToolStatus;
    }

    public void setFinalToolStatus(String finalToolStatus) {
        this.finalToolStatus = finalToolStatus;
    }
}
