import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { useDocumentTitle } from '../hooks/useCommon';
import { getRouteTitle } from '../routes/routeConfig';
import { DashboardLayout } from './DashboardLayout';
import { Footer } from './Footer';
import { Header } from './Header';
import { Sidebar } from './Sidebar';

export const MainLayout: React.FC = () => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const { pathname } = useLocation();

  useEffect(() => setSidebarOpen(false), [pathname]);
  useDocumentTitle(getRouteTitle(pathname));

  return (
    <div className="app-bg min-h-screen">
      <a
        href="#main-content"
        className="sr-only z-[100] rounded-xl bg-indigo-600 px-4 py-2 text-xs font-black uppercase text-white focus:not-sr-only focus:fixed focus:left-4 focus:top-4"
 >     
        Skip to content
      </a>

      <Sidebar open={sidebarOpen} onClose={() => setSidebarOpen(false)} />

      <div className="flex min-h-screen flex-col lg:pl-72">
        <Header onMenuClick={() => setSidebarOpen(true)} />
        <DashboardLayout />
        <Footer />
      </div>
    </div>
  );
};

export default MainLayout;