import { useEffect, useState } from "react";

import MainLayout from "../layouts/MainLayout";
import TableContainer from "../components/TableContainer";
import api from "../api/axios";
import { getDefaultDateRange } from "../utils/dateRange";

function MonthlySummaryPage() {
  const [summary, setSummary] = useState([]);
  const [dateRange] = useState(getDefaultDateRange);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadSummary() {
      try {
        const response = await api.get("/monthly-summary", {
          params: dateRange,
        });

        setSummary(response.data);
        setError("");
      } catch (error) {
        console.error(error);
        setError(
          error.response?.data?.error || "Unable to load monthly summary",
        );
      } finally {
        setLoading(false);
      }
    }

    loadSummary();
  }, [dateRange]);

  return (
    <MainLayout>
      <header className="page-header">
        <h1 className="page-title">Monthly Summary</h1>
        <p className="page-subtitle">
          {dateRange.fromDate} to {dateRange.toDate}
        </p>
      </header>

      {error && <p className="error-message">{error}</p>}

      {!loading && !error && summary.length === 0 ? (
        <div className="empty-state empty-state--panel">
          <strong>No summary data for this period</strong>
          <span>Billing totals appear after daily logs are recorded.</span>
        </div>
      ) : (
        <TableContainer title="Billing Summary">
          <table className="data-table">
            <thead>
              <tr>
                <th>Apartment</th>

                <th>Total Usage</th>

                <th>Total Cost</th>
              </tr>
            </thead>

            <tbody>
              {summary.map((item, index) => (
                <tr key={index}>
                  <td>{item.apartment}</td>

                  <td>{item.totalUsage} L</td>

                  <td>₹{item.totalCost}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </TableContainer>
      )}
    </MainLayout>
  );
}

export default MonthlySummaryPage;
