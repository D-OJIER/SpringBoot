export class WaterRatesPage {
    constructor(page) {
        this.page = page;
        this.header = page.getByRole('heading', { name: 'Water Rates' });

        // Add Rate Form Fields
        this.minLitresInput = page.locator('input[name="minLitres"]');
        this.maxLitresInput = page.locator('input[name="maxLitres"]');
        this.ratePerLitreInput = page.locator('input[name="ratePerLitre"]');
        this.effectiveFromInput = page.locator('input[name="effectiveFrom"]');
        this.sourceSelect = page.locator('select[name="sourceId"]');
        this.submitButton = page.getByRole('button', { name: 'Add Rate' });
    }

    async goto() {
        await this.page.goto('/rates');
    }

    async mockRatesResponse(rates) {
        await this.page.route('**/water-rates*', async (route) => {
            await route.fulfill({
                status: 200,
                contentType: 'application/json',
                body: JSON.stringify(rates),
            });
        });
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
