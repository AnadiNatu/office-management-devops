import { UserPlus, Users } from "lucide-react";
import { useState } from "react";
import { authService } from "../../api";
import type { ProvisionUserRequest, UserRole } from "../../api/models/authModels";
import EmptyState from "../../components/common/EmptyState";
import ErrorState from "../../components/common/ErrorState";
import LoadingState from "../../components/common/LoadingState";
import PageHeader from "../../components/common/PageHeader";
import UserTable from "../../components/common/UserTable";
import { useUserDirectory, useDisclosure } from "../../hooks/useCommon";
import Button from "../../ui/Button";
import Input from "../../ui/Input";
import Modal from "../../ui/Modal";
import Select from "../../ui/Select";
import { useToast } from "../../ui/Toast";
import { getErrorMessage } from "../../utils/errorUtils";
import { validateForm, required, compose, hasErrors, email } from "../../utils/validationUtils";

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

const ManagementPage: React.FC = () => {
  const toast = useToast();
  const { users, loading, error, reload } = useUserDirectory();
  const provisionModal = useDisclosure();

  const [form, setForm] = useState<FormState>(EMPTY);
  const [errors, setErrors] = useState<
    Partial<Record<keyof FormState, string>>
  >({});
  const [saving, setSaving] = useState(false);

  const ROLE_OPTIONS: {
  label: string;
  value: UserRole;
}[] = [
  {
    label: "Employee",
    value: "EMPLOYEE",
  },
  {
    label: "Manager",
    value: "MANAGER",
  },
];
  const set =
    (key: keyof FormState) =>
    (
      e: React.ChangeEvent<HTMLInputElement>
    ) => {
      setForm((f) => ({
        ...f,
        [key]: e.target.value,
      }));
    };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    const nextErrors = validateForm(form, {
      fname: required('First name'),
      lname: required('Last name'),
      email: compose(required('Email'), email),
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
        fname: form.fname.trim(),
        lname: form.lname.trim(),
        email: form.email.trim(),
        phoneNumber: form.phoneNumber.trim() || undefined,
        password: form.password,
        role: form.role,
      };

      await authService.managementProvisionUser(payload);

      toast.success('Employee account created.');

      setForm(EMPTY);
      setErrors({});
      provisionModal.close();

      reload();
    } catch (e) {
      toast.error(
        getErrorMessage(e, 'Could not create account')
      );
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="space-y-8 pb-16">
      <PageHeader
        title="Management"
        subtitle="Provision new employee and user accounts"
        actions={
          <Button
            leftIcon={<UserPlus size={16} />}
            onClick={provisionModal.open}
          >
            New account
          </Button>
        }
      />

      {loading ? (
        <LoadingState variant="skeleton" rows={4} />
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
          <UserTable users={users} />
        </div>
      )}

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
              onClick={handleSubmit}
              loading={saving}
            >
              Create account
            </Button>
          </>
        }
      >
        <form
          onSubmit={handleSubmit}
          className="space-y-5"
        >
          {/* First + Last Name */}
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

          {/* Email */}
          <Input
            label="Email"
            type="email"
            value={form.email}
            onChange={set('email')}
            error={errors.email}
            required
          />

          {/* Phone */}
          <Input
            label="Phone number"
            type="tel"
            placeholder="Optional"
            value={form.phoneNumber}
            onChange={set('phoneNumber')}
          />

          {/* Password */}
          <Input
            label="Initial password"
            type="password"
            placeholder="Enter at least 8 characters"
            value={form.password}
            onChange={set('password')}
            error={errors.password}
            required
          />

          {/* Role */}
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

          <p className="text-xs font-semibold text-slate-500">
            The initial password will be used by the employee when
            signing in for the first time.
          </p>
        </form>
      </Modal>
    </div>
  );
};

export default ManagementPage;