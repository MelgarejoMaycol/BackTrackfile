package com.TrackFile.app.web.controller;

import com.TrackFile.app.service.ChatbotService;
import com.TrackFile.app.web.dto.ChatRequest;
import com.TrackFile.app.web.dto.response.ChatResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin("*")
public class ChatController {

    private final ChatbotService chatbotService;

    public ChatController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping
    public ChatResponse responder(@RequestBody ChatRequest request) {
        return chatbotService.responder(request.getMensaje());
    }

    @GetMapping("/historial")
    public List<ChatResponse> historial() {
        return chatbotService.historial();
    }
}