import { motion } from "framer-motion";
import { useLocation, Outlet } from "react-router-dom";

const MotionDiv = motion.div as any;

/** Content area: keyed by top-level section so /messages/:id doesn't remount on switch. */
export const DashboardLayout: React.FC = () => {
  const { pathname } = useLocation();
  const section = pathname.split('/')[1] || 'home';

  return (
    <main id="main-content" tabIndex={-1} className="flex-1 outline-none">
      <MotionDiv
        key={section}
        initial={{ opacity: 0, y: 12 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.25 }}
        className="mx-auto w-full max-w-7xl px-4 py-8 sm:px-6 lg:px-10"
      >
        <Outlet />
      </MotionDiv>
    </main>
  );
};

export default DashboardLayout;