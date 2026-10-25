import { useState } from "react";
import { useAuth } from "../../hooks/useAuth";
import { useUserDirectory } from "../../hooks/useCommon";
import Button from "../../ui/Button";
import Modal from "../../ui/Modal";
import Select from "../../ui/Select";
import { fullName } from "../../utils/formatUtils";

interface NewConversationModalProps {
  open: boolean;
  onClose: () => void;
  onCreate: (participantId: number) => Promise<void>;
}

export const NewConversationModal: React.FC<
  NewConversationModalProps
> = ({
  open,
  onClose,
  onCreate,
}) => {
  const { user } = useAuth();
  const {
    users,
    loading,
  } = useUserDirectory(open);

  const [
    participantId,
    setParticipantId,
  ] = useState('');

  const [saving, setSaving] =
    useState(false);

  const handleClose = () => {
    if (saving) {
      return;
    }

    setParticipantId('');
    onClose();
  };

  const handleSubmit = async (
    event: React.FormEvent
  ) => {
    event.preventDefault();

    const numericParticipantId =
      Number(participantId);

    if (
      !participantId ||
      !Number.isInteger(
        numericParticipantId
      ) ||
      numericParticipantId <= 0 ||
      numericParticipantId === user?.id
    ) {
      return;
    }

    setSaving(true);

    try {
      await onCreate(
        numericParticipantId
      );

      setParticipantId('');
      onClose();
    } finally {
      setSaving(false);
    }
  };

  const colleagueOptions =
    users
      .filter(
        (u) => u.id !== user?.id
      )
      .map((u) => ({
        label: fullName(u),
        value: String(u.id),
      }));

  return (
    <Modal
      open={open}
      onClose={handleClose}
      title="New conversation"
      size="sm"
      footer={
        <>
          <Button
            variant="ghost"
            onClick={handleClose}
            disabled={saving}
          >
            Cancel
          </Button>

          <Button
            type="submit"
            form="new-conversation-form"
            loading={saving}
            disabled={
              !participantId ||
              loading ||
              saving
            }
          >
            Start conversation
          </Button>
        </>
      }
    >
      <form
        id="new-conversation-form"
        onSubmit={handleSubmit}
      >
        <Select
          label="Colleague"
          value={participantId}
          onChange={(event) =>
            setParticipantId(
              event.target.value
            )
          }
          placeholder={
            loading
              ? 'Loading colleagues...'
              : 'Select a person'
          }
          required
          hint={
            loading
              ? 'Loading directory…'
              : undefined
          }
          options={colleagueOptions}
        />
      </form>
    </Modal>
  );
};

export default NewConversationModal;