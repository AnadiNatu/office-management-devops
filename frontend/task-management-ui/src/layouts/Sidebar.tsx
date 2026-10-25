import { motion, AnimatePresence } from "framer-motion";
import { Building2, X } from "lucide-react";
import { NavLink } from "react-router-dom";
import { APP_CONFIG } from "../config/appConfig";
import { useAuth } from "../hooks/useAuth";
import { getVisibleNavItems } from "../routes/routeConfig";
import Avatar from "../ui/Avatar";
import { cn, fullName } from "../utils/formatUtils";
import { ROLE_LABELS } from "../utils/roleUtils";
import Badge from "../ui/Badge";

const MotionDiv = motion.div as any;

interface SidebarProps {
  open: boolean;
  onClose: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ open, onClose }) => {
  const { user } = useAuth();
  const items = getVisibleNavItems(user?.role);

  const content = (
    <div className="flex h-full flex-col text-white">
      <div className="flex items-center gap-3 p-6">
        <div className="rounded-2xl bg-indigo-600 p-2.5 shadow-lg shadow-indigo-600/40">
          <Building2 size={22} />
        </div>
        <div>
          <p className="text-lg font-black uppercase leading-none tracking-tighter">{APP_CONFIG.name}</p>
          <p className="mt-1 text-[9px] font-black uppercase tracking-[0.3em] text-slate-400">
            Office Management
          </p>
        </div>
      </div>

      <nav aria-label="Primary" className="custom-scrollbar flex-1 space-y-1.5 overflow-y-auto px-4 py-2">
        {items.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.path}
              to={item.path}
              end={item.end}
              onClick={onClose}
              className={({ isActive }) =>
                cn(
                  'flex items-center gap-3 rounded-2xl px-4 py-3.5 text-xs font-black uppercase tracking-widest transition-all',
                  isActive
                    ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-600/30'
                    : 'text-slate-400 hover:bg-white/10 hover:text-white'
                )
              }
            >
              <Icon size={18} />
              {item.label}
            </NavLink>
          );
        })}
      </nav>

      {user && (
        <div className="m-4 flex items-center gap-3 rounded-3xl border border-white/10 bg-white/5 p-3">
          <Avatar src={user.profilePicture} name={fullName(user)} size="sm" />
          <div className="min-w-0 flex-1">
            <p className="truncate text-xs font-black">{fullName(user)}</p>
            <Badge tone="indigo" size="sm" className="mt-1">
              {ROLE_LABELS[user.role]}
            </Badge>
          </div>
        </div>
      )}
    </div>
  );

  return (
    <>
      <aside className="glass-dark fixed inset-y-0 left-0 z-30 hidden w-72 lg:block">{content}</aside>

      <AnimatePresence>
        {open && (
          <div className="fixed inset-0 z-40 lg:hidden">
            <MotionDiv
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={onClose}
              className="absolute inset-0 bg-slate-950/60 backdrop-blur-sm"
            />
            <MotionDiv
              initial={{ x: -320 }}
              animate={{ x: 0 }}
              exit={{ x: -320 }}
              transition={{ type: 'spring', damping: 30, stiffness: 320 }}
              className="glass-dark absolute inset-y-0 left-0 w-72"
            >
              <button
                type="button"
                onClick={onClose}
                aria-label="Close menu"
                className="absolute right-3 top-3 rounded-full bg-white/10 p-2 text-white hover:bg-white/20"
              >
                <X size={18} />
              </button>
              {content}
            </MotionDiv>
          </div>
        )}
      </AnimatePresence>
    </>
  );
};

export default Sidebar;