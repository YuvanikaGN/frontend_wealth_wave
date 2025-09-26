package com.simats.wealth_wave.responses;

public class LoginResponse {
    private String status;
    private String message;
    private String fullName;
    private int userId;
    private String email;
    private String mobile; // ✅ add this

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public String getFullName() { return fullName; }
    public int getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getMobile() { return mobile; }  // ✅ real getter

    public void setStatus(String status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setEmail(String email) { this.email = email; }
    public void setMobile(String mobile) { this.mobile = mobile; } // ✅ real setter
}
