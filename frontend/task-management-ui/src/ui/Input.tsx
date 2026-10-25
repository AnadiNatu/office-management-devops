import { EyeOff, Eye } from "lucide-react";
import { forwardRef, useId, useState } from "react";
import { cn } from "../utils/formatUtils";

export interface InputProps extends Omit<React.InputHTMLAttributes<HTMLInputElement>, 'size'> {
  label?: string;
  error?: string;
  hint?: string;
  leftIcon?: React.ReactNode;
  rightElement?: React.ReactNode;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, hint, leftIcon, rightElement, className, id, type = 'text', required, ...rest }, ref) => {
    const autoId = useId();
    const inputId = id ?? autoId;
    const [show, setShow] = useState(false);
    const isPassword = type === 'password';
    const describedBy = error ? `${inputId}-error` : hint ? `${inputId}-hint` : undefined;

    return (
      <div className="w-full">
        {label && (
          <label
            htmlFor={inputId}
            className="block text-[10px] font-black uppercase tracking-widest text-slate-500 mb-2 px-1"
          >
            {label}
            {required && <span className="text-red-500"> *</span>}
          </label>
        )}
        <div className="relative">
          {leftIcon && (
            <span className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none">
              {leftIcon}
            </span>
          )}
          <input
            ref={ref}
            id={inputId}
            type={isPassword && show ? 'text' : type}
            required={required}
            aria-invalid={!!error}
            aria-describedby={describedBy}
            className={cn(
              'w-full py-3.5 bg-white/70 backdrop-blur border-2 rounded-2xl outline-none font-bold text-sm text-slate-950 placeholder:text-slate-400 placeholder:font-semibold transition-all focus:ring-2 focus:ring-indigo-600 focus:border-transparent disabled:opacity-60 disabled:cursor-not-allowed',
              leftIcon ? 'pl-11' : 'pl-4',
              isPassword || rightElement ? 'pr-12' : 'pr-4',
              error ? 'border-red-300 bg-red-50/60' : 'border-slate-200',
              className
            )}
            {...rest}
          />
          {isPassword ? (
            <button
              type="button"
              onClick={() => setShow((s) => !s)}
              aria-label={show ? 'Hide password' : 'Show password'}
              className="absolute right-3 top-1/2 -translate-y-1/2 p-1.5 rounded-lg text-slate-400 hover:text-slate-700"
            >
              {show ? <EyeOff size={18} /> : <Eye size={18} />}
            </button>
          ) : (
            rightElement && (
              <span className="absolute right-3 top-1/2 -translate-y-1/2">{rightElement}</span>
            )
          )}
        </div>
        {error ? (
          <p id={`${inputId}-error`} role="alert" className="mt-1.5 px-1 text-xs font-bold text-red-600">
            {error}
          </p>
        ) : (
          hint && (
            <p id={`${inputId}-hint`} className="mt-1.5 px-1 text-xs font-semibold text-slate-500">
              {hint}
            </p>
          )
        )}
      </div>
    );
  }
);
Input.displayName = 'Input';

export default Input;