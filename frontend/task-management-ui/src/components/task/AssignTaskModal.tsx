import { useState, useEffect } from "react";
import type { TaskResponseDTO } from "../../api/models/taskModels";
import { useUserDirectory } from "../../hooks/useCommon";
import Button from "../../ui/Button";
import Modal from "../../ui/Modal";
import Select from "../../ui/Select";
import { fullName } from "../../utils/formatUtils";


interface AssignTaskModalProps {
  open: boolean;
  onClose: () => void;

  task:
    | TaskResponseDTO
    | null;

  onAssign:
    (
      userId: number
    ) => Promise<void>;
}


const AssignTaskModal:
  React.FC<
    AssignTaskModalProps
  > = ({
    open,
    onClose,
    task,
    onAssign,
  }) => {

    const {
      users,
      loading,
    } =
      useUserDirectory(
        open
      );


    const [
      userId,
      setUserId,
    ] = useState("");


    const [
      saving,
      setSaving,
    ] = useState(false);


    const isReassign =
      task?.assignedToId != null;


    useEffect(() => {

      if (!open) {
        return;
      }

      setUserId(
        task?.assignedToId != null
          ? String(
              task.assignedToId
            )
          : ""
      );

    }, [
      open,
      task,
    ]);


    const handleSubmit =
      async (
        event: React.FormEvent
      ) => {

        event.preventDefault();


        if (!userId) {
          return;
        }


        setSaving(true);

        try {

          await onAssign(
            Number(userId)
          );

          onClose();

        } finally {

          setSaving(false);
        }
      };


    return (
      <Modal
        open={open}
        onClose={onClose}
        title={
          isReassign
            ? "Reassign task"
            : "Assign task"
        }
        size="sm"
        footer={
          <>
            <Button
              variant="ghost"
              onClick={onClose}
              disabled={saving}
            >
              Cancel
            </Button>

            <Button
              onClick={
                handleSubmit
              }
              loading={saving}
              disabled={!userId}
            >
              {
                isReassign
                  ? "Reassign"
                  : "Assign"
              }
            </Button>
          </>
        }
      >

        <form
          onSubmit={
            handleSubmit
          }
        >

          <Select
            label="Employee"
            value={userId}
            onChange={
              (event) =>
                setUserId(
                  event.target.value
                )
            }
            placeholder="Select an employee"
            required
            hint={
              loading
                ? "Loading employees…"
                : undefined
            }
            options={
              users.map(
                (user) => ({
                  label:
                    fullName(user),
                  value:
                    String(
                      user.id
                    ),
                })
              )
            }
          />

        </form>

      </Modal>
    );
  };


export default AssignTaskModal;