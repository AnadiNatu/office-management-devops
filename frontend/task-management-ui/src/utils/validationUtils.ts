export type Validator = (value: string) => string | undefined;

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;
const PHONE_RE = /^\+?[0-9]{7,15}$/;

export const required = (label = 'This field'): Validator => (v) =>
  v && v.trim() ? undefined : `${label} is required`;

export const email: Validator = (v) =>
  !v || EMAIL_RE.test(v.trim()) ? undefined : 'Enter a valid email address';

export const phone: Validator = (v) =>
  !v || PHONE_RE.test(v.replace(/[\s-]/g, ''))
    ? undefined
    : 'Enter a valid phone number (7–15 digits, optional +)';

export const minLength = (n: number, label = 'This field'): Validator => (v) =>
  !v || v.length >= n ? undefined : `${label} must be at least ${n} characters`;

export const maxLength = (n: number, label = 'This field'): Validator => (v) =>
  !v || v.length <= n ? undefined : `${label} must be at most ${n} characters`;

export const strongPassword: Validator = (v) => {
  if (!v) return undefined;
  if (v.length < 8) return 'Password must be at least 8 characters';
  if (!/[a-z]/.test(v)) return 'Include a lowercase letter';
  if (!/[A-Z]/.test(v)) return 'Include an uppercase letter';
  if (!/[0-9]/.test(v)) return 'Include a number';
  return undefined;
};

export const otpCode: Validator = (v) =>
  !v || /^[0-9]{4,8}$/.test(v.trim()) ? undefined : 'Enter the numeric code you received';

export const matches = (other: () => string, message = 'Values do not match'): Validator => (v) =>
  v === other() ? undefined : message;

export const compose = (...validators: Validator[]): Validator => (v) => {
  for (const fn of validators) {
    const err = fn(v);
    if (err) return err;
  }
  return undefined;
};

export type FormErrors<T> = Partial<Record<keyof T, string>>;
export const validateForm = <T extends object>(
  values: T,
  rules: Partial<Record<keyof T, Validator>>
): FormErrors<T> => {
  const errors: FormErrors<T> = {};

  (Object.keys(rules) as Array<keyof T>).forEach((key) => {
    const value = values[key];

    const err = rules[key]?.(
      typeof value === 'string' ? value : String(value ?? '')
    );

    if (err) {
      errors[key] = err;
    }
  });

  return errors;
};

export const hasErrors = (errors: object): boolean => Object.keys(errors).length > 0;