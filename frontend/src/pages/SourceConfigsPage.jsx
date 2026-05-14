import { useEffect, useState } from "react";

import MainLayout from "../layouts/MainLayout";
import TableContainer from "../components/TableContainer";
import api from "../api/axios";

function SourceConfigsPage() {

    const [configs, setConfigs] = useState([]);

    const [apartments, setApartments] = useState([]);

    const [sources, setSources] = useState([]);

    const [formData, setFormData] = useState({
        apartmentId: "",
        sourceId: "",
        ratioPercent: ""
    });

    useEffect(() => {

        fetchConfigs();
        fetchApartments();
        fetchSources();

    }, []);

    const fetchConfigs = async () => {

        try {

            const response =
                await api.get(
                    "/apartment-source-configs"
                );

            setConfigs(response.data);

        } catch (error) {

            console.error(error);
        }
    };

    const fetchApartments = async () => {

        try {

            const response =
                await api.get("/apartments");

            setApartments(response.data);

        } catch (error) {

            console.error(error);
        }
    };

    const fetchSources = async () => {

        try {

            const response =
                await api.get("/water-sources");

            setSources(response.data);

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

            await api.post(
                "/apartment-source-configs",
                {
                    ratioPercent:
                        Number(
                            formData.ratioPercent
                        ),

                    apartment: {
                        id:
                            Number(
                                formData.apartmentId
                            )
                    },

                    source: {
                        id:
                            Number(
                                formData.sourceId
                            )
                    }
                }
            );

            setFormData({
                apartmentId: "",
                sourceId: "",
                ratioPercent: ""
            });

            fetchConfigs();

        } catch (error) {

            console.error(error);

            alert("Failed to add config");
        }
    };

    return (

        <MainLayout>

            <h1>Source Configurations</h1>

            <form
                onSubmit={handleSubmit}
                style={formStyle}
            >

                <h2>Add Configuration</h2>

                <select
                    name="apartmentId"
                    value={formData.apartmentId}
                    onChange={handleChange}
                    required
                    style={inputStyle}
                >

                    <option value="">
                        Select Apartment
                    </option>

                    {apartments.map(apartment => (

                        <option
                            key={apartment.id}
                            value={apartment.id}
                        >
                            {apartment.number}
                        </option>

                    ))}

                </select>

                <select
                    name="sourceId"
                    value={formData.sourceId}
                    onChange={handleChange}
                    required
                    style={inputStyle}
                >

                    <option value="">
                        Select Source
                    </option>

                    {sources.map(source => (

                        <option
                            key={source.id}
                            value={source.id}
                        >
                            {source.name}
                        </option>

                    ))}

                </select>

                <input
                    type="number"
                    name="ratioPercent"
                    placeholder="Ratio %"
                    value={formData.ratioPercent}
                    onChange={handleChange}
                    required
                    style={inputStyle}
                />

                <button
                    type="submit"
                    style={buttonStyle}
                >
                    Add Config
                </button>

            </form>

            <TableContainer title="Source Configurations">

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
                                Source
                            </th>

                            <th style={thStyle}>
                                Ratio %
                            </th>

                        </tr>

                    </thead>

                    <tbody>

                        {configs.map(config => (

                            <tr key={config.id}>

                                <td style={tdStyle}>
                                    {
                                        config.apartment
                                            .number
                                    }
                                </td>

                                <td style={tdStyle}>
                                    {
                                        config.source
                                            .name
                                    }
                                </td>

                                <td style={tdStyle}>
                                    {
                                        config.ratioPercent
                                    }%
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

export default SourceConfigsPage;