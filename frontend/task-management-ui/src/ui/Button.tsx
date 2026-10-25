import React, { forwardRef } from 'react';
import { cn } from '../utils/formatUtils';
import { Loader2 } from 'lucide-react';

export type ButtonVariant = 'primary' | 'secondary' | 'danger' | 'success' | 'ghost' | 'glass';
export type ButtonSize = 'sm' | 'md' | 'lg';

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant;
  size?: ButtonSize;
  loading?: boolean;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
  fullWidth?: boolean;
}

const VARIANTS: Record<ButtonVariant, string> = {
  primary: 'bg-indigo-600 text-white shadow-lg shadow-indigo-600/25 hover:bg-indigo-700',
  secondary: 'bg-slate-950 text-white shadow-lg shadow-slate-950/20 hover:bg-slate-800',
  danger: 'bg-red-600 text-white shadow-lg shadow-red-600/25 hover:bg-red-700',
  success: 'bg-emerald-600 text-white shadow-lg shadow-emerald-600/25 hover:bg-emerald-700',
  ghost: 'bg-transparent text-slate-700 hover:bg-slate-900/5',
  glass: 'bg-white/60 backdrop-blur-md border border-white/70 text-slate-900 hover:bg-white/85',
};

const SIZES: Record<ButtonSize, string> = {
  sm: 'px-4 py-2 text-[10px] rounded-xl gap-1.5',
  md: 'px-6 py-3 text-xs rounded-2xl gap-2',
  lg: 'px-8 py-4 text-xs rounded-2xl gap-2.5',
};

export const Button = forwardRef<HTMLButtonElement, ButtonProps>(
  (
    {
      variant = 'primary',
      size = 'md',
      loading = false,
      leftIcon,
      rightIcon,
      fullWidth,
      disabled,
      className,
      children,
      type = 'button',
      ...rest
    },
    ref
  ) => (
    <button
      ref={ref}
      type={type}
      disabled={disabled || loading}
      aria-busy={loading || undefined}
      className={cn(
        'inline-flex items-center justify-center font-black uppercase tracking-widest transition-all active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed disabled:active:scale-100',
        VARIANTS[variant],
        SIZES[size],
        fullWidth && 'w-full',
        className
      )}
      {...rest}
    >
      {loading ? <Loader2 size={16} className="animate-spin" /> : leftIcon}
      {children}
      {!loading && rightIcon}
    </button>
  )
);
Button.displayName = 'Button';

export default Button;