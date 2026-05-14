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
            <AddDailyLogForm onSuccess={fetchLogs} />
            <h1>Daily Logs</h1>

            {logs.length === 0 && (
                <p>No logs found</p>
            )}

            {logs.map(log => (

                <div
                    key={log.id}
                    style={{
                        border: "1px solid #333",
                        borderRadius: "12px",
                        padding: "15px",
                        marginBottom: "10px",
                        backgroundColor: "#1b1b1b"
                    }}
                >
                    <h2>{log.apartment.number}</h2>

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

        </MainLayout>
    );
}

export default DailyLogsPage;