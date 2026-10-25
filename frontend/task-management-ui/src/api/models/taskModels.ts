export type TaskStatus =
  | "CREATED"
  | "ASSIGNED"
  | "IN_PROGRESS"
  | "COMPLETED"
  | "CANCELLED";

export interface TaskResponseDTO {
  id: number;
  title: string;
  description: string;
  createdById: number;
  assignedToId: number | null;
  status: TaskStatus;
  createdAt: string;
  updatedAt: string;
  dueDate: string | null;
}

export interface CreateTaskRequest {
  title: string;
  description: string;
  dueDate?: string;
}

export interface UpdateTaskRequest {
  title: string;
  description: string;
  dueDate?: string;
}

export interface AssignTaskRequest {
  assignedToId: number;
}

export interface ReassignTaskRequest {
  assignedToId: number;
}

export interface UpdateTaskStatusRequest {
  status: TaskStatus;
}