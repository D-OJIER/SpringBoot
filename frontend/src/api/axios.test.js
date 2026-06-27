import api from './axios';
import { vi } from 'vitest';

describe('axios interceptor', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('adds Authorization header when token exists', () => {
    localStorage.setItem('token', 'abc123');

    const handler = api.interceptors.request.handlers[0].fulfilled;
    const config = { headers: {} };
    const result = handler(config);

    expect(result.headers.Authorization).toBe('Bearer abc123');
  });

  it('redirects to login and clears token on 401', async () => {
    localStorage.setItem('token', 'abc123');
    delete window.location;
    window.location = { href: '' };

    const handler = api.interceptors.response.handlers[0].rejected;
    await expect(handler({ response: { status: 401 } })).rejects.toMatchObject({ response: { status: 401 } });

    expect(localStorage.getItem('token')).toBeNull();
    expect(window.location.href).toBe('/login');
  });
});
