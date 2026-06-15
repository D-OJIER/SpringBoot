import { useEffect, useState } from "react";
import { Search } from "lucide-react";

import MainLayout from "../layouts/MainLayout";
import UsageChart from "../components/UsageChart";
import api from "../api/axios";
import {
  getDefaultDateRange,
  getEarliestAllowedDate,
  validateDateRange,
} from "../utils/dateRange";

function DashboardPage() {
  const [logs, setLogs] = useState([]);
  const [filters, setFilters] = useState({
    apartmentNumber: "",
    ...getDefaultDateRange(),
  });
  const [appliedFilters, setAppliedFilters] = useState(filters);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [stats, setStats] = useState({
    totalLogs: 0,
    totalUsage: 0,
    totalCost: 0,
  });

  useEffect(() => {
    async function loadDashboard() {
      try {
        const [statsResponse, logsResponse] = await Promise.all([
          api.get("/dashboard/stats", { params: appliedFilters }),
          api.get("/daily-logs", {
            params: {
              ...appliedFilters,
              page: 0,
              size: 100,
            },
          }),
        ]);

        setStats(statsResponse.data);
        setLogs(logsResponse.data.content);
        setError("");
      } catch (error) {
        console.error(error);
        setError(
          error.response?.data?.error || "Unable to load dashboard data",
        );
      } finally {
        setLoading(false);
      }
    }

    loadDashboard();
  }, [appliedFilters]);

  const applyFilters = (event) => {
    event.preventDefault();

    const validationError = validateDateRange(filters.fromDate, filters.toDate);
    if (validationError) {
      setError(validationError);
      return;
    }

    setError("");
    setLoading(true);
    setAppliedFilters(filters);
  };

  return (
    <MainLayout>
      <header className="page-header">
        <h1 className="page-title">Dashboard</h1>
      </header>

      <form className="filter-bar" onSubmit={applyFilters}>
        <input
          className="form-control"
          type="search"
          placeholder="Apartment number"
          value={filters.apartmentNumber}
          onChange={(event) =>
            setFilters({
              ...filters,
              apartmentNumber: event.target.value,
            })
          }
        />
        <input
          className="form-control"
          type="date"
          aria-label="From date"
          value={filters.fromDate}
          min={getEarliestAllowedDate(filters.toDate)}
          max={filters.toDate}
          onChange={(event) =>
            setFilters({
              ...filters,
              fromDate: event.target.value,
            })
          }
          required
        />
        <input
          className="form-control"
          type="date"
          aria-label="To date"
          value={filters.toDate}
          min={filters.fromDate}
          onChange={(event) =>
            setFilters({
              ...filters,
              toDate: event.target.value,
            })
          }
          required
        />
        <button className="button" type="submit">
          <Search size={18} />
          Apply
        </button>
      </form>

      <p className="page-subtitle">
        {appliedFilters.fromDate} to {appliedFilters.toDate}
      </p>

      {error && <p className="error-message">{error}</p>}

      <div className="stats-grid">
        <div className="card">
          <h2 className="card__title">Total Logs</h2>

          <p className="card__value">{stats.totalLogs}</p>
        </div>

        <div className="card">
          <h2 className="card__title">Total Usage</h2>

          <p className="card__value">{stats.totalUsage} L</p>
        </div>

        <div className="card">
          <h2 className="card__title">Total Cost</h2>

          <p className="card__value">₹{stats.totalCost}</p>
        </div>
      </div>

      {!loading && !error && stats.totalLogs === 0 ? (
        <div className="empty-state empty-state--panel">
          <strong>No dashboard data for this period</strong>
          <span>
            Add a daily log or choose a period containing recorded usage.
          </span>
        </div>
      ) : (
        <UsageChart data={logs} loading={loading} />
      )}
    </MainLayout>
  );
}

export default DashboardPage;
