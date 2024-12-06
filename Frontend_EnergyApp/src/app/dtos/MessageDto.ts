export interface MessageDto {
  senderId: string;
  receiverId: string;
  content: string;
  seen: boolean;
  typing?: boolean;
}
