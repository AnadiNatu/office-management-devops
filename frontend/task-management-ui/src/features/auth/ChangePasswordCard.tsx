import { KeyRound } from "lucide-react";
import { useState } from "react";
import { authService } from "../../api";
import Button from "../../ui/Button";
import Card from "../../ui/Card";
import Input from "../../ui/Input";
import { useToast } from "../../ui/Toast";
import { getErrorMessage } from "../../utils/errorUtils";
import { validateForm, required, compose, strongPassword, matches, hasErrors } from "../../utils/validationUtils";

interface FormState {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}
// Need to make the values of the const to be of simple string or simple string array. 
const EMPTY: FormState = { currentPassword: '', newPassword: '', confirmPassword: '' };

export const ChangePasswordCard: React.FC = () => {
  const toast = useToast();
  const [form, setForm] = useState<FormState>(EMPTY);
  const [errors, setErrors] = useState<Partial<Record<keyof FormState, string>>>({});
  const [saving, setSaving] = useState(false);

  const set = (key: keyof FormState) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm((f) => ({ ...f, [key]: e.target.value }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const nextErrors = validateForm(form, {
      currentPassword: required('Current password'),
      newPassword: compose(required('New password'), strongPassword),
      confirmPassword: matches(() => form.newPassword, 'Passwords do not match'),
    });
    setErrors(nextErrors);
    if (hasErrors(nextErrors)) return;

    setSaving(true);
    try {
      await authService.changePassword({
        currentPassword: form.currentPassword,
        newPassword: form.newPassword,
      });
      toast.success('Password updated.');
      setForm(EMPTY);
    } catch (e) {
      toast.error(getErrorMessage(e, 'Could not update password'));
    } finally {
      setSaving(false);
    }
  };

  return (
    <Card title="Change password" icon={<KeyRound size={20} />}>
      <form onSubmit={handleSubmit} className="grid grid-cols-1 gap-5 sm:grid-cols-2">
        <div className="sm:col-span-2">
          <Input
            label="Current password"
            type="password"
            value={form.currentPassword}
            onChange={set('currentPassword')}
            error={errors.currentPassword}
            required
          />
        </div>
        <Input
          label="New password"
          type="password"
          value={form.newPassword}
          onChange={set('newPassword')}
          error={errors.newPassword}
          hint="8+ characters, upper, lower, and a number"
          required
        />
        <Input
          label="Confirm new password"
          type="password"
          value={form.confirmPassword}
          onChange={set('confirmPassword')}
          error={errors.confirmPassword}
          required
        />
        <div className="sm:col-span-2">
          <Button type="submit" loading={saving}>
            Update password
          </Button>
        </div>
      </form>
    </Card>
  );
};

export default ChangePasswordCard;