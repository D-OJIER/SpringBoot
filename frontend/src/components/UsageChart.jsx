import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    ResponsiveContainer
} from "recharts";

function UsageChart({ data, loading = false }) {

    return (

        <div className="chart-panel">

            <h2 className="chart-panel__title">Usage Trend</h2>

            {loading ? (
                <p className="chart-status">Loading usage data...</p>
            ) : (
            <ResponsiveContainer
                width="100%"
                height={300}
            >

                <LineChart data={data}>

                    <CartesianGrid strokeDasharray="3 3" stroke="#d9e4ef" />

                    <XAxis dataKey="logDate" stroke="#617089" />

                    <YAxis stroke="#617089" />

                    <Tooltip />

                    <Line
                        type="monotone"
                        dataKey="totalLitresConsumed"
                        stroke="#0e7490"
                        strokeWidth={3}
                    />

                </LineChart>

            </ResponsiveContainer>
            )}

        </div>
    );
}

export default UsageChart;
