import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    ResponsiveContainer
} from "recharts";

function UsageChart({ data }) {

    return (

        <div style={{
            backgroundColor: "#1b1b1b",
            padding: "20px",
            borderRadius: "16px",
            marginTop: "30px",
            border: "1px solid #333"
        }}>

            <h2>Usage Trend</h2>

            <ResponsiveContainer
                width="100%"
                height={300}
            >

                <LineChart data={data}>

                    <CartesianGrid strokeDasharray="3 3" />

                    <XAxis dataKey="logDate" />

                    <YAxis />

                    <Tooltip />

                    <Line
                        type="monotone"
                        dataKey="totalLitresConsumed"
                        stroke="#2563eb"
                    />

                </LineChart>

            </ResponsiveContainer>

        </div>
    );
}

export default UsageChart;