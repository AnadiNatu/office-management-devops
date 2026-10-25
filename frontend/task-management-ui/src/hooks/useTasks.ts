import { useMemo, useCallback } from "react";
import  { taskService } from "../api";
import type { TaskResponseDTO, CreateTaskRequest, UpdateTaskRequest, AssignTaskRequest, ReassignTaskRequest, UpdateTaskStatusRequest } from "../api/models/taskModels";
import { can } from "../auth/permissions";
import { useAuth } from "./useAuth";
import  { useAsync } from "./useCommon";

export function useTasks() {

  const { user } = useAuth();

  const canViewAll =
    can(user?.role, "TASK_VIEW_ALL");


  const {
    data,
    setData,
    loading,
    error,
    reload,
  } = useAsync<TaskResponseDTO[]>(
    () =>
      canViewAll
        ? taskService.getAllTasks()
        : taskService.getMyTasks(),
    [canViewAll]
  );


  const tasks = useMemo(
    () => data ?? [],
    [data]
  );


  const replaceTask = useCallback(
    (updated: TaskResponseDTO) => {

      setData((previous) => {

        const current =
          previous ?? [];

        const exists =
          current.some(
            (task) =>
              task.id === updated.id
          );

        if (!exists) {
          return current;
        }

        return current.map(
          (task) =>
            task.id === updated.id
              ? updated
              : task
        );
      });
    },
    [setData]
  );


  const createTask = useCallback(
    async (
      request: CreateTaskRequest
    ) => {

      const created =
        await taskService.createTask(
          request
        );

      setData((previous) => {

        const current =
          previous ?? [];

        const alreadyExists =
          current.some(
            (task) =>
              task.id === created.id
          );

        if (alreadyExists) {
          return current;
        }

        return [
          created,
          ...current,
        ];
      });

      return created;
    },
    [setData]
  );


  const updateTask = useCallback(
    async (
      taskId: number,
      request: UpdateTaskRequest
    ) => {

      const updated =
        await taskService.updateTask(
          taskId,
          request
        );

      replaceTask(updated);

      return updated;
    },
    [replaceTask]
  );


  const assignTask = useCallback(
    async (
      taskId: number,
      request: AssignTaskRequest
    ) => {

      const updated =
        await taskService.assignTask(
          taskId,
          request
        );

      replaceTask(updated);

      return updated;
    },
    [replaceTask]
  );


  const reassignTask = useCallback(
    async (
      taskId: number,
      request: ReassignTaskRequest
    ) => {

      const updated =
        await taskService.reassignTask(
          taskId,
          request
        );

      replaceTask(updated);

      return updated;
    },
    [replaceTask]
  );


  const updateTaskStatus =
    useCallback(
      async (
        taskId: number,
        request: UpdateTaskStatusRequest
      ) => {

        const updated =
          await taskService.updateTaskStatus(
            taskId,
            request
          );

        replaceTask(updated);

        return updated;
      },
      [replaceTask]
    );


  const deleteTask =
    useCallback(
      async (taskId: number) => {

        await taskService.deleteTask(
          taskId
        );

        setData((previous) =>
          (previous ?? []).filter(
            (task) =>
              task.id !== taskId
          )
        );
      },
      [setData]
    );


  return {
    tasks,
    loading,
    error,
    reload,
    canViewAll,

    createTask,
    updateTask,
    assignTask,
    reassignTask,
    updateTaskStatus,
    deleteTask,
  };
}


export function useTask(
  taskId: number | undefined
) {

  const {
    data,
    setData,
    loading,
    error,
    reload,
  } =
    useAsync<TaskResponseDTO | null>(
      () =>
        taskId != null
          ? taskService.getTaskById(
              taskId
            )
          : Promise.resolve(null),
      [taskId],
      null
    );


  return {
    task: data,
    setTask: setData,
    loading,
    error,
    reload,
  };
}