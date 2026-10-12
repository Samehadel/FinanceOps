package com.financeops.chat.domain;

/** Conversation input; only user and assistant roles are accepted by the application. */
public record ChatMessage(String role, String content) {
}
