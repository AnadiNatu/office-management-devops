import { Smartphone } from "lucide-react";
import { useState } from "react";
import { authService } from "../../api";
import Button from "../../ui/Button";
import Card from "../../ui/Card";
import Input from "../../ui/Input";
import { useToast } from "../../ui/Toast";
import  { getErrorMessage } from "../../utils/errorUtils";
import { otpCode, phone } from "../../utils/validationUtils";

export const PhoneVerificationCard: React.FC = () => {
  const toast = useToast();
  const [phones, setPhone] = useState('');
  const [otp, setOtp] = useState('');
  const [otpSent, setOtpSent] = useState(false);
  const [errors, setErrors] = useState<{ phones?: string; otp?: string }>({});
  const [sending, setSending] = useState(false);
  const [verifying, setVerifying] = useState(false);

  const handleSend = async () => {
    const err = phone(phones) ?? (phones ? undefined : 'Enter a phone number');
    setErrors({ phones: err });
    if (err) return;
    setSending(true);
    try {
      await authService.sendPhoneOtp({ phoneNumber: phones });
      setOtpSent(true);
      toast.success(`Verification code sent to ${phone}.`);
    } catch (e) {
      toast.error(getErrorMessage(e, 'Could not send verification code'));
    } finally {
      setSending(false);
    }
  };

  const handleVerify = async (e: React.FormEvent) => {
    e.preventDefault();
    const err = otpCode(otp) ?? (otp ? undefined : 'Enter the code you received');
    setErrors((prev) => ({ ...prev, otp: err }));
    if (err) return;
    setVerifying(true);
    try {
      await authService.verifyPhoneOtp({ identifier: phones, otp });
      toast.success('Phone number verified.');
      setOtp('');
      setOtpSent(false);
    } catch (e) {
      toast.error(getErrorMessage(e, 'Verification failed'));
    } finally {
      setVerifying(false);
    }
  };

  return (
    <Card title="Phone verification" icon={<Smartphone size={20} />} subtitle="Verify a phone number for two-factor recovery">
      <form onSubmit={handleVerify} className="space-y-4">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-end">
          <div className="flex-1">
            <Input
              label="Phone number"
              placeholder="+91XXXXXXXXXX"
              value={phones}
              onChange={(e) => setPhone(e.target.value)}
              error={errors.phones}
            />
          </div>
          <Button type="button" variant="secondary" loading={sending} onClick={handleSend}>
            {otpSent ? 'Resend code' : 'Send code'}
          </Button>
        </div>
        {otpSent && (
          <div className="flex flex-col gap-4 sm:flex-row sm:items-end">
            <div className="flex-1">
              <Input
                label="Verification code"
                placeholder="Enter code"
                value={otp}
                onChange={(e) => setOtp(e.target.value)}
                error={errors.otp}
                inputMode="numeric"
              />
            </div>
            <Button type="submit" loading={verifying}>
              Verify
            </Button>
          </div>
        )}
      </form>
    </Card>
  );
};

export default PhoneVerificationCard;

// function phoneValidator(phone: string) {
//     throw new Error(`Function not implemented. For the phone number  ${phone}`);
// }
