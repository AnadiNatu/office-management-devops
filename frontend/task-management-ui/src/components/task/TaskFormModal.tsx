import { useState, useEffect } from "react";
import type { TaskResponseDTO, CreateTaskRequest, UpdateTaskRequest } from "../../api/models/taskModels";
// import { can } from "../../auth/permissions";
// import { useAuth } from "../../hooks/useAuth";
// import { useUserDirectory } from "../../hooks/useCommon";
import Button from "../../ui/Button";
import Input from "../../ui/Input";
import Modal from "../../ui/Modal";
// import Select from "../../ui/Select";
import TextArea from "../../ui/TextArea";
import { toDateInputValue } from "../../utils/dateUtils";
// import { fullName } from "../../utils/formatUtils";
import { validateForm, required, hasErrors } from "../../utils/validationUtils";

interface TaskFormModalProps {
  open: boolean;
  onClose: () => void;

  task?: TaskResponseDTO | null;

  onSubmit: (
    request:
      | CreateTaskRequest
      | UpdateTaskRequest
  ) => Promise<void>;
}


interface FormState {
  title: string;
  description: string;
  dueDate: string;
}


const toForm = (
  task?: TaskResponseDTO | null
): FormState => ({
  title: task?.title ?? "",
  description: task?.description ?? "",
  dueDate: toDateInputValue(
    task?.dueDate
  ),
});


const TaskFormModal: React.FC<
  TaskFormModalProps
> = ({
  open,
  onClose,
  task,
  onSubmit,
}) => {

  const isEdit =
    task != null;

  const [
    form,
    setForm,
  ] = useState<FormState>(
    () => toForm(task)
  );


  const [
    errors,
    setErrors,
  ] = useState<
    Partial<
      Record<
        keyof FormState,
        string
      >
    >
  >({});


  const [
    saving,
    setSaving,
  ] = useState(false);


  useEffect(() => {

    if (!open) {
      return;
    }

    setForm(
      toForm(task)
    );

    setErrors({});

  }, [open, task]);


  const set = (
    key: keyof FormState
  ) => (
    event:
      React.ChangeEvent<
        HTMLInputElement |
        HTMLTextAreaElement
      >
  ) => {

    setForm(
      (previous) => ({
        ...previous,
        [key]:
          event.target.value,
      })
    );
  };


  const handleSubmit =
    async (
      event: React.FormEvent
    ) => {

      event.preventDefault();


      const nextErrors =
        validateForm(
          form,
          {
            title:
              required("Title"),

            description:
              required(
                "Description"
              ),
          }
        );


      setErrors(
        nextErrors
      );


      if (
        hasErrors(
          nextErrors
        )
      ) {
        return;
      }


      setSaving(true);


      try {

        const payload:
          | CreateTaskRequest
          | UpdateTaskRequest = {

          title:
            form.title.trim(),

          description:
            form.description.trim(),

          dueDate:
            form.dueDate ||
            undefined,
        };


        await onSubmit(
          payload
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
        isEdit
          ? "Edit task"
          : "New task"
      }
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
            onClick={handleSubmit}
            loading={saving}
          >
            {
              isEdit
                ? "Save changes"
                : "Create task"
            }
          </Button>
        </>
      }
    >

      <form
        onSubmit={handleSubmit}
        className="space-y-5"
      >

        <Input
          label="Title"
          value={form.title}
          onChange={set("title")}
          error={errors.title}
          required
        />


        <TextArea
          label="Description"
          value={form.description}
          onChange={set("description")}
          rows={5}
          error={errors.description}
        />


        <Input
          label="Due date"
          type="date"
          value={form.dueDate}
          onChange={set("dueDate")}
        />

      </form>

    </Modal>
  );
};


export default TaskFormModal;