/**
 * Utility to generate a mocked JWT token with a specified username and role
 */
export function generateMockToken(username, role) {
    const header = btoa(JSON.stringify({ alg: "HS256", typ: "JWT" }));
    const payload = btoa(JSON.stringify({ sub: username, role: role, exp: Date.now() + 3600000 }));
    const signature = "mockedsignature123";

    return `${header}.${payload}.${signature}`;
}
