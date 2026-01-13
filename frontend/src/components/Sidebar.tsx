'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useAuth } from '@/lib/auth';
import { cn } from '@/lib/utils';

const navItems = [
  { href: '/dashboard', label: 'Overview' },
  { href: '/customers', label: 'Customers' },
  { href: '/orders', label: 'Orders' },
  { href: '/tickets', label: 'Tickets' },
  { href: '/metrics', label: 'Metrics' },
  { href: '/audit', label: 'Audit Log' },
  { href: '/users', label: 'Users', adminOnly: true },
  { href: '/settings', label: 'Settings', adminOnly: true },
  { href: '/system', label: 'System Health', adminOnly: true }
];

export function Sidebar() {
  const pathname = usePathname();
  const { hasRole } = useAuth();

  return (
    <aside className="flex h-full flex-col border-r border-slate-800 bg-slate-950/80 px-4 py-6">
      <div className="mb-8">
        <p className="text-xs uppercase tracking-[0.3em] text-slate-400">Internal Admin</p>
        <h1 className="text-lg font-semibold">Ops Console</h1>
      </div>
      <nav className="flex flex-1 flex-col gap-2">
        {navItems
          .filter((item) => !item.adminOnly || hasRole('Admin'))
          .map((item) => {
          const active = pathname === item.href || pathname.startsWith(`${item.href}/`);
          return (
            <Link
              key={item.href}
              href={item.href}
              className={cn(
                'rounded-lg px-3 py-2 text-sm font-medium transition-colors',
                active
                  ? 'bg-slate-800 text-white'
                  : 'text-slate-300 hover:bg-slate-900 hover:text-white'
              )}
            >
              {item.label}
            </Link>
          );
        })}
      </nav>
      <div className="rounded-lg bg-slate-900/80 p-4 text-xs text-slate-400">
        <p className="mb-2 text-slate-200">Need enterprise features?</p>
        <p>SSO, multi-tenancy, and compliance packs are available as extensions.</p>
      </div>
    </aside>
  );
}
