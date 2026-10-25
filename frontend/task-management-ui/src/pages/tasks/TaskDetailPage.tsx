import { Pencil, UserPlus, Trash2, User, Calendar } from "lucide-react";
import { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import type { TaskStatus, UpdateTaskRequest } from "../../api/models/taskModels";
import { can } from "../../auth/permissions";
import CommentComposer from "../../components/comment/CommentComposer";
import CommentList from "../../components/comment/CommentList";
import ConfirmDialog from "../../components/common/ConfirmDialog";
import ErrorState from "../../components/common/ErrorState";
import LoadingState from "../../components/common/LoadingState";
import AssignTaskModal from "../../components/task/AssignTaskModal";
import TaskFormModal from "../../components/task/TaskFormModal";
import TaskStatusBadge, { getTaskStatusOptions } from "../../components/task/TaskStatusBadge";
import { useAuth } from "../../hooks/useAuth";
import { useTaskComments } from "../../hooks/useComments";
import { useDisclosure } from "../../hooks/useCommon";
import { useTask, useTasks } from "../../hooks/useTasks";
import { ROUTES } from "../../routes/routeConstants";
import Button from "../../ui/Button";
import Card from "../../ui/Card";
import Select from "../../ui/Select";
import { useToast } from "../../ui/Toast";
import { formatDateTime } from "../../utils/dateUtils";
import  { getErrorMessage } from "../../utils/errorUtils";

const TaskDetailPage: React.FC = () => {

  const {
    id,
  } = useParams();

  const taskId =
    id
      ? Number(id)
      : undefined;


  const navigate =
    useNavigate();

  const toast =
    useToast();

  const {
    user,
  } = useAuth();


  const {
    task,
    setTask,
    loading,
    error,
    reload,
  } =
    useTask(taskId);


  const {
    updateTask,
    assignTask,
    reassignTask,
    updateTaskStatus,
    deleteTask,
  } =
    useTasks();


  const {
    comments,
    loading:
      commentsLoading,
    addComment,
    updateComment,
    deleteComment,
  } =
    useTaskComments(taskId);


  const editModal =
    useDisclosure();

  const assignModal =
    useDisclosure();

  const deleteConfirm =
    useDisclosure();


  const [
    deleting,
    setDeleting,
  ] = useState(false);


  const canAssign =
    can(
      user?.role,
      "TASK_ASSIGN"
    );

  const canReassign =
    can(
      user?.role,
      "TASK_REASSIGN"
    );

  const canDelete =
    can(
      user?.role,
      "TASK_DELETE"
    );


  if (loading) {
    return (
      <LoadingState
        label="Loading task…"
      />
    );
  }


  if (
    error ||
    !task
  ) {
    return (
      <ErrorState
        message={
          error ??
          "Task not found"
        }
        onRetry={reload}
      />
    );
  }


  const canEdit =
    user?.role === "ADMIN" ||
    (
      user?.role === "MANAGER" &&
      task.createdById ===
        user.id
    );


  const handleStatusChange =
    async (
      status: string
    ) => {

      const nextStatus =
        status as TaskStatus;


      if (
        nextStatus ===
        task.status
      ) {
        return;
      }


      try {

        const updated =
          await updateTaskStatus(
            task.id,
            {
              status:
                nextStatus,
            }
          );


        setTask(updated);

        toast.success(
          "Status updated."
        );

      } catch (e) {

        toast.error(
          getErrorMessage(
            e,
            "Could not update status"
          )
        );
      }
    };


  const handleEditSubmit =
    async (
      payload: UpdateTaskRequest
    ) => {

      try {

        const updated =
          await updateTask(
            task.id,
            payload
          );


        setTask(updated);

        toast.success(
          "Task updated."
        );

      } catch (e) {

        toast.error(
          getErrorMessage(
            e,
            "Could not update task"
          )
        );

        throw e;
      }
    };


  const handleAssign =
    async (
      userId: number
    ) => {

      try {

        const updated =
          task.assignedToId != null
            ? await reassignTask(
                task.id,
                {
                  assignedToId:
                    userId,
                }
              )
            : await assignTask(
                task.id,
                {
                  assignedToId:
                    userId,
                }
              );


        setTask(updated);


        toast.success(
          task.assignedToId != null
            ? "Task reassigned."
            : "Task assigned."
        );

      } catch (e) {

        toast.error(
          getErrorMessage(
            e,
            "Could not assign task"
          )
        );

        throw e;
      }
    };


  const handleDelete =
    async () => {

      setDeleting(true);

      try {

        await deleteTask(
          task.id
        );

        toast.success(
          "Task deleted."
        );

        navigate(
          ROUTES.TASKS,
          {
            replace: true,
          }
        );

      } catch (e) {

        toast.error(
          getErrorMessage(
            e,
            "Could not delete task"
          )
        );

      } finally {

        setDeleting(false);
        deleteConfirm.close();
      }
    };


  const statusOptions = getTaskStatusOptions(task.status).map(
  ({ label, value }) => ({
    label,
    value,
  })
);


  return (
    <div className="space-y-8 pb-16">

      <Card
        title={task.title}
        subtitle={
          `Created ${formatDateTime(
            task.createdAt
          )}`
        }
        actions={
          <>
            {canEdit && (
              <Button
                size="sm"
                variant="secondary"
                leftIcon={
                  <Pencil size={14} />
                }
                onClick={
                  editModal.open
                }
              >
                Edit
              </Button>
            )}


            {(canAssign ||
              canReassign) && (
              <Button
                size="sm"
                variant="glass"
                leftIcon={
                  <UserPlus size={14} />
                }
                onClick={
                  assignModal.open
                }
              >
                {
                  task.assignedToId != null
                    ? "Reassign"
                    : "Assign"
                }
              </Button>
            )}


            {canDelete && (
              <Button
                size="sm"
                variant="danger"
                leftIcon={
                  <Trash2 size={14} />
                }
                onClick={
                  deleteConfirm.open
                }
              >
                Delete
              </Button>
            )}
          </>
        }
      >

        <div className="space-y-6">

          <div className="flex flex-wrap items-center gap-3">

            <TaskStatusBadge
              status={task.status}
            />

          </div>


          {task.description && (
            <p className="whitespace-pre-wrap text-sm font-semibold leading-relaxed text-slate-700">
              {task.description}
            </p>
          )}


          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">

            <div className="flex items-center gap-3 rounded-2xl border border-slate-100 bg-slate-50/60 p-4">

              <User
                size={18}
                className="text-indigo-600"
              />

              <div>

                <p className="text-[10px] font-black uppercase tracking-widest text-slate-400">
                  Assignee
                </p>

                <p className="text-sm font-black text-slate-900">
                  {
                    task.assignedToId != null
                      ? `Employee #${task.assignedToId}`
                      : "Unassigned"
                  }
                </p>

              </div>

            </div>


            <div className="flex items-center gap-3 rounded-2xl border border-slate-100 bg-slate-50/60 p-4">

              <Calendar
                size={18}
                className="text-indigo-600"
              />

              <div>

                <p className="text-[10px] font-black uppercase tracking-widest text-slate-400">
                  Due date
                </p>

                <p className="text-sm font-black text-slate-900">
                  {
                    task.dueDate
                      ? formatDateTime(
                          task.dueDate
                        )
                      : "No due date"
                  }
                </p>

              </div>

            </div>

          </div>


          <div className="max-w-xs">

            <Select
              label="Update status"
              value={task.status}
              onChange={
                (event) =>
                  handleStatusChange(
                    event.target.value
                  )
              }
              options={statusOptions}
            />

          </div>

        </div>

      </Card>


      <Card
        title="Comments"
        subtitle={`${comments.length} on this task`}
      >

        <div className="space-y-6">

          <CommentComposer
            onSubmit={
              async (comment) => {
                await addComment({
                  comment,
                });
              }
            }
          />


          {commentsLoading ? (

            <LoadingState
              variant="skeleton"
              rows={2}
            />

          ) : (

            <CommentList
  comments={comments}
  onUpdate={async (commentId, comment) => {
    await updateComment(commentId, {
      comment,
    });
  }}
  onDelete={deleteComment}
  taskTitleFor={() => `Task #${task.id}`}
/>

          )}

        </div>

      </Card>


      <TaskFormModal
        open={
          editModal.isOpen
        }
        onClose={
          editModal.close
        }
        task={task}
        onSubmit={
          handleEditSubmit
        }
      />


      <AssignTaskModal
        open={
          assignModal.isOpen
        }
        onClose={
          assignModal.close
        }
        task={task}
        onAssign={
          handleAssign
        }
      />


      <ConfirmDialog
        open={
          deleteConfirm.isOpen
        }
        title="Delete this task?"
        message="This action cannot be undone."
        confirmLabel="Delete"
        tone="danger"
        loading={deleting}
        onConfirm={
          handleDelete
        }
        onCancel={
          deleteConfirm.close
        }
      />

    </div>
  );
};


export default TaskDetailPage;