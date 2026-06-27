import React from 'react';
import { render } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';

function buildToken(expirySeconds) {
  const header = btoa(JSON.stringify({ alg: 'none', typ: 'JWT' }))
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=+$/, '');

  const payload = btoa(JSON.stringify({ exp: expirySeconds }))
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=+$/, '');

  return `${header}.${payload}.signature`;
}

describe('ProtectedRoute', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('redirects to login when there is no token', () => {
    const { queryByText } = render(
      <MemoryRouter>
        <ProtectedRoute>
          <div>Protected</div>
        </ProtectedRoute>
      </MemoryRouter>
    );

    expect(queryByText('Protected')).toBeNull();
  });

  it('redirects to login when the token has expired', () => {
    localStorage.setItem('token', buildToken(1));

    const { queryByText } = render(
      <MemoryRouter>
        <ProtectedRoute>
          <div>Protected</div>
        </ProtectedRoute>
      </MemoryRouter>
    );

    expect(queryByText('Protected')).toBeNull();
  });

  it('renders children when the token is valid', () => {
    const futureExp = Math.floor(Date.now() / 1000) + 60;
    localStorage.setItem('token', buildToken(futureExp));

    const { getByText } = render(
      <MemoryRouter>
        <ProtectedRoute>
          <div>Protected</div>
        </ProtectedRoute>
      </MemoryRouter>
    );

    expect(getByText('Protected')).toBeTruthy();
  });
});