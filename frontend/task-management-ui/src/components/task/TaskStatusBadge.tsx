import type { TaskStatus } from "../../api/models/taskModels";
import { Badge, type BadgeTone } from "../../ui/Badge";
// import { humanizeEnum } from "../../utils/formatUtils";



export const TASK_STATUS_OPTIONS = [
  {
    label: "Created",
    value: "CREATED",
  },
  {
    label: "Assigned",
    value: "ASSIGNED",
  },
  {
    label: "In Progress",
    value: "IN_PROGRESS",
  },
  {
    label: "Completed",
    value: "COMPLETED",
  },
  {
    label: "Cancelled",
    value: "CANCELLED",
  },
] as const;


export function getTaskStatusOptions(
  status: TaskStatus
) {

  switch (status) {

    case "CREATED":
      return [
        {
          label: "Created",
          value: "CREATED",
        },
        {
          label: "Assigned",
          value: "ASSIGNED",
        },
        {
          label: "Cancelled",
          value: "CANCELLED",
        },
      ];

    case "ASSIGNED":
      return [
        {
          label: "Assigned",
          value: "ASSIGNED",
        },
        {
          label: "In Progress",
          value: "IN_PROGRESS",
        },
        {
          label: "Cancelled",
          value: "CANCELLED",
        },
      ];

    case "IN_PROGRESS":
      return [
        {
          label: "In Progress",
          value: "IN_PROGRESS",
        },
        {
          label: "Completed",
          value: "COMPLETED",
        },
        {
          label: "Cancelled",
          value: "CANCELLED",
        },
      ];

    case "COMPLETED":
      return [
        {
          label: "Completed",
          value: "COMPLETED",
        },
      ];

    case "CANCELLED":
      return [
        {
          label: "Cancelled",
          value: "CANCELLED",
        },
      ];

    default:
      return TASK_STATUS_OPTIONS;
  }
}


interface TaskStatusBadgeProps {
  status: TaskStatus;
  size?: "sm" | "md";
}


const TaskStatusBadge: React.FC<
  TaskStatusBadgeProps
> = ({
  status,
  size = "md",
}) => {

  const label =
    TASK_STATUS_OPTIONS.find(
      (option) =>
        option.value === status
    )?.label ??
    status;


  const tone =
    status === "COMPLETED"
      ? "green"
      : status === "CANCELLED"
        ? "red"
        : status === "IN_PROGRESS"
          ? "blue"
          : status === "ASSIGNED"
            ? "purple"
            : "gray";


  return (
    <Badge
      tone={tone as BadgeTone}
      size={size}
    >
      {label}
    </Badge>
  );
};


export { TaskStatusBadge };

export default TaskStatusBadge;