import { test, expect } from '@playwright/test';

test('viewer role hides admin-only nav items', async ({ page }) => {
  await page.addInitScript(() => {
    window.localStorage.setItem('accessToken', 'test-token');
  });

  await page.route('**/api/auth/me', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        email: 'viewer@acme.test',
        fullName: 'Viewer User',
        roles: ['Viewer']
      })
    });
  });

  await page.route('**/api/metrics', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        ordersByStatus: { NEW: 1, PROCESSING: 0, FULFILLED: 2, CANCELLED: 0 },
        ticketsByPriority: { LOW: 1, MEDIUM: 1, HIGH: 0, URGENT: 0 },
        activeCustomers: 3
      })
    });
  });

  await page.goto('/dashboard');
  await expect(page.getByText('Users')).toHaveCount(0);
  await expect(page.getByText('Settings')).toHaveCount(0);
});
