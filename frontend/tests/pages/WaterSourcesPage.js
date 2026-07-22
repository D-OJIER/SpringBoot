export class WaterSourcesPage {
    constructor(page) {
        this.page = page;
        this.header = page.getByRole('heading', { name: 'Water Sources' });
        this.nameInput = page.getByPlaceholder('Source Name');
        this.pricingTypeSelect = page.locator('select[name="pricingType"]');
        this.supplyTypeSelect = page.locator('select[name="supplyType"]');
        this.submitButton = page.getByRole('button', { name: 'Add Source' });
    }

    async goto() {
        await this.page.goto('/sources');
    }

    async addSource(name, pricing, supply) {
        await this.nameInput.fill(name);
        await this.pricingTypeSelect.selectOption(pricing);
        await this.supplyTypeSelect.selectOption(supply);
        await this.submitButton.click();
    }

    async mockSourcesResponse(sources) {
        await this.page.route('**/water-sources', async (route) => {
            await route.fulfill({
                status: 200,
                contentType: 'application/json',
                body: JSON.stringify(sources),
            });
        });
    }
}
