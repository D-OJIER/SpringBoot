import { useEffect, useState } from "react";
import { ChevronLeft, ChevronRight, Search } from "lucide-react";
import api from "../api/axios";
import MainLayout from "../layouts/MainLayout";
import AddDailyLogForm from "../components/AddDailyLogForm";
import {
  getDefaultDateRange,
  getEarliestAllowedDate,
  validateDateRange,
} from "../utils/dateRange";

function DailyLogsPage() {
  const [logs, setLogs] = useState([]);
  const [filters, setFilters] = useState({
    apartmentNumber: "",
    ...getDefaultDateRange(),
  });
  const [appliedFilters, setAppliedFilters] = useState(filters);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [refreshKey, setRefreshKey] = useState(0);

  useEffect(() => {
    async function loadLogs() {
      try {
        const response = await api.get("/daily-logs", {
          params: {
            page,
            size: 20,
            ...appliedFilters,
          },
        });

        setLogs(response.data.content);
        setTotalPages(response.data.totalPages);
        setError("");
      } catch (error) {
        console.error(error);
        setError(error.response?.data?.error || "Unable to load daily logs");
      } finally {
        setLoading(false);
      }
    }

    loadLogs();
  }, [appliedFilters, page, refreshKey]);

  const applyFilters = (event) => {
    event.preventDefault();
    const validationError = validateDateRange(filters.fromDate, filters.toDate);
    if (validationError) {
      setError(validationError);
      return;
    }

    setError("");
    setLoading(true);
    setPage(0);
    setAppliedFilters(filters);
  };

  const handleCreated = () => {
    setLoading(true);
    setPage(0);
    setRefreshKey((currentKey) => currentKey + 1);
  };

  return (
    <MainLayout>
      <header className="page-header">
        <h1 className="page-title">Daily Logs</h1>
      </header>

      <AddDailyLogForm onSuccess={handleCreated} />

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

      {error && <p className="error-message">{error}</p>}

      {!loading && logs.length === 0 && (
        <div className="empty-state">
          <strong>No daily logs found</strong>
          <span>
            Try another apartment or date range within the allowed period.
          </span>
        </div>
      )}

      <div className="list-grid">
        {logs.map((log) => (
          <div key={log.id} className="list-card">
            <h2 className="list-card__title">{log.apartment.number}</h2>

            <p>Date: {log.logDate}</p>

            <p>Usage: {log.totalLitresConsumed} L</p>

            <p>Guests: {log.guestCount}</p>

            <p>Cost: ₹{log.dayCost}</p>
          </div>
        ))}
      </div>

      {totalPages > 0 && (
        <nav className="pagination" aria-label="Daily log pages">
          <button
            className="button button--secondary"
            type="button"
            aria-label="Previous page"
            disabled={page === 0 || loading}
            onClick={() => {
              setLoading(true);
              setPage((currentPage) => currentPage - 1);
            }}
          >
            <ChevronLeft size={18} />
          </button>
          <span>
            Page {page + 1} of {totalPages}
          </span>
          <button
            className="button button--secondary"
            type="button"
            aria-label="Next page"
            disabled={page + 1 >= totalPages || loading}
            onClick={() => {
              setLoading(true);
              setPage((currentPage) => currentPage + 1);
            }}
          >
            <ChevronRight size={18} />
          </button>
        </nav>
      )}
    </MainLayout>
  );
}

export default DailyLogsPage;
