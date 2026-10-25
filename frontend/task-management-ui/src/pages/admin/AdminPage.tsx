import { UserPlus, Users, UserCog, Ban, CheckCircle2 } from "lucide-react";
import { useState , useEffect} from "react";
import  { authService } from "../../api";
import type { UserResponseDTO, ProvisionUserRequest, UserRole } from "../../api/models/authModels";
import ConfirmDialog from "../../components/common/ConfirmDialog";
import EmptyState from "../../components/common/EmptyState";
import ErrorState from "../../components/common/ErrorState";
import LoadingState from "../../components/common/LoadingState";
import PageHeader from "../../components/common/PageHeader";
import UserTable from "../../components/common/UserTable";
import  { useDisclosure } from "../../hooks/useCommon";
import Button from "../../ui/Button";
import Input from "../../ui/Input";
import Modal from "../../ui/Modal";
import Select from "../../ui/Select";
import { useToast } from "../../ui/Toast";
import  { getErrorMessage } from "../../utils/errorUtils";
import { normalizeRole } from "../../utils/roleUtils";
import { validateForm, required, compose, hasErrors } from "../../utils/validationUtils";
import React, { useCallback,} from 'react';


interface FormState {
  fname: string;
  lname: string;
  email: string;
  phoneNumber: string;
  password: string;
  role: UserRole;
}

const EMPTY: FormState = {
  fname: '',
  lname: '',
  email: '',
  phoneNumber: '',
  password: '',
  role: 'EMPLOYEE',
};

const ROLE_OPTIONS = [
  { label: 'Employee', value: 'EMPLOYEE' },
  { label: 'Manager', value: 'MANAGER' },
  { label: 'Administrator', value: 'ADMIN' },
];


/* ============================================================
   ADMIN USERS HOOK
   ============================================================ */

