import { motion } from "framer-motion";
import { ArrowRight, User, Calendar } from "lucide-react";
import type { TaskResponseDTO } from "../../api/models/taskModels";
import { formatDate, isOverdue } from "../../utils/dateUtils";
import TaskStatusBadge from "./TaskStatusBadge";
import { Badge } from "../../ui/Badge";




const MotionDiv =
  motion.div as any;


interface TaskCardProps {
  task: TaskResponseDTO;
  onClick?: () => void;
}


export const TaskCard:
  React.FC<TaskCardProps> = ({
    task,
    onClick,
  }) => {

    const overdue =
      isOverdue(
        task.dueDate,
        task.status
      );


    return (
      <MotionDiv
        layout
        initial={{
          opacity: 0,
          y: 12,
        }}
        animate={{
          opacity: 1,
          y: 0,
        }}
        onClick={onClick}
        className="group flex cursor-pointer flex-col gap-4 rounded-[2rem] border border-slate-200 bg-white p-6 shadow-lg shadow-slate-300/10 transition-all hover:-translate-y-0.5 hover:border-indigo-500/60 hover:shadow-xl"
      >

        <div className="flex items-start justify-between gap-3">

          <div className="min-w-0">

            <h3 className="truncate text-base font-black uppercase tracking-tight text-slate-950">
              {task.title}
            </h3>

            {task.description && (
              <p className="mt-1 line-clamp-2 text-xs font-bold text-slate-500">
                {task.description}
              </p>
            )}

          </div>


          <ArrowRight
            size={18}
            className="mt-1 shrink-0 text-slate-300 transition-colors group-hover:text-indigo-600"
          />

        </div>


        <div className="flex flex-wrap items-center gap-2">

          <TaskStatusBadge
            status={task.status}
            size="sm"
          />

          {overdue && (
            <Badge
              tone="red"
              size="sm"
              dot
            >
              Overdue
            </Badge>
          )}

        </div>


        <div className="flex flex-wrap items-center gap-4 border-t border-slate-100 pt-4 text-[10px] font-black uppercase tracking-widest text-slate-400">

          {task.assignedToId != null && (
            <span className="flex items-center gap-1.5">
              <User size={12} />
              Employee #{task.assignedToId}
            </span>
          )}


          {task.dueDate && (
            <span
              className={
                overdue
                  ? "flex items-center gap-1.5 text-red-500"
                  : "flex items-center gap-1.5"
              }
            >
              <Calendar size={12} />

              {formatDate(
                task.dueDate
              )}

            </span>
          )}

        </div>

      </MotionDiv>
    );
  };


export default TaskCard;