/** Text-only finance conversations through a direct OpenAI model call. */
@org.springframework.modulith.ApplicationModule(
    displayName = "AI Chat",
    allowedDependencies = {"auth :: identity", "shared :: errors", "billing :: queries"}
)
package com.financeops.chat;
