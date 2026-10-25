import { Suspense } from 'react';
import { useLocation, Navigate, Outlet, Routes, Route } from 'react-router-dom';
import { PERMISSIONS } from '../auth/permissions';
import ProtectedRoute from '../auth/ProtectedRoute';
import RoleGuard from '../auth/RoleGaurd';
import { useAuth } from '../hooks/useAuth';
import { ROUTES } from './routeConstants';
import AuthLayout from '../layouts/AuthLayout';
import MainLayout from '../layouts/MainLayout';
import NotificationsPage from '../components/notification/NotificationPage';
import AdminPage from '../pages/admin/AdminPage';
import ForgotPasswordPage from '../pages/auth/ForgotPasswordPage';
import LoginPage from '../pages/auth/LoginPage';
import OAuthCallbackPage from '../pages/auth/OAuthCallbackPage';
import ResetPasswordPage from '../pages/auth/ResetPasswordPage';
import SignupPage from '../pages/auth/SignupPage';
import VerifyEmailPage from '../pages/auth/VerifyEmailPage';
import MyCommentsPage from '../pages/comments/MyCommentsPage';
import ForbiddenPage from '../pages/errors/ForbiddenPage';
import NotFoundPage from '../pages/errors/NotFoundPage';
import MessagingPage from '../pages/messaging/MessagingPage';
import ProfilePage from '../pages/profile/ProfilePage';
import TaskDetailPage from '../pages/tasks/TaskDetailPage';
import TasksPage from '../pages/tasks/TasksPage';
import ManagementPage from '../pages/management/ManagementPage';
import OAuthFailurePage from '../pages/auth/OAuthFailurePage';
import DashboardPage from '../pages/dashboard/DashboardPage';





const PageLoader: React.FC = () => (
  <div className="flex min-h-[50vh] items-center justify-center" role="status" aria-label="Loading">
    <div className="h-10 w-10 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent" />
  </div>
);

/** Login/signup/etc. are not reachable while signed in. */
const PublicOnly: React.FC = () => {
  const { status, isAuthenticated } = useAuth();
  const location = useLocation();

  if (status === 'loading') return <PageLoader />;
  if (isAuthenticated) {
    const state = location.state as | {from?: {pathname?: string; }; } | null;
    const from = state?.from?.pathname;
    // const from = (location.state as { from?: { pathname?: string } } | null)?.from?.pathname;
    return <Navigate to={from ?? ROUTES.DASHBOARD} replace />;
  }
  return <Outlet />;
};

export const AppRoutes: React.FC = () => (
  <Suspense fallback={<PageLoader />}>
    <Routes>
      {/* Public */}
      <Route element={<AuthLayout />}>
        <Route element={<PublicOnly />}>
          <Route path={ROUTES.LOGIN} element={<LoginPage />} />
          <Route path={ROUTES.SIGNUP} element={<SignupPage />} />
          <Route path={ROUTES.FORGOT_PASSWORD} element={<ForgotPasswordPage />} />
          <Route path={ROUTES.RESET_PASSWORD} element={<ResetPasswordPage />} />
        </Route>
        <Route path={ROUTES.VERIFY_EMAIL} element={<VerifyEmailPage />} />
        <Route path={ROUTES.OAUTH_CALLBACK} element={<OAuthCallbackPage />} />
        <Route path={ROUTES.OAUTH_FAILURE} element={<OAuthFailurePage />} />
// Need to add the OAuth2 Login endpoints

      </Route>

      {/* Authenticated */}
      <Route element={<ProtectedRoute />}>
        <Route element={<MainLayout />}>
          <Route index element={<DashboardPage />} />
          <Route path={ROUTES.TASKS} element={<TasksPage />} />
          <Route path={ROUTES.TASK_DETAIL()} element={<TaskDetailPage />} />
          <Route path={ROUTES.COMMENTS} element={<MyCommentsPage />} />
          <Route path={ROUTES.MESSAGES} element={<MessagingPage />} />
          <Route path={ROUTES.CONVERSATION()} element={<MessagingPage />} />
          <Route path={ROUTES.NOTIFICATIONS} element={<NotificationsPage />} />
          <Route path={ROUTES.PROFILE} element={<ProfilePage />} />

          <Route element={<RoleGuard roles={[...PERMISSIONS.ACCESS_MANAGEMENT]} />}>
            <Route path={ROUTES.MANAGEMENT} element={<ManagementPage />} />
          </Route>

          <Route element={<RoleGuard roles={[...PERMISSIONS.ACCESS_ADMIN]} />}>
            <Route path={ROUTES.ADMIN} element={<AdminPage />} />
          </Route>

          <Route path={ROUTES.FORBIDDEN} element={<ForbiddenPage />} />
        </Route>
      </Route>

      <Route path={ROUTES.NOT_FOUND} element={<NotFoundPage />} />
    </Routes>
  </Suspense>
);

export default AppRoutes;