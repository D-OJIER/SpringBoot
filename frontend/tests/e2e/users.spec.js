import { test, expect } from '@playwright/test';
import { UsersPage } from '../pages/UsersPage';
import { generateMockToken } from '../utils/token-helper';

test.describe('Users Management', () => {
    const adminToken = generateMockToken('admin', 'ADMIN');

    test.beforeEach(async ({ page }) => {
        await page.addInitScript((t) => localStorage.setItem('token', t), adminToken);
    });

    test('should display users list correctly', async ({ page }) => {
        const usersPage = new UsersPage(page);

        const mockUsers = [
            { id: 1, username: 'bob', role: 'RESIDENT', apartmentNumber: 'A-102' }
        ];

        // Mock API port 8090 requests
        await usersPage.mockUsersPageData(mockUsers, []);

        await usersPage.goto();

        // Specific, exact cell assertions
        await expect(page.locator('td:has-text("bob")')).toBeVisible();
        await expect(page.getByRole('cell', { name: 'RESIDENT', exact: true })).toBeVisible();
        await expect(page.locator('td:has-text("A-102")')).toBeVisible();
    });
});
