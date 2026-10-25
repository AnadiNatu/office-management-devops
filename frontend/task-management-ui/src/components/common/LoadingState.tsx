import { cn } from "../../utils/formatUtils";

interface LoadingStateProps {
  label?: string;
  variant?: 'spinner' | 'skeleton';
  rows?: number;
  className?: string;
}

export const LoadingState: React.FC<LoadingStateProps> = ({
  label = 'Loading…',
  variant = 'spinner',
  rows = 3,
  className,
}) => {
  if (variant === 'skeleton') {
    return (
      <div className={cn('space-y-4', className)} role="status" aria-label={label}>
        {Array.from({ length: rows }).map((_, i) => (
          <div key={i} className="h-24 animate-pulse rounded-[2rem] bg-slate-900/5" />
        ))}
      </div>
    );
  }
  return (
    <div
      role="status"
      className={cn('flex flex-col items-center justify-center gap-4 py-20', className)}
    >
      <div className="h-10 w-10 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent" />
      <p className="text-[10px] font-black uppercase tracking-[0.3em] text-slate-400">{label}</p>
    </div>
  );
};

export default LoadingState;