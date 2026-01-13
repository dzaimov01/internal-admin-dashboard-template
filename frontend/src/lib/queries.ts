import { useQuery } from '@tanstack/react-query';
import { apiClient } from '@/lib/api';

export type MetricsSnapshot = {
  ordersByStatus: Record<string, number>;
  ticketsByPriority: Record<string, number>;
  activeCustomers: number;
};

export function useMetrics() {
  return useQuery({
    queryKey: ['metrics'],
    queryFn: () => apiClient.get<MetricsSnapshot>('/api/metrics')
  });
}

export type Customer = {
  id: number;
  name: string;
  email: string;
  status: string;
  notes?: string | null;
};

export type CustomerPage = {
  content: Customer[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
};

export function useCustomers(
  page = 0,
  search?: string,
  status?: string,
  sort?: string,
  direction?: string
) {
  return useQuery({
    queryKey: ['customers', page, search, status, sort, direction],
    queryFn: () => {
      const searchQuery = search ? `&search=${encodeURIComponent(search)}` : '';
      const statusQuery = status ? `&status=${status}` : '';
      const sortQuery = sort ? `&sort=${sort}` : '';
      const directionQuery = direction ? `&direction=${direction}` : '';
      return apiClient.get<CustomerPage>(
        `/api/customers?page=${page}&size=20${searchQuery}${statusQuery}${sortQuery}${directionQuery}`
      );
    }
  });
}

export type Order = {
  id: number;
  amount: number;
  status: string;
  createdAt: string;
  customer: Customer;
};

export type OrderPage = {
  content: Order[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
};

export function useOrders(status?: string, sort?: string, direction?: string) {
  return useQuery({
    queryKey: ['orders', status, sort, direction],
    queryFn: () => {
      const query = status ? `&status=${status}` : '';
      const sortQuery = sort ? `&sort=${sort}` : '';
      const directionQuery = direction ? `&direction=${direction}` : '';
      return apiClient.get<OrderPage>(
        `/api/orders?page=0&size=20${query}${sortQuery}${directionQuery}`
      );
    }
  });
}

export type Ticket = {
  id: number;
  title: string;
  priority: string;
  status: string;
  assignedTo?: { id: number; fullName: string } | null;
};

export type TicketPage = {
  content: Ticket[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
};

export function useTickets(
  status?: string,
  priority?: string,
  sort?: string,
  direction?: string
) {
  return useQuery({
    queryKey: ['tickets', status, priority, sort, direction],
    queryFn: () => {
      const statusQuery = status ? `&status=${status}` : '';
      const priorityQuery = priority ? `&priority=${priority}` : '';
      const sortQuery = sort ? `&sort=${sort}` : '';
      const directionQuery = direction ? `&direction=${direction}` : '';
      return apiClient.get<TicketPage>(
        `/api/tickets?page=0&size=20${statusQuery}${priorityQuery}${sortQuery}${directionQuery}`
      );
    }
  });
}

export type AuditLog = {
  id: number;
  actorEmail: string;
  action: string;
  entityType: string;
  entityId: string;
  changeSummary?: string;
  createdAt: string;
};

export function useAuditLogs(actorEmail?: string, action?: string, entityType?: string) {
  return useQuery({
    queryKey: ['audit', actorEmail, action, entityType],
    queryFn: () => {
      const params = new URLSearchParams();
      if (actorEmail) params.set('actorEmail', actorEmail);
      if (action) params.set('action', action);
      if (entityType) params.set('entityType', entityType);
      const query = params.toString();
      return apiClient.get<AuditLog[]>(`/api/audit${query ? `?${query}` : ''}`);
    }
  });
}

export type OrganizationSettings = {
  id: number;
  organizationName: string;
  timezone: string;
  supportEmail: string;
};

export type FeatureFlag = {
  id: number;
  key: string;
  enabled: boolean;
  description: string;
};

export function useOrganizationSettings() {
  return useQuery({
    queryKey: ['settings'],
    queryFn: () => apiClient.get<OrganizationSettings>('/api/settings/organization')
  });
}

export function useFeatureFlags() {
  return useQuery({
    queryKey: ['flags'],
    queryFn: () => apiClient.get<FeatureFlag[]>('/api/settings/flags')
  });
}

export type UserSummary = {
  id: number;
  email: string;
  fullName: string;
  roles: string[];
};

export function useUsers() {
  return useQuery({
    queryKey: ['users'],
    queryFn: () => apiClient.get<UserSummary[]>('/api/users')
  });
}

export type SystemHealth = {
  databaseOk: boolean;
  buildVersion: string;
  timestamp: string;
};

export function useSystemHealth() {
  return useQuery({
    queryKey: ['system'],
    queryFn: () => apiClient.get<SystemHealth>('/api/system/health')
  });
}
