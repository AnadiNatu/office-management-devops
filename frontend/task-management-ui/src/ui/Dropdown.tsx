import { AnimatePresence, motion } from "framer-motion";
import { createContext, useState, useRef, useCallback, useEffect, useContext } from "react";
import { useClickOutside } from "../hooks/useCommon";
import { cn } from "../utils/formatUtils";

const MotionDiv = motion.div as any;

const DropdownContext = createContext<{ close: () => void }>({ close: () => undefined });

export interface DropdownProps {
  trigger: (ctx: { open: boolean; toggle: () => void }) => React.ReactNode;
  children: React.ReactNode | ((ctx: { close: () => void }) => React.ReactNode);
  align?: 'left' | 'right';
  panelClassName?: string;
}

export const Dropdown: React.FC<DropdownProps> = ({
  trigger,
  children,
  align = 'right',
  panelClassName,
}) => {
  const [open, setOpen] = useState(false);
  const ref = useRef<HTMLDivElement>(null);
  const close = useCallback(() => setOpen(false), []);
  const toggle = useCallback(() => setOpen((o) => !o), []);

  useClickOutside(ref, close, open);

  useEffect(() => {
    if (!open) return;
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape') close();
    };
    document.addEventListener('keydown', onKey);
    return () => document.removeEventListener('keydown', onKey);
  }, [open, close]);

  return (
    <DropdownContext.Provider value={{ close }}>
      <div ref={ref} className="relative inline-block">
        {trigger({ open, toggle })}
        <AnimatePresence>
          {open && (
            <MotionDiv
              role="menu"
              initial={{ opacity: 0, y: -8, scale: 0.97 }}
              animate={{ opacity: 1, y: 0, scale: 1 }}
              exit={{ opacity: 0, y: -8, scale: 0.97 }}
              transition={{ duration: 0.15 }}
              className={cn(
                'glass-panel absolute z-50 mt-3 min-w-[14rem] rounded-3xl p-2',
                align === 'right' ? 'right-0 origin-top-right' : 'left-0 origin-top-left',
                panelClassName
              )}
            >
              {typeof children === 'function' ? children({ close }) : children}
            </MotionDiv>
          )}
        </AnimatePresence>
      </div>
    </DropdownContext.Provider>
  );
};

export interface DropdownItemProps {
  icon?: React.ReactNode;
  tone?: 'default' | 'danger';
  disabled?: boolean;
  onClick?: () => void;
  children: React.ReactNode;
}

export const DropdownItem: React.FC<DropdownItemProps> = ({
  icon,
  tone = 'default',
  disabled,
  onClick,
  children,
}) => {
  const { close } = useContext(DropdownContext);
  return (
    <button
      type="button"
      role="menuitem"
      disabled={disabled}
      onClick={() => {
        onClick?.();
        close();
      }}
      className={cn(
        'flex w-full items-center gap-3 rounded-2xl px-4 py-3 text-left text-xs font-black uppercase tracking-widest transition-colors disabled:opacity-50',
        tone === 'danger'
          ? 'text-red-600 hover:bg-red-50'
          : 'text-slate-700 hover:bg-slate-900/5'
      )}
    >
      {icon}
      {children}
    </button>
  );
};

export const DropdownDivider: React.FC = () => <div className="my-1.5 h-px bg-slate-200/70" role="separator" />;

export default Dropdown;