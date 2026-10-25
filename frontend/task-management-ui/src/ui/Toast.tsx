import { AnimatePresence, motion } from "framer-motion";
import { CheckCircle2, AlertCircle, Info, AlertTriangle, X } from "lucide-react";
import { createContext, useState, useRef, useCallback, useEffect, useMemo, useContext } from "react";
import { APP_CONFIG } from "../config/appConfig";
import { cn } from "../utils/formatUtils";

const MotionDiv = motion.div as any;

type ToastKind = 'success' | 'error' | 'info' | 'warning';

interface ToastItem {
  id: number;
  kind: ToastKind;
  title?: string;
  message: string;
}

interface ToastApi {
  success: (message: string, title?: string) => void;
  error: (message: string, title?: string) => void;
  info: (message: string, title?: string) => void;
  warning: (message: string, title?: string) => void;
  dismiss: (id: number) => void;
}

const ToastContext = createContext<ToastApi | undefined>(undefined);

const STYLES: Record<ToastKind, { bar: string; icon: React.ReactNode; iconWrap: string }> = {
  success: { bar: 'border-emerald-500', iconWrap: 'bg-emerald-600', icon: <CheckCircle2 size={18} /> },
  error: { bar: 'border-red-600', iconWrap: 'bg-red-600', icon: <AlertCircle size={18} /> },
  info: { bar: 'border-indigo-600', iconWrap: 'bg-indigo-600', icon: <Info size={18} /> },
  warning: { bar: 'border-amber-500', iconWrap: 'bg-amber-500', icon: <AlertTriangle size={18} /> },
};

const DEFAULT_TITLES: Record<ToastKind, string> = {
  success: 'Success',
  error: 'Error',
  info: 'Notice',
  warning: 'Warning',
};

export const ToastProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [toasts, setToasts] = useState<ToastItem[]>([]);
  const idRef = useRef(0);
  const timers = useRef(new Map<number, number>());

  const dismiss = useCallback((id: number) => {
    setToasts((t) => t.filter((x) => x.id !== id));
    const handle = timers.current.get(id);
    if (handle) {
      window.clearTimeout(handle);
      timers.current.delete(id);
    }
  }, []);

  const push = useCallback(
    (kind: ToastKind, message: string, title?: string) => {
      const id = ++idRef.current;
      setToasts((t) => [...t.slice(-4), { id, kind, message, title }]);
      timers.current.set(
        id,
        window.setTimeout(() => dismiss(id), APP_CONFIG.toastDurationMs)
      );
    },
    [dismiss]
  );

  useEffect(() => {
    const map = timers.current;
    return () => {
      map.forEach((h) => window.clearTimeout(h));
      map.clear();
    };
  }, []);

  const api = useMemo<ToastApi>(
    () => ({
      success: (m, t) => push('success', m, t),
      error: (m, t) => push('error', m, t),
      info: (m, t) => push('info', m, t),
      warning: (m, t) => push('warning', m, t),
      dismiss,
    }),
    [push, dismiss]
  );

  return (
    <ToastContext.Provider value={api}>
      {children}
      <div
        aria-live="polite"
        className="pointer-events-none fixed right-4 top-20 z-[100] w-[calc(100%-2rem)] max-w-sm space-y-3"
      >
        <AnimatePresence initial={false}>
          {toasts.map((t) => (
            <MotionDiv
              key={t.id}
              layout
              role={t.kind === 'error' ? 'alert' : 'status'}
              initial={{ opacity: 0, x: 60, scale: 0.95 }}
              animate={{ opacity: 1, x: 0, scale: 1 }}
              exit={{ opacity: 0, x: 60, scale: 0.95 }}
              transition={{ type: 'spring', damping: 24, stiffness: 300 }}
              className={cn(
                'glass-panel pointer-events-auto flex gap-3 rounded-[1.5rem] border-l-8 p-4',
                STYLES[t.kind].bar
              )}
            >
              <div className={cn('h-fit rounded-xl p-2 text-white', STYLES[t.kind].iconWrap)}>
                {STYLES[t.kind].icon}
              </div>
              <div className="min-w-0 flex-1">
                <h4 className="text-sm font-black leading-tight text-slate-950">
                  {t.title ?? DEFAULT_TITLES[t.kind]}
                </h4>
                <p className="mt-1 break-words text-xs font-bold text-slate-700">{t.message}</p>
              </div>
              <button
                type="button"
                onClick={() => dismiss(t.id)}
                aria-label="Dismiss notification"
                className="h-fit rounded-full p-1 text-slate-400 hover:bg-slate-900/10"
              >
                <X size={16} />
              </button>
            </MotionDiv>
          ))}
        </AnimatePresence>
      </div>
    </ToastContext.Provider>
  );
};

export const useToast = (): ToastApi => {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error('useToast must be used within ToastProvider');
  return ctx;
};