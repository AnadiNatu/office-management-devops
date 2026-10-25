import { KeyRound } from "lucide-react";
import { useState } from "react";
import { useSearchParams, useNavigate, Link } from "react-router-dom";
import { authService } from "../../api";
import { ROUTES } from "../../routes/routeConstants";
import Button from "../../ui/Button";
import Input from "../../ui/Input";
import { useToast } from "../../ui/Toast";
import { getErrorMessage } from "../../utils/errorUtils";
import { validateForm, required, compose, otpCode, strongPassword, matches, hasErrors } from "../../utils/validationUtils";


interface FormState {
  identifier: string;
  otp: string;
  newPassword: string;
  confirmPassword: string;
}

const ResetPasswordPage: React.FC = () => {
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const toast = useToast();
  const [form, setForm] = useState<FormState>({
    identifier: params.get('identifier') ?? '',
    otp: '',
    newPassword: '',
    confirmPassword: '',
  });
  const [errors, setErrors] = useState<Partial<Record<keyof FormState, string>>>({});
  const [submitting, setSubmitting] = useState(false);

  const set = (key: keyof FormState) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm((f) => ({ ...f, [key]: e.target.value }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const nextErrors = validateForm(form, {
      identifier: required('Email'),
      otp: compose(required('Code'), otpCode),
      newPassword: compose(required('New password'), strongPassword),
      confirmPassword: matches(() => form.newPassword, 'Passwords do not match'),
    });
    setErrors(nextErrors);
    if (hasErrors(nextErrors)) return;

    setSubmitting(true);
    try {
      await authService.resetPassword({
        identifier: form.identifier,
        otp: form.otp,
        newPassword: form.newPassword,
      });
      toast.success('Password reset. Sign in with your new password.');
      navigate(ROUTES.LOGIN, { replace: true });
    } catch (e) {
      toast.error(getErrorMessage(e, 'Could not reset password'));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="space-y-8">
      <div className="text-center">
        <h2 className="text-2xl font-black uppercase tracking-tight text-slate-950">Reset password</h2>
        <p className="mt-1 text-sm font-bold text-slate-600">Enter the code sent to your email</p>
      </div>
      <form onSubmit={handleSubmit} className="space-y-5">
        <Input label="Email" type="email" value={form.identifier} onChange={set('identifier')} error={errors.identifier} required />
        <Input label="Reset code" value={form.otp} onChange={set('otp')} error={errors.otp} inputMode="numeric" required />
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
        <Button type="submit" fullWidth size="lg" loading={submitting} leftIcon={<KeyRound size={18} />}>
          Reset password
        </Button>
      </form>
      <Link to={ROUTES.LOGIN} className="block text-center text-xs font-black uppercase tracking-widest text-indigo-600">
        Back to sign in
      </Link>
    </div>
  );
};

export default ResetPasswordPage;