import { type LucideIcon, LayoutDashboard, CheckSquare, MessagesSquare, MessageSquare, Bell, User, Users, ShieldCheck } from "lucide-react";
import { PERMISSIONS } from "../auth/permissions";
import { APP_CONFIG } from "../config/appConfig";
import type { AppRole } from "../types/auth";
import  { ROUTES } from "./routeConstants";


export interface NavItem {
  label: string;
  path: string;
  icon: LucideIcon;
  /** Omit = visible to every authenticated user */
  roles?: AppRole[];
  end?: boolean;
}

export const NAV_ITEMS: NavItem[] = [
  { label: 'Dashboard', path: ROUTES.DASHBOARD, icon: LayoutDashboard, end: true },
  { label: 'Tasks', path: ROUTES.TASKS, icon: CheckSquare },
  { label: 'My Comments', path: ROUTES.COMMENTS, icon: MessagesSquare },
  { label: 'Messages', path: ROUTES.MESSAGES, icon: MessageSquare },
  { label: 'Notifications', path: ROUTES.NOTIFICATIONS, icon: Bell },
  { label: 'Profile', path: ROUTES.PROFILE, icon: User },
  {
    label: 'Management',
    path: ROUTES.MANAGEMENT,
    icon: Users,
    roles: [...PERMISSIONS.ACCESS_MANAGEMENT],
  },
  {
    label: 'Administration',
    path: ROUTES.ADMIN,
    icon: ShieldCheck,
    roles: [...PERMISSIONS.ACCESS_ADMIN],
  },
];

export const getVisibleNavItems = (role?: AppRole): NavItem[] =>
  NAV_ITEMS.filter((i) => !i.roles || (!!role && i.roles.includes(role)));

const TITLES: Array<[string, string]> = [
  [ROUTES.NOTIFICATIONS, 'Notifications'],
  [ROUTES.MESSAGES, 'Messages'],
  [ROUTES.TASKS, 'Tasks'],
  [ROUTES.COMMENTS, 'My Comments'],
  [ROUTES.PROFILE, 'Profile'],
  [ROUTES.MANAGEMENT, 'Management'],
  [ROUTES.ADMIN, 'Administration'],
  [ROUTES.FORBIDDEN, 'Access denied'],
];

export const getRouteTitle = (pathname: string): string => {
  if (pathname === ROUTES.DASHBOARD) return 'Dashboard';
  const match = TITLES.find(([p]) => pathname === p || pathname.startsWith(`${p}/`));
  return match?.[1] ?? APP_CONFIG.name;
};