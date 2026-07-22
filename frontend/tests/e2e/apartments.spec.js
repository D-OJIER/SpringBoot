import { test, expect } from '@playwright/test';
import { ApartmentsPage } from '../pages/ApartmentsPage';
import { generateMockToken } from '../utils/token-helper';

test.describe('Apartments Management', () => {
    const adminToken = generateMockToken('admin', 'ADMIN');

    test.beforeEach(async ({ page }) => {
        await page.addInitScript((t) => localStorage.setItem('token', t), adminToken);
    });

    test('should load apartments page components', async ({ page }) => {
        const aptPage = new ApartmentsPage(page);

        const mockApt = [
            { id: 1, number: 'C-303', block: { name: 'Block C' }, type: { name: 'Penthouse', baseOccupancy: 4 } }
        ];
        await aptPage.mockApartmentsPageData(mockApt, [], []);

        await aptPage.goto();

        await expect(page.locator('td:has-text("C-303")')).toBeVisible();
        await expect(page.locator('td:has-text("Block C")')).toBeVisible();
        await expect(page.locator('td:has-text("Penthouse")')).toBeVisible();
    });
});
