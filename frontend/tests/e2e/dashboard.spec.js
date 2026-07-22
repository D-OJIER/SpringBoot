import { test, expect } from '@playwright/test';
import { DashboardPage } from '../pages/DashboardPage';
import { generateMockToken } from '../utils/token-helper';
import { createMockStats, emptyStatsPayload } from '../payloads/stats.payload';

test.describe('Dashboard Statistics & Filters', () => {

    test.beforeEach(async ({ page }) => {
        const dashboardPage = new DashboardPage(page);
        // Generate valid session token
        const token = generateMockToken('admin_user', 'ADMIN');
        await dashboardPage.authenticateSession(token);
    });

    test('should display dashboard statistics correctly', async ({ page }) => {
        const dashboardPage = new DashboardPage(page);
        const mockStats = createMockStats(42, 1250, 3750);

        await dashboardPage.mockDashboardStats(mockStats);
        await dashboardPage.mockDailyLogs([]);

        await page.goto('/dashboard');

        await expect(dashboardPage.header).toBeVisible();
        await expect(page.locator('text=42')).toBeVisible();
        await expect(page.locator('text=1250 L')).toBeVisible();
        await expect(page.locator('text=₹3750')).toBeVisible();
    });

    test('should display empty state when zero logs exist', async ({ page }) => {
        const dashboardPage = new DashboardPage(page);

        await dashboardPage.mockDashboardStats(emptyStatsPayload);
        await dashboardPage.mockDailyLogs([]);

        await page.goto('/dashboard');

        await expect(page.locator('strong:has-text("No dashboard data for this period")')).toBeVisible();
    });

});