function useAdminUsers(): {
  users: UserResponseDTO[];
  loading: boolean;
  error: string | null;
  reload: () => Promise<void>;
  setUsers: React.Dispatch<React.SetStateAction<UserResponseDTO[]>>;
} {
  const [users, setUsers] = useState<UserResponseDTO[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const reload = useCallback(async (): Promise<void> => {
    setLoading(true);
    setError(null);

    try {
      const response = await authService.getAllUsers();

      setUsers(response);
    } catch (err: unknown) {
      const message =
        err instanceof Error
          ? err.message
          : 'Could not load users';

      setError(message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void reload();
  }, [reload]);

  return {
    users,
    loading,
    error,
    reload,
    setUsers,
  };
}


/* ============================================================
   ADMIN PAGE
   ============================================================ */

const AdminPage: React.FC = () => {
  const toast = useToast();

  const {
    users,
    loading,
    error,
    reload,
    setUsers,
  } = useAdminUsers();

  const provisionModal = useDisclosure();
  const roleModal = useDisclosure();
  const toggleConfirm = useDisclosure();

  const [form, setForm] = useState<FormState>(EMPTY);

  const [errors, setErrors] = useState<
    Partial<Record<keyof FormState, string>>
  >({});

  const [saving, setSaving] = useState(false);

  const [roleTarget, setRoleTarget] =
    useState<UserResponseDTO | null>(null);

  const [newRole, setNewRole] = useState('');
  const [roleSaving, setRoleSaving] = useState(false);

  const [toggleTarget, setToggleTarget] =
    useState<UserResponseDTO | null>(null);

  const [toggling, setToggling] = useState(false);


  /* ============================================================
     FORM FIELD HANDLER
     ============================================================ */

  const set =
    (key: keyof FormState) =>
    (e: React.ChangeEvent<HTMLInputElement>) => {
      setForm((previous) => ({
        ...previous,
        [key]: e.target.value,
      }));
    };


  /* ============================================================
     PROVISION USER
     ============================================================ */

  const handleProvision = async (
    e: React.FormEvent
  ): Promise<void> => {
    e.preventDefault();

    const nextErrors = validateForm(form, {
      fname: required('First name'),
      lname: required('Last name'),
      email: compose(
        required('Email'),
        emailValidator
      ),
      password: compose(
    required('Password'),
    (value: string) =>
      value.length >= 8
        ? undefined
        : 'Password must be at least 8 characters'
  ),
    });

    setErrors(nextErrors);

    if (hasErrors(nextErrors)) {
      return;
    }

    setSaving(true);

    try {
      const payload: ProvisionUserRequest = {
        fname: form.fname,
        lname: form.lname,
        email: form.email,
        password: form.password,
        phoneNumber: form.phoneNumber || undefined,
        role: form.role,
      };

      const created =
        await authService.provisionUser(payload);

      setUsers((previous) => [
        created,
        ...previous,
      ]);

      toast.success('Account provisioned.');

      setForm(EMPTY);
      setErrors({});

      provisionModal.close();

    } catch (err: unknown) {
      toast.error(
        getErrorMessage(
          err,
          'Could not provision account'
        )
      );
    } finally {
      setSaving(false);
    }
  };


  /* ============================================================
     ROLE MODAL
     ============================================================ */

  const openRoleModal = (
    user: UserResponseDTO
  ): void => {
    setRoleTarget(user);

    setNewRole(
      normalizeRole(user.role) as UserRole
    );

    roleModal.open();
  };


  /* ============================================================
     CHANGE ROLE
     ============================================================ */

  const handleRoleChange = async (): Promise<void> => {
  if (!roleTarget) {
    return;
  }

  setRoleSaving(true);

  try {
    const updated = await authService.changeRole(
      roleTarget.id,
      newRole as UserRole
    );

    setUsers((previous) =>
      previous.map((user) =>
        user.id === updated.id
          ? updated
          : user
      )
    );

    toast.success('Role updated.');
    roleModal.close();

  } catch (err: unknown) {
    toast.error(
      getErrorMessage(
        err,
        'Could not update role'
      )
    );
  } finally {
    setRoleSaving(false);
  }
};


  /* ============================================================
     ENABLE / DISABLE MODAL
     ============================================================ */

  const openToggleConfirm = (
    user: UserResponseDTO
  ): void => {
    setToggleTarget(user);
    toggleConfirm.open();
  };


  /* ============================================================
     ENABLE / DISABLE USER
     ============================================================ */
const handleToggle = async (): Promise<void> => {
  if (!toggleTarget) {
    return;
  }

  setToggling(true);

  try {
    const updated = toggleTarget.enabled
      ? await authService.disableUser(toggleTarget.id)
      : await authService.enableUser(toggleTarget.id);

    setUsers((previous) =>
      previous.map((user) =>
        user.id === updated.id
          ? updated
          : user
      )
    );

    toast.success(
      toggleTarget.enabled
        ? 'User disabled.'
        : 'User enabled.'
    );

    toggleConfirm.close();

  } catch (err: unknown) {
    toast.error(
      getErrorMessage(
        err,
        'Could not update user status'
      )
    );
  } finally {
    setToggling(false);
  }
};


  /* ============================================================
     RENDER
     ============================================================ */

  return (
    <div className="space-y-8 pb-16">

      <PageHeader
        title="Administration"
        subtitle="Full account control: provisioning, roles, and access"
        actions={
          <Button
            leftIcon={<UserPlus size={16} />}
            onClick={provisionModal.open}
          >
            New account
          </Button>
        }
      />


      {/* ======================================================
          USERS
          ====================================================== */}

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

      ) : users.length === 0 ? (
        <EmptyState
          icon={<Users size={48} />}
          title="No users found"
        />

      ) : (
        <div className="glass-panel overflow-hidden rounded-[2.5rem]">

          <UserTable
            users={users}

            renderActions={(user) => (
              <div className="flex justify-end gap-2">

                <Button
                  size="sm"
                  variant="glass"
                  leftIcon={
                    <UserCog size={14} />
                  }
                  onClick={() =>
                    openRoleModal(user)
                  }
                >
                  Role
                </Button>


                <Button
                  size="sm"
                  variant={
                    user.enabled
                      ? 'danger'
                      : 'success'
                  }
                  leftIcon={
                    user.enabled
                      ? <Ban size={14} />
                      : <CheckCircle2 size={14} />
                  }
                  onClick={() =>
                    openToggleConfirm(user)
                  }
                >
                  {user.enabled
                    ? 'Disable'
                    : 'Enable'}
                </Button>

              </div>
            )}
          />

        </div>
      )}


      {/* ======================================================
          PROVISION USER MODAL
          ====================================================== */}

      <Modal
        open={provisionModal.isOpen}
        onClose={provisionModal.close}
        title="Provision account"
        footer={
          <>
            <Button
              variant="ghost"
              onClick={provisionModal.close}
              disabled={saving}
            >
              Cancel
            </Button>

            <Button
              onClick={() => {
                const formElement =
                  document.querySelector(
                    '#provision-user-form'
                  ) as HTMLFormElement | null;

                formElement?.requestSubmit();
              }}
              loading={saving}
            >
              Create account
            </Button>
          </>
        }
      >

        <form
          id="provision-user-form"
          onSubmit={handleProvision}
          className="space-y-5"
        >

          <div className="grid grid-cols-2 gap-4">

            <Input
              label="First name"
              value={form.fname}
              onChange={set('fname')}
              error={errors.fname}
              required
            />

            <Input
              label="Last name"
              value={form.lname}
              onChange={set('lname')}
              error={errors.lname}
              required
            />

          </div>


          <Input
            label="Email"
            type="email"
            value={form.email}
            onChange={set('email')}
            error={errors.email}
            required
          />


          <Input
            label="Phone number"
            placeholder="Optional"
            value={form.phoneNumber}
            onChange={set('phoneNumber')}
          />

<Input
            label="Initial Password"
            placeholder="Required"
            value={form.password}
            onChange={set('password')}
          />

          <Select
  label="Role"
  value={form.role}
  onChange={(e) =>
    setForm((previous) => ({
      ...previous,
      role: e.target.value as UserRole,
    }))
  }
  options={ROLE_OPTIONS}
/>

        </form>

      </Modal>


      {/* ======================================================
          ROLE CHANGE MODAL
          ====================================================== */}

      <Modal
        open={roleModal.isOpen}
        onClose={roleModal.close}
        title="Change role"
        size="sm"
        footer={
          <>
            <Button
              variant="ghost"
              onClick={roleModal.close}
              disabled={roleSaving}
            >
              Cancel
            </Button>

            <Button
              onClick={handleRoleChange}
              loading={roleSaving}
            >
              Save role
            </Button>
          </>
        }
      >

        {roleTarget && (
          <div className="space-y-5">

            <p className="text-sm font-bold text-slate-600">

              Updating role for{' '}

              <span className="text-slate-950">
                {roleTarget.fname}{' '}
                {roleTarget.lname}
              </span>

            </p>


            <Select
              label="New role"
              value={newRole}
              onChange={(e) =>
                setNewRole(e.target.value as UserRole)
              }
              options={ROLE_OPTIONS}
            />

          </div>
        )}

      </Modal>


      {/* ======================================================
          ENABLE / DISABLE CONFIRMATION
          ====================================================== */}

      <ConfirmDialog
        open={toggleConfirm.isOpen}

        title={
          toggleTarget?.enabled
            ? 'Disable this user?'
            : 'Enable this user?'
        }

        message={
          toggleTarget?.enabled
            ? 'They will immediately lose access to sign in.'
            : 'They will regain access to sign in.'
        }

        confirmLabel={
          toggleTarget?.enabled
            ? 'Disable'
            : 'Enable'
        }

        tone={
          toggleTarget?.enabled
            ? 'danger'
            : 'primary'
        }

        loading={toggling}

        onConfirm={handleToggle}

        onCancel={toggleConfirm.close}
      />

    </div>
  );
};


/* ============================================================
   EMAIL VALIDATOR
   ============================================================ */

function emailValidator(
  value: string
): string | undefined {

  const email = value.trim();

  if (!email) {
    return undefined;
  }

  const emailRegex =
    /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i;

  if (!emailRegex.test(email)) {
    return 'Please enter a valid email address';
  }

  return undefined;
}


export default AdminPage;