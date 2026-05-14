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

            <h1>Dashboard</h1>

            <div style={{
                display: "grid",
                gridTemplateColumns: "repeat(3, 1fr)",
                gap: "20px",
                marginTop: "20px"
            }}>

                <div style={cardStyle}>
                    <h2>Total Logs</h2>

                    <p style={numberStyle}>
                        {stats.totalLogs}
                    </p>
                </div>

                <div style={cardStyle}>
                    <h2>Total Usage</h2>

                    <p style={numberStyle}>
                        {stats.totalUsage} L
                    </p>
                </div>

                <div style={cardStyle}>
                    <h2>Total Cost</h2>

                    <p style={numberStyle}>
                        ₹{stats.totalCost}
                    </p>
                </div>

            </div>

            <UsageChart data={logs} />

        </MainLayout>
    );
}

const cardStyle = {
    backgroundColor: "#1b1b1b",
    padding: "20px",
    borderRadius: "16px",
    border: "1px solid #333"
};

const numberStyle = {
    fontSize: "2rem",
    fontWeight: "bold"
};

export default DashboardPage;