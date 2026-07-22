export class DailyLogsPage {
    constructor(page) {
        this.page = page;
        this.header = page.getByRole('heading', { name: 'Daily Logs' });

        // Filters
        this.apartmentInput = page.getByPlaceholder('Apartment number');
        this.fromDateInput = page.getByLabel('From date');
        this.toDateInput = page.getByLabel('To date');
        this.sortBySelect = page.getByLabel('Sort by');
        this.sortDirSelect = page.getByLabel('Sort direction');
        this.applyButton = page.getByRole('button', { name: 'Apply' });

        // Add Form Fields
        this.addLogDateInput = page.locator('input[name="logDate"]');
        this.addLitresInput = page.getByPlaceholder('Total Litres');
        this.addGuestsInput = page.getByPlaceholder('Guest Count');
        this.addApartmentSelect = page.locator('select[name="apartmentId"]');
        this.submitLogButton = page.getByRole('button', { name: 'Add Log' });

        // Pagination
        this.prevPageButton = page.getByLabel('Previous page');
        this.nextPageButton = page.getByLabel('Next page');
    }

    async goto() {
        await this.page.goto('/logs');
    }

    async addNewLog(date, litres, guests, apartmentId) {
        await this.addLogDateInput.fill(date);
        await this.addLitresInput.fill(litres.toString());
        await this.addGuestsInput.fill(guests.toString());
        await this.addApartmentSelect.selectOption(apartmentId.toString());
        await this.submitLogButton.click();
    }

    async mockLogsResponse(status, data) {
        await this.page.route('**/daily-logs*', async (route) => {
            await route.fulfill({
                status,
                contentType: 'application/json',
                body: JSON.stringify(data),
            });
        });
    }

    async mockApartmentsResponse(apartments) {
        await this.page.route('**/apartments', async (route) => {
            await route.fulfill({
                status: 200,
                contentType: 'application/json',
                body: JSON.stringify(apartments),
            });
        });
    }
}
