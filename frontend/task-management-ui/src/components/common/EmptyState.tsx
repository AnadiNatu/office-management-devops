import { Inbox } from "lucide-react";
import { cn } from "../../utils/formatUtils";

interface EmptyStateProps {
  title: string;
  description?: string;
  icon?: React.ReactNode;
  action?: React.ReactNode;
  className?: string;
}

export const EmptyState: React.FC<EmptyStateProps> = ({
  title,
  description,
  icon,
  action,
  className,
}) => (
  <div
    className={cn(
      'flex flex-col items-center gap-4 rounded-[2.5rem] border-4 border-dashed border-slate-200 px-6 py-16 text-center',
      className
    )}
  >
    <div className="text-slate-300">{icon ?? <Inbox size={56} />}</div>
    <div>
      <h3 className="text-sm font-black uppercase tracking-[0.3em] text-slate-400">{title}</h3>
      {description && <p className="mt-2 max-w-md text-sm font-bold text-slate-500">{description}</p>}
    </div>
    {action}
  </div>
);

export default EmptyState;