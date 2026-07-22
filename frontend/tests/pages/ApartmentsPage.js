export class ApartmentsPage {
  constructor(page) {
    this.page = page;
    this.header = page.getByRole('heading', { name: 'Apartments' });

    // Add Form Fields
    this.numberInput = page.getByPlaceholder('Apartment Number');
    this.blockSelect = page.locator('select[name="blockId"]');
    this.typeSelect = page.locator('select[name="apartmentTypeId"]');
    this.submitButton = page.getByRole('button', { name: 'Add Apartment' });
  }

  async goto() {
    await this.page.goto('/apartments');
  }

  async mockApartmentsPageData(apartments, blocks, types) {
    await this.page.route('*://*:8090/apartments', async (route) => {
      await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(apartments) });
    });
    await this.page.route('*://*:8090/blocks', async (route) => {
      await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(blocks) });
    });
    await this.page.route('*://*:8090/apartment-types', async (route) => {
      await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(types) });
    });
  }

}
