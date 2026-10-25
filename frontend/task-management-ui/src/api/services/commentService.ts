import { COMMENT_ENDPOINTS } from "../endpoints/commentEndpoints";
import { apiService } from "../http/apiService";
import type { AddCommentRequest, CommentResponseDTO, UpdateCommentRequest } from "../models/commentModels";


export const commentService = {

  addComment: (
    taskId: number,
    request: AddCommentRequest
  ) =>
    apiService.post<CommentResponseDTO>(
      COMMENT_ENDPOINTS.ADD_COMMENT(taskId),
      request
    ),

  getCommentsByTask: (taskId: number) =>
    apiService.get<CommentResponseDTO[]>(
      COMMENT_ENDPOINTS.GET_COMMENTS_BY_TASK(taskId)
    ),

  getCommentById: (commentId: number) =>
    apiService.get<CommentResponseDTO>(
      COMMENT_ENDPOINTS.GET_COMMENT_BY_ID(commentId)
    ),

  getMyComments: () =>
    apiService.get<CommentResponseDTO[]>(
      COMMENT_ENDPOINTS.MY_COMMENTS
    ),

  updateComment: (
    commentId: number,
    request: UpdateCommentRequest
  ) =>
    apiService.put<CommentResponseDTO>(
      COMMENT_ENDPOINTS.UPDATE_COMMENT(commentId),
      request
    ),

  deleteComment: (commentId: number) =>
    apiService.delete<void>(
      COMMENT_ENDPOINTS.DELETE_COMMENT(commentId)
    ),

};