package com.financeops.chat.api;

import com.financeops.chat.application.ChatService;
import com.financeops.chat.domain.ChatMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chat;

    public ChatController(ChatService chat) {
        this.chat = chat;
    }

    @PostMapping
    ChatResponse reply(@RequestBody ChatRequest request) {
        return new ChatResponse(chat.reply(request.messages()));
    }

    record ChatRequest(List<ChatMessage> messages) {
    }

    record ChatResponse(String reply) {
    }
}
