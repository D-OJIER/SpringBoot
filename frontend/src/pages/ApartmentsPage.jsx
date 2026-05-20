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

            <header className="page-header">
                <h1 className="page-title">Apartments</h1>
            </header>

            <AddApartmentForm onSuccess={fetchApartments} />

            <TableContainer title="Apartment List">

                <table className="data-table">

                    <thead>

                        <tr>

                            <th>
                                Number
                            </th>

                            <th>
                                Block
                            </th>

                            <th>
                                Type
                            </th>

                            <th>
                                Occupancy
                            </th>

                        </tr>

                    </thead>

                    <tbody>

                        {apartments.map(apartment => (

                            <tr key={apartment.id}>

                                <td>
                                    {apartment.number}
                                </td>

                                <td>
                                    {apartment.block.name}
                                </td>

                                <td>
                                    {apartment.type.name}
                                </td>

                                <td>
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

export default ApartmentsPage;
