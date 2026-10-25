import React from 'react';
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { ROUTES } from '../routes/routeConstants';

// const FullScreenLoader = () => (
//   <div className="min-h-screen flex items-center justify-center bg-slate-950">
//     <div className="w-12 h-12 rounded-full border-4 border-indigo-600 border-t-transparent animate-spin" />
//   </div>
// );

// export const ProtectedRoute: React.FC<{ children?: React.ReactNode }> = ({ children }) => {
//   const { status, isAuthenticated } = useAuth();
//   const location = useLocation();

//   if (status === 'loading') return <FullScreenLoader />;
//   if (!isAuthenticated) {
//     return <Navigate to={ROUTES.LOGIN} replace state={{ from: location }} />;
//   }
//   return <>{children ?? <Outlet />}</>;
// };

// export default ProtectedRoute;

const FullScreenLoader: React.FC = () => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-950">
      <div
        className="w-12 h-12 rounded-full border-4 border-indigo-600 border-t-transparent animate-spin"
        aria-label="Loading"
      />
    </div>
  );
};

interface ProtectedRouteProps {
  children?: React.ReactNode;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({children,}) => {
  const {status,isAuthenticated,} = useAuth();
  const location = useLocation();

  /*
   * VERY IMPORTANT:
   *
   * Do not redirect while AuthProvider is still checking
   * localStorage/session/token state.
   */
  if (status === 'loading') {
    return <FullScreenLoader />;
  }

  /*
   * User is not logged in.
   *
   * Save the page they originally wanted so that the login
   * page can redirect them back after successful login.
   */
  if (!isAuthenticated) {
    return (
      <Navigate to={ROUTES.LOGIN} replace state={{from: location,}}/>
    );
  }

  /*
   * Authenticated user.
   */
  return children ?? <Outlet />;
};

export default ProtectedRoute;
export { ProtectedRoute };