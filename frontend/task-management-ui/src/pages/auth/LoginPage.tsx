import { Mail, LogIn, BriefcaseBusiness, ShieldCheck, UserRound, Users } from "lucide-react";
import { useState } from "react";
import { useNavigate, useLocation, Link } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";
import { ROUTES } from "../../routes/routeConstants";
import Button from "../../ui/Button";
import Input from "../../ui/Input";
import { useToast } from "../../ui/Toast";
import { getErrorMessage } from "../../utils/errorUtils";
import { validateForm, compose, required, email, hasErrors } from "../../utils/validationUtils";

// interface FormState {
//   email: string;
//   password: string;
// }

// const LoginPage: React.FC = () => {
//   const { login } = useAuth();
//   const toast = useToast();
//   const navigate = useNavigate();
//   const location = useLocation();
//   const [form, setForm] = useState<FormState>({ email: '', password: '' });
//   const [errors, setErrors] = useState<Partial<FormState>>({});
//   const [submitting, setSubmitting] = useState(false);

//   const set = (key: keyof FormState) => (e: React.ChangeEvent<HTMLInputElement>) =>
//     setForm((f) => ({ ...f, [key]: e.target.value }));

//   const handleSubmit = async (e: React.FormEvent) => {
//     e.preventDefault();
//     const nextErrors = validateForm(form, {
//       email: compose(required('Email'), email),
//       password: required('Password'),
//     });
//     setErrors(nextErrors);
//     if (hasErrors(nextErrors)) return;

//     setSubmitting(true);
//     try {
//       await login({ username: form.email, password: form.password });
//       const from = (location.state as { from?: { pathname?: string } } | null)?.from?.pathname;
//       navigate(from ?? ROUTES.DASHBOARD, { replace: true });
//     } catch (e) {
//       toast.error(getErrorMessage(e, 'Invalid email or password'));
//     } finally {
//       setSubmitting(false);
//     }
//   };

//   return (
//     <div className="space-y-8">
//       <div className="text-center">
//         <h2 className="text-2xl font-black uppercase tracking-tight text-slate-950">Welcome back</h2>
//         <p className="mt-1 text-sm font-bold text-slate-600">Sign in to your workspace</p>
//       </div>

//       <form onSubmit={handleSubmit} className="space-y-5">
//         <Input
//           label="Email"
//           type="email"
//           leftIcon={<Mail size={18} />}
//           value={form.email}
//           onChange={set('email')}
//           error={errors.email}
//           autoComplete="username"
//           required
//         />
//         <div>
//           <Input
//             label="Password"
//             type="password"
//             value={form.password}
//             onChange={set('password')}
//             error={errors.password}
//             autoComplete="current-password"
//             required
//           />
//           <Link
//             to={ROUTES.FORGOT_PASSWORD}
//             className="mt-2 inline-block px-1 text-xs font-black uppercase tracking-widest text-indigo-600 hover:text-indigo-700"
//           >
//             Forgot password?
//           </Link>
//         </div>
//         <Button type="submit" fullWidth size="lg" loading={submitting} leftIcon={<LogIn size={18} />}>
//           Sign in
//         </Button>
//       </form>

//       <p className="text-center text-xs font-bold text-slate-500">
//         Don't have an account?{' '}
//         <Link to={ROUTES.SIGNUP} className="font-black text-indigo-600 hover:text-indigo-700">
//           Create one
//         </Link>
//       </p>
//     </div>
//   );
// };

// export default LoginPage;

interface FormState {
  email: string;
  password: string;
}

type DemoAccount = {
  label: string;
  role: string;
  email: string;
  password: string;
  icon: React.ReactNode;
};

const DEMO_ACCOUNTS: DemoAccount[] = [
  {
    label: "Admin",
    role: "Administrator",
    email: "anadinatu2001+admin@gmail.com",
    password: "Password1234",
    icon: <ShieldCheck size={19} />,
  },
  {
    label: "Manager",
    role: "Manager",
    email: "anadinatu2001+manager@gmail.com",
    password: "password123",
    icon: <BriefcaseBusiness size={19} />,
  },
  {
    label: "Manager 2",
    role: "Manager",
    email: "anadinatu2001+manager2@gmail.com",
    password: "password123",
    icon: <BriefcaseBusiness size={19} />,
  },
  {
    label: "Employee",
    role: "Employee",
    email: "anadinatu2001+employee@gmail.com",
    password: "password123",
    icon: <UserRound size={19} />,
  },
  {
    label: "Employee 2",
    role: "Employee",
    email: "anadinatu2001+employee2@gmail.com",
    password: "password123",
    icon: <Users size={19} />,
  },
  {
    label: "Employee 3",
    role: "Employee",
    email: "anadinatu2001+employee3@gmail.com",
    password: "password123",
    icon: <Users size={19} />,
  },
  {
    label: "Employee 4",
    role: "Employee",
    email: "anadinatu2001+employee4@gmail.com",
    password: "password123",
    icon: <Users size={19} />,
  },
];

