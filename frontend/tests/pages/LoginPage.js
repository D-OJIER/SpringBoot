export class LoginPage {
    constructor(page) {
        this.page = page;
        this.usernameInput = page.getByPlaceholder('Username');
        this.passwordInput = page.getByPlaceholder('Password');
        this.loginButton = page.getByRole('button', { name: 'Login' });
        this.header = page.getByRole('heading', { name: 'Login' });
    }

    async goto() {
        await this.page.goto('/login');
    }

    async login(username, password) {
        await this.usernameInput.fill(username);
        await this.passwordInput.fill(password);
        await this.loginButton.click();
    }

    async mockLoginResponse(status, data) {
        await this.page.route('*://*:8090/auth/login', async (route) => {
            await route.fulfill({
                status,
                contentType: 'application/json',
                body: JSON.stringify(data),
            });
        });
    }

}
