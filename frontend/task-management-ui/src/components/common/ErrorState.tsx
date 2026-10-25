import { AlertTriangle, RefreshCw } from "lucide-react";
import Button from "../../ui/Button";
import { cn } from "../../utils/formatUtils";

interface ErrorStateProps {
  message: string;
  title?: string;
  onRetry?: () => void;
  className?: string;
}

export const ErrorState: React.FC<ErrorStateProps> = ({
  message,
  title = 'Something went wrong',
  onRetry,
  className,
}) => (
  <div
    role="alert"
    className={cn('flex flex-col items-center gap-4 px-6 py-16 text-center', className)}
  >
    <div className="rounded-3xl bg-red-100 p-5 text-red-600">
      <AlertTriangle size={32} />
    </div>
    <div>
      <h3 className="text-lg font-black uppercase tracking-tight text-slate-950">{title}</h3>
      <p className="mt-1 max-w-md text-sm font-bold text-slate-600">{message}</p>
    </div>
    {onRetry && (
      <Button variant="secondary" size="sm" leftIcon={<RefreshCw size={14} />} onClick={onRetry}>
        Try again
      </Button>
    )}
  </div>
);

export default ErrorState;