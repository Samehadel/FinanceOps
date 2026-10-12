import {
  afterNextRender,
  Component,
  ElementRef,
  inject,
  Injector,
  signal,
  viewChild,
} from "@angular/core";
import { HttpErrorResponse } from "@angular/common/http";
import { FormsModule } from "@angular/forms";
import { Router } from "@angular/router";
import { ApiService, errorMessage } from "./api.service";
import { ChatMessage } from "./models";

@Component({
  selector: "app-chat",
  imports: [FormsModule],
  templateUrl: "./chat.component.html",
  styleUrl: "./chat.component.css",
})
export class ChatComponent {
  private api = inject(ApiService);
  private router = inject(Router);
  private injector = inject(Injector);
  private transcript = viewChild<ElementRef<HTMLDivElement>>("transcript");
  private composer = viewChild<ElementRef<HTMLTextAreaElement>>("composer");
  messages = signal<ChatMessage[]>([]);
  draft = signal("");
  sending = signal(false);
  error = signal("");

  constructor() {
    afterNextRender(() => this.composer()?.nativeElement.focus());
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === "Enter" && !event.shiftKey && !event.isComposing) {
      event.preventDefault();
      void this.submit();
    }
  }

  async submit(): Promise<void> {
    const content = this.draft().trim();
    if (this.sending() || !content || content.length > 4000) return;
    const previous = this.messages();
    const messages: ChatMessage[] = [...previous, { role: "user", content }];
    this.messages.set(messages);
    this.draft.set("");
    this.error.set("");
    this.sending.set(true);
    this.scrollToLatest();
    try {
      // Keep the most recent ten complete turns plus this question.
      const response = await this.api.chat(messages.slice(-21));
      this.messages.update((items) => [
        ...items,
        { role: "assistant", content: response.reply },
      ]);
    } catch (error) {
      this.messages.set(previous);
      this.draft.set(content);
      if (error instanceof HttpErrorResponse && error.status === 401) {
        this.api.profile.set(null);
        await this.router.navigate(["/login"]);
      } else {
        this.error.set(errorMessage(error));
      }
    } finally {
      this.sending.set(false);
      this.scrollToLatest();
      afterNextRender(() => this.composer()?.nativeElement.focus(), {
        injector: this.injector,
      });
    }
  }

  private scrollToLatest(): void {
    afterNextRender(
      () => {
        const element = this.transcript()?.nativeElement;
        if (element) element.scrollTop = element.scrollHeight;
      },
      { injector: this.injector },
    );
  }
}
