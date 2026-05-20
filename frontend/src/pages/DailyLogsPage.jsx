import { useEffect, useState } from "react";
import api from "../api/axios";
import MainLayout from "../layouts/MainLayout";
import AddDailyLogForm from "../components/AddDailyLogForm";

function DailyLogsPage() {

    const [logs, setLogs] = useState([]);

    useEffect(() => {
        fetchLogs();
    }, []);

    const fetchLogs = async () => {

        try {

            const response = await api.get("/daily-logs");

            console.log(response.data);

            setLogs(response.data);

        } catch (error) {

            console.error(error);
        }
    };

    return (
        <MainLayout>
            <header className="page-header">
                <h1 className="page-title">Daily Logs</h1>
            </header>

            <AddDailyLogForm onSuccess={fetchLogs} />

            {logs.length === 0 && (
                <p className="empty-state">No logs found</p>
            )}

            <div className="list-grid">
                {logs.map(log => (

                    <div
                        key={log.id}
                        className="list-card"
                    >
                        <h2 className="list-card__title">{log.apartment.number}</h2>

                        <p>Date: {log.logDate}</p>

                        <p>
                            Usage: {log.totalLitresConsumed} L
                        </p>

                        <p>
                            Guests: {log.guestCount}
                        </p>

                        <p>
                            Cost: ₹{log.dayCost}
                        </p>

                    </div>
                ))}
            </div>

        </MainLayout>
    );
}

export default DailyLogsPage;
