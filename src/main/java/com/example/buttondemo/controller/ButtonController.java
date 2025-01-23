package com.example.buttondemo.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ButtonController {

    private final ApplicationContext applicationContext;

    public ButtonController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/api/check")
    @ResponseBody
    public ResponseEntity<String> checkRequest() {
        return ResponseEntity.ok("İstek başarıyla alındı!");
    }

    @PostMapping("/api/shutdown")
    @ResponseBody
    public ResponseEntity<String> shutdown() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                SpringApplication.exit(applicationContext, () -> 0);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        return ResponseEntity.ok("Uygulama kapatılıyor...");
    }

    @PostMapping("/api/message")
    @ResponseBody
    public ResponseEntity<MessageResponse> sendMessage(@RequestBody MessageRequest request) {
        String response = "Gelen mesaj: " + request.getMessage();
        return ResponseEntity.ok(new MessageResponse(response));
    }
}

class MessageRequest {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

class MessageResponse {
    private String response;

    public MessageResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
} 