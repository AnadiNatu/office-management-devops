import { CheckCircle2, Mail, Send } from "lucide-react";
import  { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { authService } from "../../api";
import  { ROUTES } from "../../routes/routeConstants";
import Button from "../../ui/Button";
import Input from "../../ui/Input";
import { getErrorMessage } from "../../utils/errorUtils";
import { compose, required } from "../../utils/validationUtils";


const ForgotPasswordPage: React.FC = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [error, setError] = useState<string>();
  const [apiError, setApiError] = useState<string>();
  const [sent, setSent] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setApiError(undefined);
    const err = compose(required('Email'), emailValidator)(email);
    setError(err);
    if (err) return;

    setSubmitting(true);
    try {
      await authService.forgotPassword({ email });
      setSent(true);
    } catch (e) {
      setApiError(getErrorMessage(e, 'Could not send reset instructions'));
    } finally {
      setSubmitting(false);
    }
  };

  if (sent) {
    return (
      <div className="space-y-6 text-center">
        <div className="mx-auto w-fit rounded-3xl bg-emerald-100 p-5 text-emerald-600">
          <CheckCircle2 size={36} />
        </div>
        <div>
          <h2 className="text-2xl font-black uppercase tracking-tight text-slate-950">Check your inbox</h2>
          <p className="mt-2 text-sm font-bold text-slate-600">
            If an account exists for {email}, a reset code has been sent.
          </p>
        </div>
        <Button fullWidth onClick={() => navigate(`${ROUTES.RESET_PASSWORD}?identifier=${encodeURIComponent(email)}`)}>
          Enter reset code
        </Button>
        <Link to={ROUTES.LOGIN} className="block text-xs font-black uppercase tracking-widest text-indigo-600">
          Back to sign in
        </Link>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      <div className="text-center">
        <h2 className="text-2xl font-black uppercase tracking-tight text-slate-950">Forgot password</h2>
        <p className="mt-1 text-sm font-bold text-slate-600">We'll send a reset code to your email</p>
      </div>
      <form onSubmit={handleSubmit} className="space-y-5">
        <Input
          label="Email"
          type="email"
          leftIcon={<Mail size={18} />}
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          error={error ?? apiError}
          required
        />
        <Button type="submit" fullWidth size="lg" loading={submitting} leftIcon={<Send size={18} />}>
          Send reset code
        </Button>
      </form>
      <Link
        to={ROUTES.LOGIN}
        className="block text-center text-xs font-black uppercase tracking-widest text-indigo-600"
      >
        Back to sign in
      </Link>
    </div>
  );
};

export default ForgotPasswordPage;

function emailValidator(value: string): string | undefined {
    throw new Error(`Function not implemented. Value not used: ${value}`);
}
