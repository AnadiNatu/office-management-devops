import { X } from "lucide-react";
import { useRef, useId, useEffect } from "react";
import { createPortal } from "react-dom";
import { cn } from "../utils/formatUtils";
import { AnimatePresence, motion } from "framer-motion";

const MotionDiv = motion.div as any;

const FOCUSABLE =
  'a[href],button:not([disabled]),textarea:not([disabled]),input:not([disabled]),select:not([disabled]),[tabindex]:not([tabindex="-1"])';

const SIZES = {
  sm: 'max-w-md',
  md: 'max-w-lg',
  lg: 'max-w-2xl',
  xl: 'max-w-4xl',
} as const;

export interface ModalProps {
  open: boolean;
  onClose: () => void;
  title?: React.ReactNode;
  description?: React.ReactNode;
  size?: keyof typeof SIZES;
  footer?: React.ReactNode;
  children?: React.ReactNode;
  closeOnBackdrop?: boolean;
  hideCloseButton?: boolean;
}

export const Modal: React.FC<ModalProps> = ({
  open,
  onClose,
  title,
  description,
  size = 'md',
  footer,
  children,
  closeOnBackdrop = true,
  hideCloseButton = false,
}) => {
  const panelRef = useRef<HTMLDivElement>(null);
  const bodyRef = useRef<HTMLDivElement>(null);
  const onCloseRef = useRef(onClose);
  onCloseRef.current = onClose;
  const titleId = useId();

  useEffect(() => {
    if (!open) return;
    const previouslyFocused = document.activeElement as HTMLElement | null;
    const prevOverflow = document.body.style.overflow;
    document.body.style.overflow = 'hidden';

    const raf = requestAnimationFrame(() => {
      const first = bodyRef.current?.querySelector<HTMLElement>(FOCUSABLE);
      (first ?? panelRef.current)?.focus();
    });

    const onKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        onCloseRef.current();
        return;
      }
      if (e.key !== 'Tab' || !panelRef.current) return;
      const nodes = Array.from(panelRef.current.querySelectorAll<HTMLElement>(FOCUSABLE));
      if (!nodes.length) {
        e.preventDefault();
        return;
      }
      const first = nodes[0];
      const last = nodes[nodes.length - 1];
      const active = document.activeElement;
      if (e.shiftKey && (active === first || active === panelRef.current)) {
        e.preventDefault();
        last.focus();
      } else if (!e.shiftKey && active === last) {
        e.preventDefault();
        first.focus();
      }
    };

    document.addEventListener('keydown', onKeyDown);
    return () => {
      cancelAnimationFrame(raf);
      document.removeEventListener('keydown', onKeyDown);
      document.body.style.overflow = prevOverflow;
      previouslyFocused?.focus?.();
    };
  }, [open]);

  if (typeof document === 'undefined') return null;

  return createPortal(
    <AnimatePresence>
      {open && (
        <MotionDiv
          key="modal-backdrop"
          className="fixed inset-0 z-[80] flex items-center justify-center p-4 sm:p-6 bg-slate-950/60 backdrop-blur-md"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          onMouseDown={(e: React.MouseEvent) => {
            if (closeOnBackdrop && e.target === e.currentTarget) onClose();
          }}
        >
          <MotionDiv
            ref={panelRef}
            role="dialog"
            aria-modal="true"
            aria-labelledby={title ? titleId : undefined}
            tabIndex={-1}
            initial={{ opacity: 0, scale: 0.95, y: 20 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.95, y: 10 }}
            transition={{ type: 'spring', damping: 26, stiffness: 320 }}
            className={cn(
              'glass-panel w-full flex max-h-[90vh] flex-col rounded-[2.5rem] outline-none overflow-hidden',
              SIZES[size]
            )}
          >
            {(title || !hideCloseButton) && (
              <div className="flex items-start justify-between gap-4 p-6 sm:p-8 pb-4">
                <div className="min-w-0">
                  {title && (
                    <h2
                      id={titleId}
                      className="text-xl sm:text-2xl font-black uppercase tracking-tight text-slate-950"
                    >
                      {title}
                    </h2>
                  )}
                  {description && (
                    <p className="mt-1.5 text-sm font-bold text-slate-600">{description}</p>
                  )}
                </div>
                {!hideCloseButton && (
                  <button
                    type="button"
                    onClick={onClose}
                    aria-label="Close dialog"
                    className="p-2.5 rounded-full bg-slate-900/5 hover:bg-slate-900/10 text-slate-600 transition-colors"
                  >
                    <X size={18} />
                  </button>
                )}
              </div>
            )}

            <div
              ref={bodyRef}
              className="flex-1 overflow-y-auto px-6 sm:px-8 pb-6 custom-scrollbar"
            >
              {children}
            </div>

            {footer && (
              <div className="flex flex-wrap items-center justify-end gap-3 px-6 sm:px-8 py-5 border-t border-slate-200/70 bg-white/40">
                {footer}
              </div>
            )}
          </MotionDiv>
        </MotionDiv>
      )}
    </AnimatePresence>,
    document.body
  );
};

export default Modal;