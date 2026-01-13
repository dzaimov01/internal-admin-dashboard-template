'use client';

import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { apiClient } from '@/lib/api';
import { useUsers } from '@/lib/queries';
import { AdminGate } from '@/components/AdminGate';
import { z } from 'zod';

const ROLE_OPTIONS = ['Admin', 'Manager', 'Viewer'];

export default function UsersPage() {
  const { data: users } = useUsers();
  const queryClient = useQueryClient();
  const [email, setEmail] = useState('');
  const [fullName, setFullName] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('Viewer');

  const createMutation = useMutation({
    mutationFn: () =>
      apiClient.post('/api/users', {
        email,
        fullName,
        password,
        roles: [role]
      }),
    onSuccess: () => {
      setEmail('');
      setFullName('');
      setPassword('');
      queryClient.invalidateQueries({ queryKey: ['users'] });
    }
  });

  const handleCreate = () => {
    const schema = z.object({
      email: z.string().email(),
      fullName: z.string().min(2),
      password: z.string().min(8)
    });
    const result = schema.safeParse({ email, fullName, password });
    if (!result.success) {
      return;
    }
    createMutation.mutate();
  };

  return (
    <AdminGate>
      <div className="grid gap-6 lg:grid-cols-[2fr_1fr]">
      <Card>
        <p className="text-sm font-semibold text-white">User management</p>
        <p className="text-xs text-slate-400">Invite users and assign roles.</p>
        <div className="mt-6 overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="text-xs uppercase text-slate-500">
              <tr>
                <th className="py-2">Name</th>
                <th className="py-2">Email</th>
                <th className="py-2">Roles</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800">
              {users?.map((user) => (
                <tr key={user.id} className="text-slate-200">
                  <td className="py-3 font-medium text-white">{user.fullName}</td>
                  <td className="py-3 text-slate-300">{user.email}</td>
                  <td className="py-3 text-slate-400">{user.roles.join(', ')}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>

      <Card>
        <p className="text-sm font-semibold text-white">Create user</p>
        <div className="mt-4 space-y-3">
          <Input placeholder="Full name" value={fullName} onChange={(e) => setFullName(e.target.value)} />
          <Input placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
          <Input placeholder="Temporary password" value={password} onChange={(e) => setPassword(e.target.value)} type="password" />
          <select
            className="h-10 w-full rounded-md border border-slate-700 bg-slate-900/60 px-3 text-sm text-slate-100"
            value={role}
            onChange={(e) => setRole(e.target.value)}
          >
            {ROLE_OPTIONS.map((option) => (
              <option key={option} value={option}>
                {option}
              </option>
            ))}
          </select>
          <Button
            onClick={handleCreate}
            disabled={!email || !fullName || !password || createMutation.isPending}
          >
            {createMutation.isPending ? 'Creating...' : 'Create'}
          </Button>
        </div>
      </Card>
      </div>
    </AdminGate>
  );
}
