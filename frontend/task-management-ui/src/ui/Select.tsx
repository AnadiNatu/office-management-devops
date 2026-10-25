import { ChevronDown } from "lucide-react";
import { forwardRef, useId } from "react";
import type { SelectOption } from "../types/common";
import { cn } from "../utils/formatUtils";

export interface SelectProps extends Omit<React.SelectHTMLAttributes<HTMLSelectElement>, 'size'> {
  label?: string;
  error?: string;
  hint?: string;
  options?: SelectOption[];
  placeholder?: string;
}

export const Select = forwardRef<HTMLSelectElement, SelectProps>(
  ({ label, error, hint, options, placeholder, className, id, required, children, ...rest }, ref) => {
    const autoId = useId();
    const selectId = id ?? autoId;
    const describedBy = error ? `${selectId}-error` : hint ? `${selectId}-hint` : undefined;

    return (
      <div className="w-full">
        {label && (
          <label
            htmlFor={selectId}
            className="block text-[10px] font-black uppercase tracking-widest text-slate-500 mb-2 px-1"
          >
            {label}
            {required && <span className="text-red-500"> *</span>}
          </label>
        )}
        <div className="relative">
          <select
            ref={ref}
            id={selectId}
            required={required}
            aria-invalid={!!error}
            aria-describedby={describedBy}
            className={cn(
              'w-full appearance-none py-3.5 pl-4 pr-11 bg-white/70 backdrop-blur border-2 rounded-2xl outline-none font-bold text-sm text-slate-950 transition-all focus:ring-2 focus:ring-indigo-600 focus:border-transparent disabled:opacity-60 disabled:cursor-not-allowed',
              error ? 'border-red-300 bg-red-50/60' : 'border-slate-200',
              className
            )}
            {...rest}
          >
            {placeholder && (
              <option value="" disabled={required}>
                {placeholder}
              </option>
            )}
            {options?.map((o) => (
              <option key={String(o.value)} value={o.value}>
                {o.label}
              </option>
            ))}
            {children}
          </select>
          <ChevronDown
            size={18}
            className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none"
          />
        </div>
        {error ? (
          <p id={`${selectId}-error`} role="alert" className="mt-1.5 px-1 text-xs font-bold text-red-600">
            {error}
          </p>
        ) : (
          hint && (
            <p id={`${selectId}-hint`} className="mt-1.5 px-1 text-xs font-semibold text-slate-500">
              {hint}
            </p>
          )
        )}
      </div>
    );
  }
);
Select.displayName = 'Select';

export default Select;