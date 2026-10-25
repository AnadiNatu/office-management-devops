import { Menu, ChevronDown, User, LogOut } from "lucide-react";
import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import ConfirmDialog from "../components/common/ConfirmDialog";
import { useAuth } from "../hooks/useAuth";
import { getRouteTitle } from "../routes/routeConfig";
import { ROUTES } from "../routes/routeConstants";
import Avatar from "../ui/Avatar";
import Dropdown, { DropdownDivider, DropdownItem } from "../ui/Dropdown";
import { fullName, cn } from "../utils/formatUtils";
import NotificationBell from "../components/notification/NotificationBell";



interface HeaderProps {
  onMenuClick: () => void;
}

export const Header: React.FC<HeaderProps> = ({ onMenuClick }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const { pathname } = useLocation();
  const [confirmOpen, setConfirmOpen] = useState(false);
  const name = fullName(user);

  const handleLogout = () => {
    setConfirmOpen(false);
    logout();
    navigate(ROUTES.LOGIN, { replace: true });
  };

  return (
    <>
      <header className="glass-panel sticky top-0 z-20 flex items-center justify-between gap-4 rounded-none border-x-0 border-t-0 px-4 py-3 shadow-none sm:px-6 lg:px-10">
        <div className="flex min-w-0 items-center gap-3">
          <button
            type="button"
            onClick={onMenuClick}
            aria-label="Open menu"
            className="rounded-2xl bg-slate-950 p-2.5 text-white lg:hidden"
          >
            <Menu size={20} />
          </button>
          <h2 className="truncate text-lg font-black uppercase tracking-tight text-slate-950 sm:text-xl">
            {getRouteTitle(pathname)}
          </h2>
        </div>

        <div className="flex items-center gap-3">
          <NotificationBell />

          <Dropdown
            trigger={({ open, toggle }) => (
              <button
                type="button"
                onClick={toggle}
                aria-haspopup="menu"
                aria-expanded={open}
                className="flex items-center gap-2 rounded-2xl p-1.5 pr-3 transition-colors hover:bg-slate-900/5"
              >
                <Avatar src={user?.profilePicture} name={name} size="sm" />
                <span className="hidden max-w-[10rem] truncate text-xs font-black uppercase tracking-wide text-slate-900 sm:block">
                  {name}
                </span>
                <ChevronDown
                  size={16}
                  className={cn('text-slate-500 transition-transform', open && 'rotate-180')}
                />
              </button>
            )}
          >
            <div className="px-4 py-3">
              <p className="truncate text-sm font-black text-slate-950">{name}</p>
              <p className="truncate text-xs font-bold text-slate-500">{user?.email}</p>
            </div>
            <DropdownDivider />
            <DropdownItem icon={<User size={16} />} onClick={() => navigate(ROUTES.PROFILE)}>
              Profile
            </DropdownItem>
            <DropdownItem tone="danger" icon={<LogOut size={16} />} onClick={() => setConfirmOpen(true)}>
              Sign out
            </DropdownItem>
          </Dropdown>
        </div>
      </header>

      <ConfirmDialog
        open={confirmOpen}
        title="Sign out?"
        message="You will need to sign in again to access your workspace."
        confirmLabel="Sign out"
        tone="danger"
        onConfirm={handleLogout}
        onCancel={() => setConfirmOpen(false)}
      />
    </>
  );
};

export default Header;