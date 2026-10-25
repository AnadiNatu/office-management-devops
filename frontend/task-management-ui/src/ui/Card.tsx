import { motion } from "framer-motion";
import { cn } from "../utils/formatUtils";

export interface CardProps extends Omit<React.HTMLAttributes<HTMLDivElement>, 'title'> {
  title?: React.ReactNode;
  subtitle?: React.ReactNode;
  icon?: React.ReactNode;
  actions?: React.ReactNode;
  variant?: 'glass' | 'solid' | 'dark';
  padded?: boolean;
  hoverable?: boolean;
  animated?: boolean;
}

const VARIANTS = {
  glass: 'glass-panel text-slate-950',
  solid: 'bg-white border border-slate-200 shadow-2xl shadow-slate-300/20 text-slate-950',
  dark: 'bg-slate-950 text-white border border-white/10 shadow-2xl',
} as const;

export const Card: React.FC<CardProps> = ({
  title,
  subtitle,
  icon,
  actions,
  variant = 'glass',
  padded = true,
  hoverable = false,
  animated = false,
  className,
  children,
  ...rest
}) => {
  const Wrapper: any = animated ? motion.div : 'div';
  const motionProps = animated
    ? { initial: { opacity: 0, y: 16 }, animate: { opacity: 1, y: 0 }, transition: { duration: 0.3 } }
    : {};

  return (
    <Wrapper
      className={cn(
        'rounded-[2.5rem] overflow-hidden',
        VARIANTS[variant],
        hoverable &&
          'transition-all hover:-translate-y-0.5 hover:border-indigo-500/60 hover:shadow-indigo-600/10 cursor-pointer',
        className
      )}
      {...motionProps}
      {...rest}
    >
      {(title || actions) && (
        <div
          className={cn(
            'flex items-center justify-between gap-4 border-b',
            variant === 'dark' ? 'border-white/10' : 'border-slate-200/70 bg-white/40',
            'p-6 md:p-8'
          )}
        >
          <div className="flex items-center gap-3 min-w-0">
            {icon && <span className="text-indigo-600 shrink-0">{icon}</span>}
            <div className="min-w-0">
              <h2 className="font-black text-lg md:text-xl uppercase tracking-tight truncate">{title}</h2>
              {subtitle && <p className="text-xs font-bold text-slate-500 mt-0.5">{subtitle}</p>}
            </div>
          </div>
          {actions && <div className="flex items-center gap-2 shrink-0">{actions}</div>}
        </div>
      )}
      <div className={cn(padded && 'p-6 md:p-8')}>{children}</div>
    </Wrapper>
  );
};

export default Card;