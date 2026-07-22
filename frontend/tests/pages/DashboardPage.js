export class DashboardPage {
  constructor(page) {
    this.page = page;
    this.header = page.getByRole('heading', { name: 'Dashboard' });
    this.apartmentInput = page.getByPlaceholder('Apartment number');
    this.applyButton = page.getByRole('button', { name: 'Apply' });
  }

  async authenticateSession(token) {
    await this.page.addInitScript((jwt) => {
      localStorage.setItem('token', jwt);
    }, token);
  }

  async mockDashboardStats(stats) {
    await this.page.route('**/dashboard/stats*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify(stats),
      });
    });
  }

  async mockDailyLogs(logs) {
    await this.page.route('**/daily-logs*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({ content: logs }),
      });
    });
  }
}
