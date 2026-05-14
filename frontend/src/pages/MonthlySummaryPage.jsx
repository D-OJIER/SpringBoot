import { useEffect, useState } from "react";

import MainLayout from "../layouts/MainLayout";
import TableContainer from "../components/TableContainer";
import api from "../api/axios";

function MonthlySummaryPage() {

    const [summary, setSummary] = useState([]);

    useEffect(() => {
        fetchSummary();
    }, []);

    const fetchSummary = async () => {

        try {

            const response =
                await api.get(
                    "/monthly-summary"
                );

            setSummary(response.data);

        } catch (error) {

            console.error(error);
        }
    };

    return (

        <MainLayout>

            <h1>Monthly Summary</h1>

            <TableContainer title="Billing Summary">

                <table style={{
                    width: "100%",
                    borderCollapse: "collapse"
                }}>

                    <thead>

                        <tr>

                            <th style={thStyle}>
                                Apartment
                            </th>

                            <th style={thStyle}>
                                Total Usage
                            </th>

                            <th style={thStyle}>
                                Total Cost
                            </th>

                        </tr>

                    </thead>

                    <tbody>

                        {summary.map((item, index) => (

                            <tr key={index}>

                                <td style={tdStyle}>
                                    {item.apartment}
                                </td>

                                <td style={tdStyle}>
                                    {item.totalUsage} L
                                </td>

                                <td style={tdStyle}>
                                    ₹{item.totalCost}
                                </td>

                            </tr>

                        ))}

                    </tbody>

                </table>

            </TableContainer>

        </MainLayout>
    );
}

const thStyle = {
    textAlign: "left",
    padding: "12px",
    borderBottom: "1px solid #333"
};

const tdStyle = {
    padding: "12px",
    borderBottom: "1px solid #222"
};

export default MonthlySummaryPage;