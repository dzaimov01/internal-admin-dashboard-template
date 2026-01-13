'use client';

import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Card } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { apiClient } from '@/lib/api';
import { useFeatureFlags, useOrganizationSettings } from '@/lib/queries';
import { useState, useEffect } from 'react';
import { AdminGate } from '@/components/AdminGate';

export default function SettingsPage() {
  const { data: org } = useOrganizationSettings();
  const { data: flags } = useFeatureFlags();
  const [name, setName] = useState('');
  const [timezone, setTimezone] = useState('');
  const [supportEmail, setSupportEmail] = useState('');
  const queryClient = useQueryClient();

  useEffect(() => {
    if (org) {
      setName(org.organizationName);
      setTimezone(org.timezone);
      setSupportEmail(org.supportEmail);
    }
  }, [org]);

  const updateOrg = useMutation({
    mutationFn: () =>
      apiClient.patch('/api/settings/organization', {
        organizationName: name,
        timezone,
        supportEmail
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['settings'] });
    }
  });

  const toggleFlag = useMutation({
    mutationFn: (payload: { key: string; enabled: boolean }) =>
      apiClient.patch('/api/settings/flags', payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['flags'] });
    }
  });

  return (
    <AdminGate>
      <div className="grid gap-6 lg:grid-cols-2">
      <Card>
        <p className="text-sm font-semibold text-white">Organization settings</p>
        <p className="text-xs text-slate-400">Single-tenant workspace configuration.</p>
        <div className="mt-4 space-y-3">
          <Input value={name} onChange={(e) => setName(e.target.value)} />
          <Input value={timezone} onChange={(e) => setTimezone(e.target.value)} />
          <Input value={supportEmail} onChange={(e) => setSupportEmail(e.target.value)} />
          <Button onClick={() => updateOrg.mutate()} disabled={updateOrg.isPending}>
            {updateOrg.isPending ? 'Saving...' : 'Save changes'}
          </Button>
        </div>
      </Card>

      <Card>
        <p className="text-sm font-semibold text-white">Feature flags</p>
        <p className="text-xs text-slate-400">Safely roll out features to internal teams.</p>
        <div className="mt-4 space-y-4">
          {flags?.map((flag) => (
            <div key={flag.key} className="flex items-center justify-between">
              <div>
                <p className="text-sm text-white">{flag.key}</p>
                <p className="text-xs text-slate-400">{flag.description}</p>
              </div>
              <Button
                variant={flag.enabled ? 'default' : 'outline'}
                size="sm"
                onClick={() => toggleFlag.mutate({ key: flag.key, enabled: !flag.enabled })}
              >
                {flag.enabled ? 'Enabled' : 'Disabled'}
              </Button>
            </div>
          ))}
        </div>
      </Card>
      </div>
    </AdminGate>
  );
}
