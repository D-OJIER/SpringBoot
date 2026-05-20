import { useEffect, useState } from "react";

import MainLayout from "../layouts/MainLayout";
import UsageChart from "../components/UsageChart";
import api from "../api/axios";

function DashboardPage() {

    const [logs, setLogs] = useState([]);

    const [stats, setStats] = useState({
        totalLogs: 0,
        totalUsage: 0,
        totalCost: 0
    });

    useEffect(() => {

        fetchStats();
        fetchLogs();

    }, []);

    const fetchStats = async () => {

        try {

            const response =
                await api.get("/dashboard/stats");

            setStats(response.data);

        } catch (error) {

            console.error(error);
        }
    };

    const fetchLogs = async () => {

        try {

            const response =
                await api.get("/daily-logs");

            setLogs(response.data);

        } catch (error) {

            console.error(error);
        }
    };

    return (

        <MainLayout>

            <header className="page-header">
                <h1 className="page-title">Dashboard</h1>
            </header>

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

            <UsageChart data={logs} />

        </MainLayout>
    );
}

export default DashboardPage;
