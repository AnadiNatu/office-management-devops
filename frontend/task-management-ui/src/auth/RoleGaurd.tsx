import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { ROUTES } from '../routes/routeConstants';
import type { AppRole } from '../types/auth';

// interface RoleGuardProps {
//   roles: AppRole[];
//   children?: React.ReactNode;
//   /** 'hide' renders nothing (for inline UI); 'redirect' goes to 403 (for routes). */
//   mode?: 'hide' | 'redirect';
//   fallback?: React.ReactNode;
// }

// export const RoleGuard: React.FC<RoleGuardProps> = ({
//   roles, children, mode = 'redirect', fallback = null,
// }) => {
//   const { user } = useAuth();
//   const allowed = !!user && roles.includes(user.role);

//   if (!allowed) {
//     return mode === 'redirect' ? <Navigate to={ROUTES.FORBIDDEN} replace /> : <>{fallback}</>;
//   }
//   return <>{children ?? <Outlet />}</>;
// };

// export default RoleGuard;
interface RoleGuardProps {
  roles: AppRole[];
  children?: React.ReactNode;

  /*
   * redirect = route-level protection
   * hide = inline UI protection
   */
  mode?: 'hide' | 'redirect';

  fallback?: React.ReactNode;
}

const RoleGuard: React.FC<RoleGuardProps> = ({
  roles,
  children,
  mode = 'redirect',
  fallback = null,
}) => {
  const { user } = useAuth();

  const allowed =
    !!user &&
    roles.includes(user.role);

  if (!allowed) {
    if (mode === 'hide') {
      return <>{fallback}</>;
    }

    return (
      <Navigate
        to={ROUTES.FORBIDDEN}
        replace
      />
    );
  }

  return children ?? <Outlet />;
};

export default RoleGuard;
export { RoleGuard };