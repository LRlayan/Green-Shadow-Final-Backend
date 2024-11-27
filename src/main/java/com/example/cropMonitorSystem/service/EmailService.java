package com.example.cropMonitorSystem.service;

public interface EmailService {
    void sendEmail(String to, String subject, String text);
}
