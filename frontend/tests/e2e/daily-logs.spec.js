import { test, expect } from '@playwright/test';
import { DailyLogsPage } from '../pages/DailyLogsPage';
import { generateMockToken } from '../utils/token-helper';

test.describe('Daily Logs Management', () => {
    const adminToken = generateMockToken('admin', 'ADMIN');

    test.beforeEach(async ({ page }) => {
        const dailyLogsPage = new DailyLogsPage(page);
        await page.addInitScript((t) => localStorage.setItem('token', t), adminToken);
    });

    test('should display daily logs list and pagination', async ({ page }) => {
        const dailyLogsPage = new DailyLogsPage(page);

        await dailyLogsPage.mockLogsResponse(200, {
            content: [
                { id: 1, apartmentNumber: 'A-101', logDate: '2026-07-20', totalLitresConsumed: 150, guestCount: 2, dayCost: 450 }
            ],
            totalPages: 3
        });
        await dailyLogsPage.mockApartmentsResponse([]);

        await dailyLogsPage.goto();

        await expect(dailyLogsPage.header).toBeVisible();
        await expect(page.locator('text=A-101')).toBeVisible();
        await expect(page.locator('text=Usage: 150 L')).toBeVisible();

        // Verify pagination elements
        await expect(dailyLogsPage.nextPageButton).toBeEnabled();
        await expect(dailyLogsPage.prevPageButton).toBeDisabled();
    });

    test('should allow creating a daily log successfully', async ({ page }) => {
        const dailyLogsPage = new DailyLogsPage(page);

        await dailyLogsPage.mockLogsResponse(200, { content: [], totalPages: 1 });
        await dailyLogsPage.mockApartmentsResponse([{ id: 10, number: 'B-202' }]);

        await dailyLogsPage.goto();

        // Setup POST mock
        let postBody = null;
        await page.route('**/daily-logs', async (route) => {
            if (route.request().method() === 'POST') {
                postBody = route.request().postDataJSON();
                await route.fulfill({ status: 201, contentType: 'application/json', body: '{}' });
            }
        });

        // Handle dialog
        page.on('dialog', async (dialog) => {
            expect(dialog.message()).toContain('Daily Log Added');
            await dialog.accept();
        });

        await dailyLogsPage.addNewLog('2026-07-21', 300, 4, 10);

        expect(postBody).toEqual({
            logDate: '2026-07-21',
            totalLitresConsumed: 300,
            guestCount: 4,
            apartmentId: 10
        });
    });
});
