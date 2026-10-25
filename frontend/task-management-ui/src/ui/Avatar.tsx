import { useState, useEffect } from "react";
import { cn, initialsOf } from "../utils/formatUtils";

const SIZES = {
  xs: 'w-7 h-7 text-[10px] rounded-lg',
  sm: 'w-9 h-9 text-xs rounded-xl',
  md: 'w-11 h-11 text-sm rounded-2xl',
  lg: 'w-16 h-16 text-xl rounded-[1.5rem]',
  xl: 'w-28 h-28 text-3xl rounded-[2rem]',
} as const;

const PALETTE = [
  'bg-indigo-600',
  'bg-violet-600',
  'bg-emerald-600',
  'bg-sky-600',
  'bg-rose-600',
  'bg-amber-600',
  'bg-slate-900',
];

const colorFor = (name: string) => {
  let h = 0;
  for (let i = 0; i < name.length; i++) h = (h * 31 + name.charCodeAt(i)) >>> 0;
  return PALETTE[h % PALETTE.length];
};

export interface AvatarProps {
  src?: string | null;
  name?: string | null;
  size?: keyof typeof SIZES;
  className?: string;
}

export const Avatar: React.FC<AvatarProps> = ({ src, name, size = 'md', className }) => {
  const [failed, setFailed] = useState(false);
  useEffect(() => setFailed(false), [src]);

  const label = name ?? 'User';

  if (src && !failed) {
    return (
      <img
        src={src}
        alt={label}
        onError={() => setFailed(true)}
        className={cn('object-cover shadow-lg shrink-0', SIZES[size], className)}
      />
    );
  }

  return (
    <div
      role="img"
      aria-label={label}
      className={cn(
        'flex items-center justify-center font-black text-white shadow-lg shrink-0 select-none',
        colorFor(label),
        SIZES[size],
        className
      )}
    >
      {initialsOf(name)}
    </div>
  );
};

export default Avatar;