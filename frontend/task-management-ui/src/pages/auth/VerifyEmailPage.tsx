import { MailCheck } from "lucide-react";
import { useState } from "react";
import { useSearchParams, useNavigate, Link } from "react-router-dom";
import { authService } from "../../api";
import { useAuth } from "../../hooks/useAuth";
import  { ROUTES } from "../../routes/routeConstants";
import Button from "../../ui/Button";
import Input from "../../ui/Input";
import { useToast } from "../../ui/Toast";
import  { getErrorMessage } from "../../utils/errorUtils";
import { compose, required, validateForm, otpCode, hasErrors, email } from "../../utils/validationUtils";



interface FormState {
  email: string;
  otp: string;
}

const VerifyEmailPage: React.FC = () => {
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const toast = useToast();
  const { isAuthenticated, updateUser } = useAuth();
  const [form, setForm] = useState<FormState>({ email: params.get('email') ?? '', otp: '' });
  const [errors, setErrors] = useState<Partial<FormState>>({});
  const [sending, setSending] = useState(false);
  const [verifying, setVerifying] = useState(false);

  const set = (key: keyof FormState) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm((f) => ({ ...f, [key]: e.target.value }));

  const handleSend = async () => {
    const err = compose(required('Email'), email)(form.email);
    setErrors((prev) => ({ ...prev, email: err }));
    if (err) return;
    setSending(true);
    try {
      await authService.sendVerificationOtp({ email: form.email });
      toast.success('Verification code sent.');
    } catch (e) {
      toast.error(getErrorMessage(e, 'Could not send code'));
    } finally {
      setSending(false);
    }
  };

  const handleVerify = async (e: React.FormEvent) => {
    e.preventDefault();
    const nextErrors = validateForm(form, {
      email: compose(required('Email'), email),
      otp: compose(required('Code'), otpCode),
    });
    setErrors(nextErrors);
    if (hasErrors(nextErrors)) return;

    setVerifying(true);
    try {
      await authService.verifyEmail({ email: form.email, otp: form.otp });
      if (isAuthenticated) updateUser({ emailVerified: true });
      toast.success('Email verified.');
      navigate(isAuthenticated ? ROUTES.DASHBOARD : ROUTES.LOGIN, { replace: true });
    } catch (e) {
      toast.error(getErrorMessage(e, 'Verification failed'));
    } finally {
      setVerifying(false);
    }
  };

  return (
    <div className="space-y-8">
      <div className="text-center">
        <div className="mx-auto mb-4 w-fit rounded-3xl bg-indigo-100 p-5 text-indigo-600">
          <MailCheck size={32} />
        </div>
        <h2 className="text-2xl font-black uppercase tracking-tight text-slate-950">Verify your email</h2>
        <p className="mt-1 text-sm font-bold text-slate-600">Enter the code we sent to your inbox</p>
      </div>
      <form onSubmit={handleVerify} className="space-y-5">
        <Input label="Email" type="email" value={form.email} onChange={set('email')} error={errors.email} required />
        <div className="flex items-end gap-3">
          <div className="flex-1">
            <Input label="Verification code" value={form.otp} onChange={set('otp')} error={errors.otp} inputMode="numeric" required />
          </div>
          <Button type="button" variant="secondary" loading={sending} onClick={handleSend}>
            Send code
          </Button>
        </div>
        <Button type="submit" fullWidth size="lg" loading={verifying}>
          Verify email
        </Button>
      </form>
      {!isAuthenticated && (
        <Link to={ROUTES.LOGIN} className="block text-center text-xs font-black uppercase tracking-widest text-indigo-600">
          Back to sign in
        </Link>
      )}
    </div>
  );
};

export default VerifyEmailPage;

