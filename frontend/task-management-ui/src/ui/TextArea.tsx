import { forwardRef, useId } from "react";
import { cn } from "../utils/formatUtils";

export interface TextAreaProps extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string;
  error?: string;
  hint?: string;
  showCount?: boolean;
}

export const TextArea = forwardRef<HTMLTextAreaElement, TextAreaProps>(
  ({ label, error, hint, showCount, className, id, required, rows = 4, value, maxLength, ...rest }, ref) => {
    const autoId = useId();
    const areaId = id ?? autoId;
    const describedBy = error ? `${areaId}-error` : hint ? `${areaId}-hint` : undefined;
    const length = typeof value === 'string' ? value.length : 0;

    return (
      <div className="w-full">
        {label && (
          <label
            htmlFor={areaId}
            className="block text-[10px] font-black uppercase tracking-widest text-slate-500 mb-2 px-1"
          >
            {label}
            {required && <span className="text-red-500"> *</span>}
          </label>
        )}
        <textarea
          ref={ref}
          id={areaId}
          rows={rows}
          value={value}
          maxLength={maxLength}
          required={required}
          aria-invalid={!!error}
          aria-describedby={describedBy}
          className={cn(
            'w-full p-4 bg-white/70 backdrop-blur border-2 rounded-3xl outline-none font-bold text-sm text-slate-950 placeholder:text-slate-400 placeholder:font-semibold resize-none transition-all focus:ring-2 focus:ring-indigo-600 focus:border-transparent disabled:opacity-60',
            error ? 'border-red-300 bg-red-50/60' : 'border-slate-200',
            className
          )}
          {...rest}
        />
        <div className="mt-1.5 px-1 flex justify-between gap-4">
          {error ? (
            <p id={`${areaId}-error`} role="alert" className="text-xs font-bold text-red-600">
              {error}
            </p>
          ) : (
            <p id={`${areaId}-hint`} className="text-xs font-semibold text-slate-500">
              {hint}
            </p>
          )}
          {showCount && maxLength && (
            <span className="text-[10px] font-black text-slate-400 tabular-nums">
              {length}/{maxLength}
            </span>
          )}
        </div>
      </div>
    );
  }
);
TextArea.displayName = 'TextArea';

export default TextArea;