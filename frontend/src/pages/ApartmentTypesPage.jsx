import { useEffect, useState } from "react";

import MainLayout from "../layouts/MainLayout";
import TableContainer from "../components/TableContainer";
import api from "../api/axios";

function ApartmentTypesPage() {

    const [types, setTypes] = useState([]);

    const [formData, setFormData] = useState({
        name: "",
        baseOccupancy: "",
        litresPerPerson: ""
    });

    useEffect(() => {
        fetchTypes();
    }, []);

    const fetchTypes = async () => {

        try {

            const response =
                await api.get("/apartment-types");

            setTypes(response.data);

        } catch (error) {

            console.error(error);
        }
    };

    const handleChange = (e) => {

        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {

        e.preventDefault();

        try {

            await api.post("/apartment-types", {

                name: formData.name,

                baseOccupancy:
                    Number(formData.baseOccupancy),

                litresPerPerson:
                    Number(formData.litresPerPerson)
            });

            alert("Apartment Type Added");

            setFormData({
                name: "",
                baseOccupancy: "",
                litresPerPerson: ""
            });

            fetchTypes();

        } catch (error) {

            console.error(error);

            alert("Failed to add type");
        }
    };

    return (

        <MainLayout>

            <h1>Apartment Types</h1>

            <form
                onSubmit={handleSubmit}
                style={formStyle}
            >

                <h2>Add Apartment Type</h2>

                <input
                    type="text"
                    name="name"
                    placeholder="Type Name"
                    value={formData.name}
                    onChange={handleChange}
                    required
                    style={inputStyle}
                />

                <input
                    type="number"
                    name="baseOccupancy"
                    placeholder="Base Occupancy"
                    value={formData.baseOccupancy}
                    onChange={handleChange}
                    required
                    style={inputStyle}
                />

                <input
                    type="number"
                    step="0.01"
                    name="litresPerPerson"
                    placeholder="Litres Per Person"
                    value={formData.litresPerPerson}
                    onChange={handleChange}
                    required
                    style={inputStyle}
                />

                <button
                    type="submit"
                    style={buttonStyle}
                >
                    Add Type
                </button>

            </form>

            <TableContainer title="Apartment Types">

                <table style={{
                    width: "100%",
                    borderCollapse: "collapse"
                }}>

                    <thead>

                        <tr>

                            <th style={thStyle}>
                                Name
                            </th>

                            <th style={thStyle}>
                                Occupancy
                            </th>

                            <th style={thStyle}>
                                Litres / Person
                            </th>

                        </tr>

                    </thead>

                    <tbody>

                        {types.map(type => (

                            <tr key={type.id}>

                                <td style={tdStyle}>
                                    {type.name}
                                </td>

                                <td style={tdStyle}>
                                    {type.baseOccupancy}
                                </td>

                                <td style={tdStyle}>
                                    {type.litresPerPerson}
                                </td>

                            </tr>

                        ))}

                    </tbody>

                </table>

            </TableContainer>

        </MainLayout>
    );
}

const formStyle = {
    backgroundColor: "#1b1b1b",
    padding: "20px",
    borderRadius: "16px",
    marginBottom: "20px",
    border: "1px solid #333"
};

const inputStyle = {
    width: "100%",
    padding: "10px",
    marginBottom: "10px",
    borderRadius: "8px",
    border: "1px solid #444",
    backgroundColor: "#222",
    color: "white"
};

const buttonStyle = {
    padding: "10px 20px",
    borderRadius: "8px",
    border: "none",
    backgroundColor: "#2563eb",
    color: "white",
    cursor: "pointer"
};

const thStyle = {
    textAlign: "left",
    padding: "12px",
    borderBottom: "1px solid #333"
};

const tdStyle = {
    padding: "12px",
    borderBottom: "1px solid #222"
};

export default ApartmentTypesPage;