const LoginPage: React.FC = () => {
  const { login } = useAuth();
  const toast = useToast();
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState<FormState>({ email: "", password: "" });
  const [errors, setErrors] = useState<Partial<FormState>>({});
  const [submitting, setSubmitting] = useState(false);
  const [activeDemoAccount, setActiveDemoAccount] = useState<string | null>(null);

  const set = (key: keyof FormState) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm((current) => ({ ...current, [key]: e.target.value }));

  const navigateAfterLogin = () => {
    const from = (location.state as { from?: { pathname?: string } } | null)?.from
      ?.pathname;
    navigate(from ?? ROUTES.DASHBOARD, { replace: true });
  };

  const authenticate = async (credentials: FormState, demoLabel?: string) => {
    if (submitting) return;

    setSubmitting(true);
    setActiveDemoAccount(demoLabel ?? null);

    try {
      await login({ username: credentials.email, password: credentials.password });
      navigateAfterLogin();
    } catch (error) {
      toast.error(
        getErrorMessage(
          error,
          demoLabel
            ? `Unable to sign in with the ${demoLabel} demo account. Please verify that the account is active.`
            : "Invalid email or password",
        ),
      );
    } finally {
      setSubmitting(false);
      setActiveDemoAccount(null);
    }
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    const nextErrors = validateForm(form, {
      email: compose(required("Email"), email),
      password: required("Password"),
    });

    setErrors(nextErrors);
    if (hasErrors(nextErrors)) return;

    await authenticate(form);
  };

  const handleDemoLogin = (account: DemoAccount) => {
    setForm({ email: account.email, password: account.password });
    setErrors({});
    void authenticate(
      { email: account.email, password: account.password },
      account.label,
    );
  };

  return (
    <div className="mx-auto w-full max-w-xl space-y-7">
      <div className="text-center">
        <h2 className="text-2xl font-black uppercase tracking-tight text-slate-950">
          Welcome back
        </h2>
        <p className="mt-1 text-sm font-medium text-slate-600">
          Sign in to your workspace
        </p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-4">
        <Input
          label="Email"
          type="email"
          leftIcon={<Mail size={18} />}
          value={form.email}
          onChange={set("email")}
          error={errors.email}
          autoComplete="username"
          required
        />

        <div>
          <Input
            label="Password"
            type="password"
            value={form.password}
            onChange={set("password")}
            error={errors.password}
            autoComplete="current-password"
            required
          />
          <Link
            to={ROUTES.FORGOT_PASSWORD}
            className="mt-2 inline-block px-1 text-xs font-bold text-indigo-600 transition hover:text-indigo-700"
          >
            Forgot password?
          </Link>
        </div>

        <Button
          type="submit"
          fullWidth
          size="lg"
          loading={submitting && activeDemoAccount === null}
          leftIcon={<LogIn size={18} />}
        >
          Sign in
        </Button>
      </form>

      <section className="rounded-2xl border border-slate-200 bg-slate-50 p-4 sm:p-5">
        <div className="mb-5 text-center">
          <h3 className="text-sm font-extrabold uppercase tracking-wider text-slate-900 sm:text-base">
            Explore with a demo account
          </h3>
          <p className="mx-auto mt-1 max-w-sm text-sm leading-5 text-slate-600">
            Pick a role to sign in instantly. Credentials are filled in and submitted for you.
          </p>
        </div>

        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
          {DEMO_ACCOUNTS.map((account) => {
            const isSigningIn =
              submitting && activeDemoAccount === account.label;

            return (
              <div
                key={account.email}
                className="flex min-w-0 flex-col rounded-xl border border-slate-200 bg-white p-3 shadow-sm transition hover:border-indigo-200 hover:shadow-md"
              >
                <div className="flex min-w-0 items-center gap-3">
                  <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-indigo-50 text-indigo-600">
                    {account.icon}
                  </span>
                  <span className="min-w-0 flex-1">
                    <span className="block break-words text-sm font-bold leading-5 text-slate-900">
                      {account.label}
                    </span>
                    <span className="mt-0.5 block text-xs leading-4 text-slate-500">
                      {account.role}
                    </span>
                  </span>
                </div>

                <button
                  type="button"
                  onClick={() => handleDemoLogin(account)}
                  disabled={submitting}
                  className="mt-3 inline-flex min-h-9 w-full items-center justify-center gap-2 rounded-lg bg-indigo-50 px-3 py-2 text-sm font-bold text-indigo-700 transition hover:bg-indigo-600 hover:text-white focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-60"
                  aria-label={`Sign in automatically as ${account.role} ${account.label}`}
                >
                  {isSigningIn ? (
                    <>
                      <span className="h-3.5 w-3.5 animate-spin rounded-full border-2 border-current border-t-transparent" />
                      Signing in…
                    </>
                  ) : (
                    <>
                      <LogIn size={15} />
                      Quick login
                    </>
                  )}
                </button>
              </div>
            );
          })}
        </div>

        <p className="mt-4 text-center text-xs leading-5 text-slate-500">
          Public demo accounts are for reviewing this application only. Please
          don&apos;t store personal or sensitive information in them.
        </p>
      </section>

      <p className="text-center text-xs font-medium text-slate-500">
        Don&apos;t have an account?{" "}
        <Link
          to={ROUTES.SIGNUP}
          className="font-bold text-indigo-600 transition hover:text-indigo-700"
        >
          Create one
        </Link>
      </p>
    </div>
  );
};

export default LoginPage;
