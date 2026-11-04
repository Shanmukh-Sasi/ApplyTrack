import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/',
  withCredentials: true,
  headers: { 'Content-Type': 'application/json' },
});

export function errorMessage(error) {
  const data = error?.response?.data;
  if (typeof data === 'string' && data.trim()) return data;
  if (data?.message && data?.details) return `${data.message}: ${data.details}`;
  if (data?.message) return data.message;
  if (data?.error) return data.error;
  if (error?.response?.status === 403) return 'Request was rejected. Refresh the page and try again (CSRF/session token may have expired).';
  if (error?.response?.status === 401) return 'Please log in to continue.';
  return error?.message || 'Something went wrong. Please try again.';
}

async function csrfHeaders() {
  // Ask Spring Security to create or refresh its XSRF-TOKEN cookie first.
  await api.get('/auth/csrf');

  // With Spring Security's SPA CSRF handler, send the plain cookie token back
  // in the matching header. The browser sends the session cookie automatically.
  const cookie = document.cookie
    .split('; ')
    .find((part) => part.startsWith('XSRF-TOKEN='));
  const token = cookie ? decodeURIComponent(cookie.slice('XSRF-TOKEN='.length)) : '';

  if (!token) {
    throw new Error('CSRF token cookie was not set. Check the backend CSRF endpoint and frontend proxy.');
  }

  return { 'X-XSRF-TOKEN': token };
}

export const authApi = {
  csrf: () => api.get('/auth/csrf'),
  login: async (credentials) => api.post('/auth/login', credentials, { headers: await csrfHeaders() }),
  register: async (payload) => api.post('/user/createUser', payload, { headers: await csrfHeaders() }),
  logout: async () => api.post('/logout', {}, { headers: await csrfHeaders() }),
};

export function applicationListParams({ page = 0, size = 10, sort = 'id,desc', company = '', status = '' } = {}) {
  const params = { page, size, sort };
  if (company.trim()) params.company = company.trim();
  return { params, status };
}

export const applicationApi = {
  list: ({ page = 0, size = 10, sort = 'id,desc', company = '', status = '' } = {}) => {
    const { params } = applicationListParams({ page, size, sort, company });
    const path = status ? `/application/status/${encodeURIComponent(status)}` : '/application/search';
    return api.get(path, { params });
  },
  details: (id) => api.get(`/application/viewstatus/${id}`),
  create: async (payload) => api.post('/application/create', payload, { headers: await csrfHeaders() }),
  update: async (id, payload) => api.put(`/application/update/${id}`, payload, { headers: await csrfHeaders() }),
  updateStatus: async (id, status) => api.patch(`/application/UpdateStatus/${id}`, { status }, { headers: await csrfHeaders() }),
  remove: async (id) => api.delete(`/application/delete/${id}`, { headers: await csrfHeaders() }),
};

export const interviewApi = {
  list: () => api.get('/interview/viewall'),
  upcoming: () => api.get('/interview/upcoming'),
  byApplication: (applicationId) => api.get(`/interview/application/${applicationId}`),
  details: (id) => api.get(`/interview/${id}`),
  create: async (applicationId, payload) => api.post(`/interview/create/${applicationId}`, payload, { headers: await csrfHeaders() }),
  update: async (id, payload) => api.put(`/interview/update/${id}`, payload, { headers: await csrfHeaders() }),
  updateOutcome: async (id, outcome) => api.patch(`/interview/outcome/${id}`, { outcome }, { headers: await csrfHeaders() }),
  remove: async (id) => api.delete(`/interview/delete/${id}`, { headers: await csrfHeaders() }),
};

export default api;
