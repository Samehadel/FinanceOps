package com.financeops.chat.persistence;

import com.financeops.chat.application.port.ChatHistory;
import com.financeops.chat.domain.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
class JdbcChatHistory implements ChatHistory {
    private final JdbcTemplate jdbc;

    public void lock(String userId) {
        jdbc.queryForObject("SELECT id FROM app_user WHERE id = ? FOR UPDATE", UUID.class, UUID.fromString(userId));
    }

    public List<ChatMessage> load(String userId) {
        return jdbc.query("SELECT role, content FROM chat_message WHERE user_id = ? ORDER BY id",
            (row, index) -> new ChatMessage(row.getString("role"), row.getString("content")), UUID.fromString(userId));
    }

    public void append(String userId, ChatMessage message) {
        jdbc.update("INSERT INTO chat_message (user_id, role, content) VALUES (?, ?, ?)",
            UUID.fromString(userId), message.role(), message.content());
    }

    public void clear(String userId) {
        jdbc.update("DELETE FROM chat_message WHERE user_id = ?", UUID.fromString(userId));
    }
}
