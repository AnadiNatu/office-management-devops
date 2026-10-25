export interface CommentResponseDTO {
  id: number;
  taskId: number;

  commentedByUserId: number;

  commenterFirstName: string;
  commenterLastName: string;

  commenterRole: string;

  comment: string;

  createdAt?: string;
  updatedAt?: string;
}

export interface AddCommentRequest {
  comment: string;
}

export interface UpdateCommentRequest {
  comment: string;
}