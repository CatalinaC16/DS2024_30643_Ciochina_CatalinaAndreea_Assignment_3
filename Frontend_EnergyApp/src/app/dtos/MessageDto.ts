export interface MessageDto {
  id?: string;
  senderId: string;
  receiverId: string;
  content: string;
  seen: boolean;
  typing?: boolean;
}
