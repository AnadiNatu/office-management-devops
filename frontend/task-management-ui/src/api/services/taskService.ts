import { toLocalDateTime } from "../../utils/dateUtils";
import { TASK_ENDPOINTS } from "../endpoints/taskEndpoints";
import { apiService } from "../http/apiService";
import type { CreateTaskRequest, TaskResponseDTO, AssignTaskRequest, ReassignTaskRequest, UpdateTaskStatusRequest, UpdateTaskRequest } from "../models/taskModels";

export const taskService = {

  createTask: (
    request: CreateTaskRequest
  ): Promise<TaskResponseDTO> => {

    const payload: CreateTaskRequest = {
      ...request,
      dueDate: toLocalDateTime(request.dueDate),
    };

    return apiService.post<TaskResponseDTO>(
      TASK_ENDPOINTS.CREATE,
      payload
    );
  },


  getTaskById: (
    taskId: number
  ): Promise<TaskResponseDTO> => {

    return apiService.get<TaskResponseDTO>(
      TASK_ENDPOINTS.GET_BY_ID(taskId)
    );
  },


  getMyTasks: (): Promise<TaskResponseDTO[]> => {

    return apiService.get<TaskResponseDTO[]>(
      TASK_ENDPOINTS.MY_TASKS
    );
  },


  getAllTasks: (): Promise<TaskResponseDTO[]> => {

    return apiService.get<TaskResponseDTO[]>(
      TASK_ENDPOINTS.ALL_TASKS
    );
  },


  assignTask: (
    taskId: number,
    request: AssignTaskRequest
  ): Promise<TaskResponseDTO> => {

    return apiService.put<TaskResponseDTO>(
      TASK_ENDPOINTS.ASSIGN(taskId),
      request
    );
  },


  reassignTask: (
    taskId: number,
    request: ReassignTaskRequest
  ): Promise<TaskResponseDTO> => {

    return apiService.put<TaskResponseDTO>(
      TASK_ENDPOINTS.REASSIGN(taskId),
      request
    );
  },


  updateTaskStatus: (
    taskId: number,
    request: UpdateTaskStatusRequest
  ): Promise<TaskResponseDTO> => {

    return apiService.put<TaskResponseDTO>(
      TASK_ENDPOINTS.UPDATE_STATUS(taskId),
      request
    );
  },


  updateTask: (
    taskId: number,
    request: UpdateTaskRequest
  ): Promise<TaskResponseDTO> => {

    const payload: UpdateTaskRequest = {
      ...request,
      dueDate: toLocalDateTime(request.dueDate),
    };

    return apiService.put<TaskResponseDTO>(
      TASK_ENDPOINTS.UPDATE(taskId),
      payload
    );
  },


  deleteTask: (
    taskId: number
  ): Promise<void> => {

    return apiService.delete<void>(
      TASK_ENDPOINTS.DELETE(taskId)
    );
  },
};