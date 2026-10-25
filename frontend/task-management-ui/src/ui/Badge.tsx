import React from 'react';
import { cn } from "../utils/formatUtils";

export type BadgeTone =
  | "neutral"
  | "indigo"
  | "green"
  | "amber"
  | "red"
  | "blue"
  | "purple"
  | "slate";

type BadgeToneStyles = {
  wrap: string;
  dot: string;
};

const TONES: Record<BadgeTone, BadgeToneStyles> = {
  neutral: {
    wrap: "bg-slate-100 text-slate-700 border-slate-200",
    dot: "bg-slate-400",
  },

  indigo: {
    wrap: "bg-indigo-50 text-indigo-700 border-indigo-200",
    dot: "bg-indigo-500",
  },

  green: {
    wrap: "bg-green-50 text-green-700 border-green-200",
    dot: "bg-green-500",
  },

  amber: {
    wrap: "bg-orange-50 text-orange-700 border-orange-200",
    dot: "bg-orange-500",
  },

  red: {
    wrap: "bg-red-50 text-red-700 border-red-200",
    dot: "bg-red-500",
  },

  blue: {
    wrap: "bg-sky-50 text-sky-700 border-sky-200",
    dot: "bg-sky-500",
  },

  purple: {
    wrap: "bg-purple-50 text-purple-700 border-purple-200",
    dot: "bg-purple-500",
  },

  slate: {
    wrap: "bg-slate-950 text-white border-slate-950",
    dot: "bg-white",
  },
};

export interface BadgeProps
  extends React.HTMLAttributes<HTMLSpanElement> {
  tone?: BadgeTone | string | null;
  dot?: boolean;
  size?: "sm" | "md";
}

function resolveTone(
  tone: BadgeProps["tone"]
): BadgeTone {
  if (
    typeof tone === "string" &&
    tone in TONES
  ) {
    return tone as BadgeTone;
  }

  return "neutral";
}

export const Badge: React.FC<BadgeProps> = ({
  tone = "neutral",
  dot = false,
  size = "md",
  className,
  children,
  ...rest
}) => {
  const resolvedTone = resolveTone(tone);
  const styles = TONES[resolvedTone];

  return (
    <span
      className={cn(
        "inline-flex items-center gap-1.5 rounded-xl border font-black uppercase tracking-widest whitespace-nowrap",
        size === "sm"
          ? "px-2 py-0.5 text-[8px]"
          : "px-3 py-1.5 text-[10px]",
        styles.wrap,
        className
      )}
      {...rest}
    >
      {dot && (
        <span
          className={cn(
            "h-1.5 w-1.5 rounded-full",
            styles.dot
          )}
          aria-hidden
        />
      )}

      {children}
    </span>
  );
};

export default Badge;