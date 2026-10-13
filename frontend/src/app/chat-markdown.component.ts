import { Component, computed, input, ViewEncapsulation } from "@angular/core";
import { Marked } from "marked";

const markdown = new Marked({
  gfm: true,
  breaks: true,
  renderer: {
    // Model responses are text: do not interpret embedded HTML as markup.
    html({ text }) {
      return text
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;");
    },
  },
});

@Component({
  selector: "app-chat-markdown",
  template: `<div [innerHTML]="html()"></div>`,
  styleUrl: "./chat-markdown.component.css",
  encapsulation: ViewEncapsulation.None,
})
export class ChatMarkdownComponent {
  content = input.required<string>();
  // Keep Angular's HTML sanitization enabled for generated links and markup.
  html = computed(() => markdown.parse(this.content(), { async: false }));
}
