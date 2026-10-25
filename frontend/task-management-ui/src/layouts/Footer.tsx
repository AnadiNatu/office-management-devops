import { APP_CONFIG } from "../config/appConfig";

export const Footer: React.FC = () => (
  <footer className="px-4 py-6 text-center sm:px-6 lg:px-10">
    <p className="text-[10px] font-black uppercase tracking-[0.3em] text-slate-400">
      © {new Date().getFullYear()} {APP_CONFIG.name} · Office Management System
    </p>
  </footer>
);

export default Footer;