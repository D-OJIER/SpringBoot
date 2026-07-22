import { test, expect } from '@playwright/test';
import { LoginPage } from '../pages/LoginPage';
import { generateMockToken } from '../utils/token-helper';

test.describe('Authentication & Token Flow', () => {

    test('should display all login elements correctly', async ({ page }) => {
        const loginPage = new LoginPage(page);
        await loginPage.goto();

        await expect(loginPage.header).toBeVisible();
        await expect(loginPage.usernameInput).toBeVisible();
        await expect(loginPage.passwordInput).toBeVisible();
        await expect(loginPage.loginButton).toBeVisible();
    });

    test('should show alert on invalid credentials', async ({ page }) => {
        const loginPage = new LoginPage(page);
        await loginPage.goto();

        await loginPage.mockLoginResponse(401, { message: 'Invalid username or password' });

        // 1. Start waiting for the dialog event
        const dialogPromise = page.waitForEvent('dialog');

        // 2. Trigger the dialog
        await loginPage.login('wronguser', 'wrongpass');

        // 3. Wait for the dialog to appear and accept it
        const dialog = await dialogPromise;
        expect(dialog.message()).toContain('Invalid username or password');
        await dialog.accept();
    });


    test('should store token in localStorage and redirect on success', async ({ page }) => {
        const loginPage = new LoginPage(page);
        await loginPage.goto();

        const mockToken = generateMockToken('admin', 'ADMIN');
        await page.route('**/auth/login', async (route) => {
            await route.fulfill({
                status: 200,
                contentType: 'application/json',
                body: JSON.stringify({ token: mockToken }),
            });
        });

        await loginPage.login('admin', 'admin123');

        await expect(page).toHaveURL(/\/dashboard/);
        const storedToken = await page.evaluate(() => localStorage.getItem('token'));
        expect(storedToken).toBe(mockToken);
    });

});
