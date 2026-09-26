package com.workouttracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FeedbackRequestDTO {

    @NotBlank(message = "Please write a message")
    @Size(max = 2000, message = "Message must not exceed 2000 characters")
    private String message;

    @Size(max = 20)
    private String appVersion;

    @Size(max = 200)
    private String page;

    @Size(max = 300)
    private String device;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String appVersion) { this.appVersion = appVersion; }

    public String getPage() { return page; }
    public void setPage(String page) { this.page = page; }

    public String getDevice() { return device; }
    public void setDevice(String device) { this.device = device; }
}
