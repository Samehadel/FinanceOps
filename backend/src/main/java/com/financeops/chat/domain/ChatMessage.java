package com.financeops.chat.domain;

/** A saved conversation message with a user or assistant role. */
public record ChatMessage(String role, String content) {
}
