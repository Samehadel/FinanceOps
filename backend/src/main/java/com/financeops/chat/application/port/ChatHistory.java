package com.financeops.chat.application.port;

import com.financeops.chat.domain.ChatMessage;
import java.util.List;

public interface ChatHistory {
    void lock(String userId);
    List<ChatMessage> load(String userId);
    void append(String userId, ChatMessage message);
    void clear(String userId);
}
