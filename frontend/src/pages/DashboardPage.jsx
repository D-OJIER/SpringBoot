import { useEffect, useState } from "react";

import MainLayout from "../layouts/MainLayout";
import UsageChart from "../components/UsageChart";
import api from "../api/axios";
import { getDefaultDateRange } from "../utils/dateRange";

function DashboardPage() {

    const [logs, setLogs] = useState([]);
    const [dateRange] = useState(getDefaultDateRange);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [stats, setStats] = useState({
        totalLogs: 0,
        totalUsage: 0,
        totalCost: 0
    });

    useEffect(() => {
        async function loadDashboard() {
            try {
                const [statsResponse, logsResponse] = await Promise.all([
                    api.get("/dashboard/stats", { params: dateRange }),
                    api.get("/daily-logs", {
                        params: {
                            ...dateRange,
                            page: 0,
                            size: 100
                        }
                    })
                ]);

                setStats(statsResponse.data);
                setLogs(logsResponse.data.content);
                setError("");
            } catch (error) {
                console.error(error);
                setError(error.response?.data?.error || "Unable to load dashboard data");
            } finally {
                setLoading(false);
            }
        }

        loadDashboard();
    }, [dateRange]);

    return (

        <MainLayout>

            <header className="page-header">
                <h1 className="page-title">Dashboard</h1>
                <p className="page-subtitle">
                    {dateRange.fromDate} to {dateRange.toDate}
                </p>
            </header>

            {error && <p className="error-message">{error}</p>}

            <div className="stats-grid">

                <div className="card">
                    <h2 className="card__title">Total Logs</h2>

                    <p className="card__value">
                        {stats.totalLogs}
                    </p>
                </div>

                <div className="card">
                    <h2 className="card__title">Total Usage</h2>

                    <p className="card__value">
                        {stats.totalUsage} L
                    </p>
                </div>

                <div className="card">
                    <h2 className="card__title">Total Cost</h2>

                    <p className="card__value">
                        ₹{stats.totalCost}
                    </p>
                </div>

            </div>

            {!loading && !error && stats.totalLogs === 0 ? (
                <div className="empty-state empty-state--panel">
                    <strong>No dashboard data for this period</strong>
                    <span>Add a daily log or choose a period containing recorded usage.</span>
                </div>
            ) : (
                <UsageChart data={logs} loading={loading} />
            )}

        </MainLayout>
    );
}

export default DashboardPage;
