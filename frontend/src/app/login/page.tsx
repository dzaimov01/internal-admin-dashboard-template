'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { useAuth } from '@/lib/auth';
import { z } from 'zod';

export default function LoginPage() {
  const { login } = useAuth();
  const router = useRouter();
  const [email, setEmail] = useState('admin@acme.test');
  const [password, setPassword] = useState('admin123!');
  const [error, setError] = useState<string | null>(null);

  const onSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setError(null);
    const schema = z.object({
      email: z.string().email(),
      password: z.string().min(6)
    });
    const result = schema.safeParse({ email, password });
    if (!result.success) {
      setError('Enter a valid email and password.');
      return;
    }
    try {
      await login(email, password);
      router.push('/dashboard');
    } catch (err) {
      setError('Unable to sign in. Check your credentials.');
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center px-6">
      <div className="w-full max-w-md rounded-2xl border border-slate-800 bg-slate-950/70 p-8 shadow-soft">
        <p className="text-xs uppercase tracking-[0.3em] text-slate-400">Internal Admin</p>
        <h1 className="mt-3 text-2xl font-semibold text-white">Welcome back</h1>
        <p className="mt-2 text-sm text-slate-400">
          Sign in to manage customers, orders, tickets, and metrics.
        </p>
        <form className="mt-6 space-y-4" onSubmit={onSubmit}>
          <div>
            <label className="text-xs uppercase tracking-wide text-slate-400">Email</label>
            <Input value={email} onChange={(e) => setEmail(e.target.value)} type="email" required />
          </div>
          <div>
            <label className="text-xs uppercase tracking-wide text-slate-400">Password</label>
            <Input value={password} onChange={(e) => setPassword(e.target.value)} type="password" required />
          </div>
          {error && <p className="text-sm text-red-400">{error}</p>}
          <Button type="submit" className="w-full">Sign in</Button>
        </form>
        <p className="mt-6 text-xs text-slate-500">
          Demo credentials are prefilled. Replace with your directory or SSO integration in production.
        </p>
      </div>
    </div>
  );
}
