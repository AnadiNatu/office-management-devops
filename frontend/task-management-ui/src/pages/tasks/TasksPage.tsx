import { Plus, CheckSquare } from "lucide-react";
import { useState, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import type { CreateTaskRequest } from "../../api/models/taskModels";
import { can } from "../../auth/permissions";
import EmptyState from "../../components/common/EmptyState";
import ErrorState from "../../components/common/ErrorState";
import LoadingState from "../../components/common/LoadingState";
import PageHeader from "../../components/common/PageHeader";
import TaskCard from "../../components/task/TaskCard";
import TaskFilters from "../../components/task/TaskFilters";
import TaskFormModal from "../../components/task/TaskFormModal";
import { useAuth } from "../../hooks/useAuth";
import { useDisclosure, useDebounce } from "../../hooks/useCommon";
import { useTasks } from "../../hooks/useTasks";
import { ROUTES } from "../../routes/routeConstants";
import Button from "../../ui/Button";
import { useToast } from "../../ui/Toast";
import { getErrorMessage } from "../../utils/errorUtils";



const TasksPage:
  React.FC = () => {

    const {
      user,
    } = useAuth();


    const toast =
      useToast();


    const navigate =
      useNavigate();


    const {
      tasks,
      loading,
      error,
      reload,
      canViewAll,
      createTask,
    } =
      useTasks();


    const createModal =
      useDisclosure();


    const [
      search,
      setSearch,
    ] = useState("");


    const [
      status,
      setStatus,
    ] = useState("");


    const debouncedSearch =
      useDebounce(
        search,
        250
      );


    const canCreate =
      can(
        user?.role,
        "TASK_CREATE"
      );


    const filtered =
      useMemo(
        () => {

          const query =
            debouncedSearch
              .trim()
              .toLowerCase();


          return tasks.filter(
            (task) => {

              const matchesQuery =
                !query ||
                task.title
                  .toLowerCase()
                  .includes(query) ||
                task.description
                  .toLowerCase()
                  .includes(query);


              const matchesStatus =
                !status ||
                task.status ===
                  status;


              return (
                matchesQuery &&
                matchesStatus
              );
            }
          );

        },
        [
          tasks,
          debouncedSearch,
          status,
        ]
      );


    const handleCreate =
      async (
        payload:
          CreateTaskRequest
      ) => {

        try {

          await createTask(
            payload
          );

          toast.success(
            "Task created."
          );

        } catch (error) {

          toast.error(
            getErrorMessage(
              error,
              "Could not create task"
            )
          );

          throw error;
        }
      };


    return (
      <div className="space-y-8 pb-16">

        <PageHeader
          title="Tasks"
          subtitle={
            canViewAll
              ? "All tasks across the organization"
              : "Tasks assigned to you"
          }
          actions={
            canCreate && (
              <Button
                leftIcon={
                  <Plus size={16} />
                }
                onClick={
                  createModal.open
                }
              >
                New task
              </Button>
            )
          }
        />


        <TaskFilters
          search={search}
          onSearchChange={
            setSearch
          }
          status={status}
          onStatusChange={
            setStatus
          }
        />


        {loading ? (

          <LoadingState
            variant="skeleton"
            rows={4}
          />

        ) : error ? (

          <ErrorState
            message={error}
            onRetry={reload}
          />

        ) : filtered.length === 0 ? (

          <EmptyState
            icon={
              <CheckSquare
                size={48}
              />
            }
            title="No tasks found"
            description={
              tasks.length
                ? "Try adjusting your filters."
                : "Nothing has been created yet."
            }
          />

        ) : (

          <div className="grid grid-cols-1 gap-5 md:grid-cols-2 xl:grid-cols-3">

            {filtered.map(
              (task) => (
                <TaskCard
                  key={task.id}
                  task={task}
                  onClick={() =>
                    navigate(
                      ROUTES.TASK_DETAIL(
                        task.id
                      )
                    )
                  }
                />
              )
            )}

          </div>
        )}


        <TaskFormModal
          open={
            createModal.isOpen
          }
          onClose={
            createModal.close
          }
          onSubmit={
            handleCreate
          }
        />

      </div>
    );
  };


export default TasksPage;