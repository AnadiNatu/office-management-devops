import  { useMemo, useCallback } from "react";
import  { commentService } from "../api";
import type { CommentResponseDTO, AddCommentRequest, UpdateCommentRequest } from "../api/models/commentModels";
import  { useAsync } from "./useCommon";

export function useTaskComments(
  taskId: number | undefined
) {

  const {
    data,
    setData,
    loading,
    error,
    reload,
  } =
    useAsync<CommentResponseDTO[]>(
      () =>
        taskId != null
          ? commentService.getCommentsByTask(
              taskId
            )
          : Promise.resolve([]),
      [taskId]
    );

  const comments =
    useMemo(
      () => data ?? [],
      [data]
    );

  const addComment =
    useCallback(
      async (
        request: AddCommentRequest
      ) => {

        if (
          taskId == null ||
          !Number.isInteger(taskId) ||
          taskId <= 0
        ) {
          throw new Error(
            "No valid task selected"
          );
        }

        const created =
          await commentService.addComment(
            taskId,
            {
              comment:
                request.comment.trim(),
            }
          );

        setData((previous) => {

          const current =
            previous ?? [];

          /*
           * Prevent accidental duplicate
           * insertion if the caller retries.
           */
          if (
            current.some(
              (item) =>
                item.id ===
                created.id
            )
          ) {
            return current;
          }

          return [
            ...current,
            created,
          ];
        });

        return created;
      },
      [taskId, setData]
    );

  const updateComment =
    useCallback(
      async (
        commentId: number,
        request: UpdateCommentRequest
      ) => {

        const updated =
          await commentService.updateComment(
            commentId,
            {
              comment:
                request.comment.trim(),
            }
          );

        setData(
          (previous) =>
            (previous ?? []).map(
              (comment) =>
                comment.id ===
                commentId
                  ? updated
                  : comment
            )
        );

        return updated;
      },
      [setData]
    );

  const deleteComment =
    useCallback(
      async (
        commentId: number
      ) => {

        await commentService.deleteComment(
          commentId
        );

        setData(
          (previous) =>
            (previous ?? []).filter(
              (comment) =>
                comment.id !==
                commentId
            )
        );
      },
      [setData]
    );

  return {
    comments,
    loading,
    error,
    reload,
    addComment,
    updateComment,
    deleteComment,
  };
}

export function useMyComments() {

  const {
    data,
    setData,
    loading,
    error,
    reload,
  } =
    useAsync<CommentResponseDTO[]>(
      () =>
        commentService.getMyComments(),
      []
    );

  const comments =
    useMemo(
      () => data ?? [],
      [data]
    );

  const updateComment =
    useCallback(
      async (
        commentId: number,
        request: UpdateCommentRequest
      ) => {

        const updated =
          await commentService.updateComment(
            commentId,
            {
              comment:
                request.comment.trim(),
            }
          );

        setData(
          (previous) =>
            (previous ?? []).map(
              (comment) =>
                comment.id ===
                commentId
                  ? updated
                  : comment
            )
        );

        return updated;
      },
      [setData]
    );

  const deleteComment =
    useCallback(
      async (
        commentId: number
      ) => {

        await commentService.deleteComment(
          commentId
        );

        setData(
          (previous) =>
            (previous ?? []).filter(
              (comment) =>
                comment.id !==
                commentId
            )
        );
      },
      [setData]
    );

  return {
    comments,
    loading,
    error,
    reload,
    updateComment,
    deleteComment,
  };
}