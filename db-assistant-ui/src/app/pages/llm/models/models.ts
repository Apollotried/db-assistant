import { QueryResultDto } from '../../../services/models/query-result-dto';

export type SenderType = 'USER' | 'ASSISTANT';

export interface BaseChatMessage {
  id: number;
  sender: SenderType;
  sentAt: string;
  type: 'USER' | 'LLM' | 'QUERY';
}

// Case 1: User request (plain text)
export interface UserChatMessage extends BaseChatMessage {
  type: 'USER';
  content: string; // the natural language request from the user
}

// Case 2: LLM-generated chat message
export interface LlmChatMessage extends BaseChatMessage {
  type: 'LLM';
  content: string; // SQL or explanation
}

// Case 3: SQL execution result
export interface QueryChatMessage extends BaseChatMessage {
  type: 'QUERY';
  message: string; // summary like "✅ SELECT executed"
  result: QueryResultDto; // raw result so you can display rows/aggregate/etc.
}

// The unified type
export type ChatMessage = UserChatMessage | LlmChatMessage | QueryChatMessage;
