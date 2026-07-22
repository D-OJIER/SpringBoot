import { test, expect } from '@playwright/test';
import { WaterSourcesPage } from '../pages/WaterSourcesPage';
import { generateMockToken } from '../utils/token-helper';

test.describe('Water Sources Management', () => {
    const adminToken = generateMockToken('admin', 'ADMIN');

    test.beforeEach(async ({ page }) => {
        await page.addInitScript((t) => localStorage.setItem('token', t), adminToken);
    });

    test('should load water sources and add new source', async ({ page }) => {
        const wsPage = new WaterSourcesPage(page);

        await wsPage.mockSourcesResponse([
            { id: 1, name: 'Municipal Supply', pricingType: 'SLAB', supplyType: 'MUNICIPAL' }
        ]);

        await wsPage.goto();
        await expect(page.locator('text=Municipal Supply')).toBeVisible();

        // Mock POST request
        await page.route('*://*:8090/water-sources', async (route) => {
            if (route.request().method() === 'POST') {
                await route.fulfill({ status: 201, contentType: 'application/json', body: '{}' });
            }
        });

        // 1. Start waiting for the dialog event
        const dialogPromise = page.waitForEvent('dialog');

        // 2. Trigger the dialog
        await wsPage.addSource('Borewell', 'SLAB', 'GROUND');

        // 3. Wait for the dialog to appear and accept it
        const dialog = await dialogPromise;
        expect(dialog.message()).toContain('Water Source Added');
        await dialog.accept();
    });

});
