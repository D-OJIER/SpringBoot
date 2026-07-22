export class UsersPage {
    constructor(page) {
        this.page = page;
        this.header = page.getByRole('heading', { name: 'User Management' });

        // Add Form Fields
        this.usernameInput = page.getByPlaceholder('Username');
        this.passwordInput = page.getByPlaceholder('Password');
        this.roleSelect = page.locator('select[name="role"]');
        this.apartmentSelect = page.locator('select[name="apartmentId"]');
        this.submitButton = page.getByRole('button', { name: 'Add User' });
    }

    async goto() {
        await this.page.goto('/users');
    }

    async mockUsersPageData(users, apartments) {
        await this.page.route('*://*:8090/users', async (route) => {
            await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(users) });
        });
        await this.page.route('*://*:8090/apartments', async (route) => {
            await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(apartments) });
        });
    }

}
