import { UserPlus } from "lucide-react";
import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { authService } from "../../api";
import { ROUTES } from "../../routes/routeConstants";
import Button from "../../ui/Button";
import Input from "../../ui/Input";
import Select from "../../ui/Select";
import { useToast } from "../../ui/Toast";
import { getErrorMessage } from "../../utils/errorUtils";
import { validateForm, required, compose, strongPassword, matches, hasErrors, phone, email } from "../../utils/validationUtils";

interface FormState {
  fname: string;
  lname: string;
  email: string;
  phoneNumber: string;
  password: string;
  confirmPassword: string;
  role: string;
}

const EMPTY: FormState = {
  fname: '',
  lname: '',
  email: '',
  phoneNumber: '',
  password: '',
  confirmPassword: '',
  role: 'ROLE_EMPLOYEE',
};

const SignupPage: React.FC = () => {
  const toast = useToast();
  const navigate = useNavigate();
  const [form, setForm] = useState<FormState>(EMPTY);
  const [errors, setErrors] = useState<Partial<Record<keyof FormState, string>>>({});
  const [submitting, setSubmitting] = useState(false);

  const set = (key: keyof FormState) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm((f) => ({ ...f, [key]: e.target.value }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const nextErrors = validateForm(form, {
      fname: required('First name'),
      lname: required('Last name'),
      email: compose(required('Email'), email),
      phoneNumber: form.phoneNumber ? phone : undefined,
      password: compose(required('Password'), strongPassword),
      confirmPassword: matches(() => form.password, 'Passwords do not match'),
    });
    setErrors(nextErrors);
    if (hasErrors(nextErrors)) return;

    setSubmitting(true);
    try {
      await authService.signup({
        fname: form.fname,
        lname: form.lname,
        email: form.email,
        password: form.password,
        phoneNumber: form.phoneNumber || undefined,
        role: form.role,
      });
      toast.success('Account created. Verify your email to continue.');
      navigate(`${ROUTES.VERIFY_EMAIL}?email=${encodeURIComponent(form.email)}`);
    } catch (e) {
      toast.error(getErrorMessage(e, 'Could not create account'));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="space-y-8">
      <div className="text-center">
        <h2 className="text-2xl font-black uppercase tracking-tight text-slate-950">Create account</h2>
        <p className="mt-1 text-sm font-bold text-slate-600">Join your organization's workspace</p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-5">
        <div className="grid grid-cols-2 gap-4">
          <Input label="First name" value={form.fname} onChange={set('fname')} error={errors.fname} required />
          <Input label="Last name" value={form.lname} onChange={set('lname')} error={errors.lname} required />
        </div>
        <Input
          label="Email"
          type="email"
          value={form.email}
          onChange={set('email')}
          error={errors.email}
          autoComplete="username"
          required
        />
        <Input
          label="Phone number"
          placeholder="Optional"
          value={form.phoneNumber}
          onChange={set('phoneNumber')}
          error={errors.phoneNumber}
        />
        <div className="grid grid-cols-2 gap-4">
          <Input
            label="Password"
            type="password"
            value={form.password}
            onChange={set('password')}
            error={errors.password}
            autoComplete="new-password"
            required
          />
          <Input
            label="Confirm password"
            type="password"
            value={form.confirmPassword}
            onChange={set('confirmPassword')}
            error={errors.confirmPassword}
            autoComplete="new-password"
            required
          />
        </div>
        <Select
          label="Account type"
          value={form.role}
          onChange={(e) => setForm((f) => ({ ...f, role: e.target.value }))}
          options={[
            { label: 'Employee', value: 'ROLE_EMPLOYEE' },
            { label: 'General user', value: 'ROLE_USER' },
          ]}
        />
        <Button type="submit" fullWidth size="lg" loading={submitting} leftIcon={<UserPlus size={18} />}>
          Create account
        </Button>
      </form>

      <p className="text-center text-xs font-bold text-slate-500">
        Already have an account?{' '}
        <Link to={ROUTES.LOGIN} className="font-black text-indigo-600 hover:text-indigo-700">
          Sign in
        </Link>
      </p>
    </div>
  );
};

export default SignupPage;

// function emailValidator(value: string): string | undefined {
//     throw new Error("Function not implemented. Value not used: " + value);
// }


// function phoneValidator(value: string): string | undefined {
//     throw new Error("Function not implemented. Value not used: " + value);
// }
