import React from 'react';

interface PageHeaderProps {
  title: string;
  subtitle?: string;
  actions?: React.ReactNode;
}

export const PageHeader: React.FC<PageHeaderProps> = ({ title, subtitle, actions }) => (
  <div className="flex flex-col gap-6 md:flex-row md:items-end md:justify-between">
    <div>
      <h1 className="text-3xl md:text-4xl font-black uppercase leading-none tracking-tighter text-slate-950">
        {title}
      </h1>
      {subtitle && <p className="mt-2 text-base font-bold text-slate-700 md:text-lg">{subtitle}</p>}
    </div>
    {actions && <div className="flex flex-wrap items-center gap-3">{actions}</div>}
  </div>
);

export default PageHeader;