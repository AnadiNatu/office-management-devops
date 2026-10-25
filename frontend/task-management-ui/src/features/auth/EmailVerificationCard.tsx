import { MailCheck, ShieldCheck } from "lucide-react";
import { useState } from "react";
import  { authService } from "../../api";
import { useAuth } from "../../hooks/useAuth";
import Button from "../../ui/Button";
import Card from "../../ui/Card";
import Input from "../../ui/Input";
import { useToast } from "../../ui/Toast";
import { getErrorMessage } from "../../utils/errorUtils";
import { otpCode } from "../../utils/validationUtils";
import Badge from "../../ui/Badge";

export const EmailVerificationCard: React.FC = () => {
  const { user, updateUser } = useAuth();
  const toast = useToast();
  const [otp, setOtp] = useState('');
  const [error, setError] = useState<string>();
  const [sending, setSending] = useState(false);
  const [verifying, setVerifying] = useState(false);

  if (!user) return null;

  const handleSend = async () => {
    setSending(true);
    try {
      await authService.sendVerificationOtp({ email: user.email });
      toast.success(`Verification code sent to ${user.email}.`);
    } catch (e) {
      toast.error(getErrorMessage(e, 'Could not send verification code'));
    } finally {
      setSending(false);
    }
  };

  const handleVerify = async (e: React.FormEvent) => {
    e.preventDefault();
    const err = otpCode(otp) ?? (otp ? undefined : 'Enter the code you received');
    setError(err);
    if (err) return;
    setVerifying(true);
    try {
      await authService.verifyEmail({ email: user.email, otp });
      updateUser({ emailVerified: true });
      toast.success('Email verified.');
      setOtp('');
    } catch (e) {
      toast.error(getErrorMessage(e, 'Verification failed'));
    } finally {
      setVerifying(false);
    }
  };

  return (
    <Card
      title="Email verification"
      icon={<MailCheck size={20} />}
      actions={
        user.emailVerified ? (
          <Badge tone="green" dot>
            Verified
          </Badge>
        ) : (
          <Badge tone="amber" dot>
            Unverified
          </Badge>
        )
      }
    >
      <p className="mb-5 text-sm font-bold text-slate-600">{user.email}</p>
      {user.emailVerified ? (
        <div className="flex items-center gap-2 text-sm font-bold text-emerald-700">
          <ShieldCheck size={18} /> Your email is verified.
        </div>
      ) : (
        <form onSubmit={handleVerify} className="flex flex-col gap-4 sm:flex-row sm:items-end">
          <div className="flex-1">
            <Input
              label="Verification code"
              placeholder="Enter code"
              value={otp}
              onChange={(e) => setOtp(e.target.value)}
              error={error}
              inputMode="numeric"
            />
          </div>
          <div className="flex gap-3">
            <Button type="button" variant="secondary" loading={sending} onClick={handleSend}>
              Send code
            </Button>
            <Button type="submit" loading={verifying}>
              Verify
            </Button>
          </div>
        </form>
      )}
    </Card>
  );
};

export default EmailVerificationCard;