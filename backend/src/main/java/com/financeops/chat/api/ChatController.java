package com.financeops.chat.api;

import com.financeops.chat.application.ChatService;
import com.financeops.chat.domain.ChatMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    List<ChatMessage> history() {
        return chat.history();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void clear() {
        chat.clear();
    }

    @PostMapping
    ChatResponse reply(@RequestBody ChatRequest request) {
        return new ChatResponse(chat.reply(request.content()));
    }

    record ChatRequest(String content) {
    }

    record ChatResponse(String reply) {
    }
}
