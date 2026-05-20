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

            <header className="page-header">
                <h1 className="page-title">Monthly Summary</h1>
            </header>

            <TableContainer title="Billing Summary">

                <table className="data-table">

                    <thead>

                        <tr>

                            <th>
                                Apartment
                            </th>

                            <th>
                                Total Usage
                            </th>

                            <th>
                                Total Cost
                            </th>

                        </tr>

                    </thead>

                    <tbody>

                        {summary.map((item, index) => (

                            <tr key={index}>

                                <td>
                                    {item.apartment}
                                </td>

                                <td>
                                    {item.totalUsage} L
                                </td>

                                <td>
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

export default MonthlySummaryPage;
