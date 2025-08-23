import {Component, signal, ViewChild} from '@angular/core';
import { FormsModule } from '@angular/forms';
import {ChatMessageResponseDto} from '../../../services/models/chat-message-response-dto';
import {SqlOperationsService} from '../../../services/services/sql-operations.service';
import {ChatMessageControllerService} from '../../../services/services/chat-message-controller.service';
import {DatabaseConnectionsService} from '../../../services/services/database-connections.service';
import {DbConnectionResponse} from '../../../services/models/db-connection-response';
import {NgStyle} from '@angular/common';
import {SqlRequest} from '../../../services/models/sql-request';
import {ChatMessage, LlmChatMessage, QueryChatMessage, UserChatMessage} from '../models/models';
import {Header} from '../../../components/header/header';


@Component({
  selector: 'app-llm-page',
  standalone: true,
  imports: [FormsModule, NgStyle, Header],
  templateUrl: './llm-page.html',
  styleUrl: './llm-page.scss'
})
export class LlmPage {

  messages = signal<ChatMessage[]>([]);
  newMessage = '';

  isLoading = signal(false);
  currentDataSource = signal<DbConnectionResponse  | null>(null);
  schema = signal<string | undefined>('');
  isSchemaOpen = false;

  constructor(
    private sqlService: SqlOperationsService,
    private chatService: ChatMessageControllerService,
    private dbService: DatabaseConnectionsService,
  ) {
  }

  ngOnInit() {
    this.loadActiveConnection();
  }



  loadActiveConnection() {
    this.dbService.getActiveConnection().subscribe({
      next: (connection) => {
        this.currentDataSource.set(connection);
        if(connection){
          this.loadMessages();
          this.loadSchema();
        }

      },
      error: (err) => {
        console.error('Failed to load active connection:', err);
        this.currentDataSource.set(null);
        this.schema.set('');
      }
    });
  }


  loadMessages() {
    if (!this.currentDataSource()) return;
    this.chatService.getMessages({ connectionId: this.currentDataSource()?.id }).subscribe(msgs => {
      const mapped = msgs.map(dto => this.mapToChatMessage(dto));
      this.messages.set(mapped);
      setTimeout(() => {
        const chatBody = document.querySelector('.card-body');
        chatBody?.scrollTo({ top: chatBody.scrollHeight, behavior: 'smooth' });
      }, 50);
    });
  }

  sendMessage(){
    if (!this.newMessage.trim() || !this.currentDataSource) return;

    this.isLoading.set(true);

    this.messages.update(current => [
      ...current,
      this.mapToChatMessage({
        id: 0,
        content: this.newMessage,
        sender: 'USER',
        sentAt: new Date().toISOString()
      })
    ]);
    const userRequest = this.newMessage;
    this.newMessage = '';

    this.sqlService.generateSql({
      body: userRequest
    }).subscribe({
      next: () => {
        this.loadMessages();
        this.isLoading.set(false);

      },
      error: err => {
        console.log(err);
        this.isLoading.set(false);
      }
    })
  }

  clearChat(){
    if (!this.currentDataSource) return;

    this.chatService.clearChat().subscribe({
      next: () => {
        this.messages.set([]);
      }
    })
  }

  handleEnter(event: KeyboardEvent) {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault(); // prevent new line
      this.sendMessage();
    }
  }

  loadSchema() {
    this.dbService.getSchema().subscribe({
      next: (res) => {
        this.schema.set(res.schema);
      },
      error: (err) => {
        console.error('Failed to load schema:', err);
        this.schema.set(''); // clear schema if failed
      }
    });
  }


  showSchema() {
    this.isSchemaOpen = true;
  }

  closeSchema(){
    this.isSchemaOpen = false;
  }



  executeSql(sql: string) {
    this.isLoading.set(true);

    const sqlRequest: SqlRequest = { sql };

    this.sqlService.executeSql({ body: sqlRequest }).subscribe({
      next: (result) => {
        this.isLoading.set(false);

        if (result.success) {
          // Push a QUERY type message with full result
          const queryMessage = this.mapToChatMessage(result);

          this.messages.update(msgs => [
            ...msgs,
            queryMessage
          ]);

        } else {
          // Failure message
          this.messages.update(msgs => [
            ...msgs,
            {
              id: Date.now(),
              sender: 'ASSISTANT',
              sentAt: new Date().toISOString(),
              type: 'LLM',
              content: `❌ Execution failed: ${result.message}`
            }
          ]);
        }
      },
      error: (err) => {
        this.isLoading.set(false);
        console.error('Failed to execute SQL:', err);

        this.messages.update(msgs => [
          ...msgs,
          {
            id: Date.now(),
            sender: 'ASSISTANT',
            sentAt: new Date().toISOString(),
            type: 'LLM',
            content: '❌ Execution failed due to network error'
          }
        ]);
      }
    });
  }





  editSql(content: string) {

  }

  tryAgain(msg: ChatMessageResponseDto) {

  }


  mapToChatMessage(dto: any): ChatMessage {
    // Case 1: User message
    if (dto.sender === 'USER') {
      return {
        id: dto.id ?? Date.now(),
        sender: 'USER',
        sentAt: dto.sentAt ?? new Date().toISOString(),
        type: 'USER',
        content: dto.content
      };
    }

    // Case 2: LLM message (from backend)
    if (dto.sender === 'ASSISTANT' && dto.content && !dto.result) {
      return {
        id: dto.id ?? Date.now(),
        sender: 'ASSISTANT',
        sentAt: dto.sentAt ?? new Date().toISOString(),
        type: 'LLM',
        content: dto.content
      };
    }

    // Case 3: Query result (from executeSql)
    if (dto.success !== undefined) {
      let message = '';
      if (dto.isAggregateQuery && dto.aggregateResult) {
        message = `${dto.aggregateResult.displayName}: ${dto.aggregateResult.value}`;
      } else if (dto.queryType === 'SELECT') {
        message = `Returned ${dto.rowCount} rows`;
      } else if (dto.affectedRows !== undefined) {
        message = `Affected ${dto.affectedRows} rows`;
      } else {
        message = dto.message || 'Query executed';
      }

      return {
        id: Date.now(),
        sender: 'ASSISTANT',
        sentAt: new Date().toISOString(),
        type: 'QUERY',
        message: message,
        result: dto
      };
    }

    // Fallback: treat as LLM message
    return {
      id: dto.id ?? Date.now(),
      sender: 'ASSISTANT',
      sentAt: dto.sentAt ?? new Date().toISOString(),
      type: 'LLM',
      content: dto.content || 'No content'
    };
  }


  // Type guard methods
  isUserMessage(msg: ChatMessage): msg is UserChatMessage {
    return msg.type === 'USER';
  }

  isLlmMessage(msg: ChatMessage): msg is LlmChatMessage {
    return msg.type === 'LLM';
  }

  isQueryMessage(msg: ChatMessage): msg is QueryChatMessage {
    return msg.type === 'QUERY';
  }

// Helper to get safe content
  getMessageContent(msg: ChatMessage): string {
    if (this.isUserMessage(msg) || this.isLlmMessage(msg)) {
      return msg.content;
    }
    return msg.message || '';
  }


  hasTableData(result: any): boolean {
    return !!result?.data?.length && !!result?.columns?.length;
  }

  getTableData(result: any): any[] {
    return result?.data || [];
  }

  getTableColumns(result: any): string[] {
    return result?.columns || [];
  }



}
