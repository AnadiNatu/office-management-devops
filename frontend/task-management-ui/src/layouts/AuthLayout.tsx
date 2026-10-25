import { motion } from "framer-motion";
import { Building2 } from "lucide-react";
import { Outlet } from "react-router-dom";
import { APP_CONFIG } from "../config/appConfig";

const MotionDiv = motion.div as any;

export const AuthLayout: React.FC = () => (
  <div className="relative flex min-h-screen items-center justify-center overflow-hidden bg-slate-950 p-6">
    <div
      aria-hidden
      className="pointer-events-none absolute -left-40 -top-40 h-[28rem] w-[28rem] rounded-full bg-indigo-600/30 blur-3xl"
    />
    <div
      aria-hidden
      className="pointer-events-none absolute -bottom-40 -right-32 h-[30rem] w-[30rem] rounded-full bg-purple-600/25 blur-3xl"
    />
    <div
      aria-hidden
      className="pointer-events-none absolute left-1/2 top-1/3 h-72 w-72 -translate-x-1/2 rounded-full bg-emerald-500/10 blur-3xl"
    />

    <MotionDiv
      initial={{ opacity: 0, y: 24, scale: 0.97 }}
      animate={{ opacity: 1, y: 0, scale: 1 }}
      transition={{ duration: 0.4 }}
      className="relative w-full max-w-lg"
    >
      <div className="glass-panel overflow-hidden rounded-[3rem] shadow-2xl">
        <div className="flex flex-col items-center bg-slate-950/90 p-8 text-white">
          <div className="mb-3 rounded-2xl bg-indigo-600 p-3 shadow-lg shadow-indigo-600/40">
            <Building2 size={28} />
          </div>
          <h1 className="text-3xl font-black uppercase tracking-tighter">{APP_CONFIG.name}</h1>
          <p className="mt-2 text-[10px] font-black uppercase tracking-[0.3em] text-slate-400">
            Office Management System
          </p>
        </div>
        <div className="p-8 sm:p-10">
          <Outlet />
        </div>
      </div>
    </MotionDiv>
  </div>
);

export default AuthLayout;