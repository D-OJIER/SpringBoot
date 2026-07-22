import { test, expect } from '@playwright/test';
import { WaterRatesPage } from '../pages/WaterRatesPage';
import { generateMockToken } from '../utils/token-helper';

test.describe('Water Rates Management', () => {
    const adminToken = generateMockToken('admin', 'ADMIN');

    test.beforeEach(async ({ page }) => {
        await page.addInitScript((t) => localStorage.setItem('token', t), adminToken);
    });

    test('should load rates table and edit rate inline', async ({ page }) => {
        const ratesPage = new WaterRatesPage(page);

        await ratesPage.mockRatesResponse([
            { id: 1, minLitres: 0, maxLitres: 1000, ratePerLitre: 5.5, effectiveFrom: '2026-01-01', source: { id: 2, name: 'Main Well' } }
        ]);
        await ratesPage.mockSourcesResponse([]);

        await ratesPage.goto();

        await expect(page.locator('text=Main Well')).toBeVisible();
        await expect(page.locator('text=0 - 1000')).toBeVisible();

        // Trigger inline edit
        await page.getByRole('button', { name: 'Edit' }).click();

        // Verify inputs appeared in the table cell
        const minInput = page.locator('tbody input[name="minLitres"]');
        await expect(minInput).toHaveValue('0');
        await minInput.fill('100');

        // Mock PUT update request
        let putData = null;
        await page.route('**/water-rates/1', async (route) => {
            putData = route.request().postDataJSON();
            await route.fulfill({ status: 200 });
        });

        await page.getByRole('button', { name: 'Save' }).click();
        expect(putData.minLitres).toBe(100);
    });
});
