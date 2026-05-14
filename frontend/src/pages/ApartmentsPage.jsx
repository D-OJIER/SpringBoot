import { useEffect, useState } from "react";

import MainLayout from "../layouts/MainLayout";
import TableContainer from "../components/TableContainer";
import api from "../api/axios";
import AddApartmentForm from "../components/AddApartmentForm";

function ApartmentsPage() {

    const [apartments, setApartments] = useState([]);

    useEffect(() => {
        fetchApartments();
    }, []);

    const fetchApartments = async () => {

        try {

            const response =
                await api.get("/apartments");

            setApartments(response.data);

        } catch (error) {

            console.error(error);
        }
    };

    return (

        <MainLayout>

            <h1>Apartments</h1>

            <AddApartmentForm onSuccess={fetchApartments} />

            <TableContainer title="Apartment List">

                <table style={{
                    width: "90%",
                    borderCollapse: "collapse"
                }}>

                    <thead>

                        <tr>

                            <th style={thStyle}>
                                Number
                            </th>

                            <th style={thStyle}>
                                Block
                            </th>

                            <th style={thStyle}>
                                Type
                            </th>

                            <th style={thStyle}>
                                Occupancy
                            </th>

                        </tr>

                    </thead>

                    <tbody>

                        {apartments.map(apartment => (

                            <tr key={apartment.id}>

                                <td style={tdStyle}>
                                    {apartment.number}
                                </td>

                                <td style={tdStyle}>
                                    {apartment.block.name}
                                </td>

                                <td style={tdStyle}>
                                    {apartment.type.name}
                                </td>

                                <td style={tdStyle}>
                                    {
                                        apartment.type
                                            .baseOccupancy
                                    }
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

export default ApartmentsPage;