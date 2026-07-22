export class ApiClient {
  /**
   * @param {import('@playwright/test').APIRequestContext} request
   */
  constructor(request) {
    this.request = request;
  }

  // Pre-seed a daily log via direct backend HTTP call
  async createDailyLog(token, logData) {
    const response = await this.request.post('http://localhost:8080/daily-logs', {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      data: logData,
    });

    return response.json();
  }
}
