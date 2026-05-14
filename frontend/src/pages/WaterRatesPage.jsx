import { useEffect, useState } from "react";

import MainLayout from "../layouts/MainLayout";
import api from "../api/axios";
import AddWaterRateForm from "../components/AddWaterRateForm";
import TableContainer from "../components/TableContainer";

function WaterRatesPage() {

    const [rates, setRates] = useState([]);

    const [editingRateId, setEditingRateId] = useState(null);
    const [editForm, setEditForm] = useState({});

    const startEdit = (rate) => {

        setEditingRateId(rate.id);

        setEditForm({
            minLitres: rate.minLitres,
            maxLitres: rate.maxLitres,
            ratePerLitre: rate.ratePerLitre,
            effectiveFrom: rate.effectiveFrom,
            effectiveTo: rate.effectiveTo
        });
    };


    const handleEditChange = (e) => {

        setEditForm({
            ...editForm,
            [e.target.name]: e.target.value
        });
    };

    const saveEdit = async (rate) => {

        try {

            await api.put(

                `/water-rates/${rate.id}`,

                {
                    ...rate,

                    minLitres:
                        Number(editForm.minLitres),

                    maxLitres:
                        Number(editForm.maxLitres),

                    ratePerLitre:
                        Number(editForm.ratePerLitre),

                    effectiveFrom:
                        editForm.effectiveFrom,

                    effectiveTo:
                        editForm.effectiveTo
                }
            );

            setEditingRateId(null);

            fetchRates();

        } catch (error) {

            console.error(error);

            alert("Failed to update");
        }
    };
    
    useEffect(() => {
        fetchRates();
    }, []);

    const fetchRates = async () => {

        try {

            const response =
                await api.get("/water-rates");

            setRates(response.data);

        } catch (error) {

            console.error(error);
        }
    };

    const deleteRate = async (id) => {

        const confirmed =
            window.confirm(
                "Delete this rate?"
            );

        if (!confirmed) return;

        try {

            await api.delete(
                `/water-rates/${id}`
            );

            fetchRates();

        } catch (error) {

            console.error(error);

            alert("Failed to delete rate");
        }
    };

    return (

        <MainLayout>

            <h1>Water Rates</h1>

            <AddWaterRateForm onSuccess={fetchRates} />

            <TableContainer title="Water Rates">

                <table style={{
                    width: "100%",
                    borderCollapse: "collapse"
                }}>

                    <thead>

                        <tr>

                            <th style={thStyle}>Source</th>

                            <th style={thStyle}>Range</th>

                            <th style={thStyle}>Rate</th>

                            <th style={thStyle}>Effective From</th>

                            <th style={thStyle}>Actions</th>
                        
                        </tr>

                    </thead>

                    <tbody>

                        {rates.map(rate => (

                            <tr key={rate.id}>

                                <td style={tdStyle}>
                                    {rate.source.name}
                                </td>

                                <td style={tdStyle}>

                                    {editingRateId === rate.id ? (

                                        <div>

                                            <input
                                                name="minLitres"
                                                value={editForm.minLitres}
                                                onChange={handleEditChange}
                                                style={editInputStyle}
                                            />

                                            <input
                                                name="maxLitres"
                                                value={editForm.maxLitres}
                                                onChange={handleEditChange}
                                                style={editInputStyle}
                                            />

                                        </div>

                                    ) : (

                                        <>
                                            {rate.minLitres}
                                            {" - "}
                                            {rate.maxLitres}
                                        </>

                                    )}

                                </td>

                                <td style={tdStyle}>

                                    {editingRateId === rate.id ? (

                                        <input
                                            name="ratePerLitre"
                                            value={editForm.ratePerLitre}
                                            onChange={handleEditChange}
                                            style={editInputStyle}
                                        />

                                    ) : (

                                        <>₹{rate.ratePerLitre}</>

                                    )}

                                </td>

                                <td style={tdStyle}>
                                    {rate.effectiveFrom}
                                </td>
                                <td style={tdStyle}>

                                    {editingRateId === rate.id ? (

                                        <button
                                            onClick={() => saveEdit(rate)}
                                            style={saveButtonStyle}
                                        >
                                            Save
                                        </button>

                                    ) : (

                                        <>

                                            <button
                                                onClick={() => startEdit(rate)}
                                                style={editButtonStyle}
                                            >
                                                Edit
                                            </button>

                                            <button
                                                onClick={() => deleteRate(rate.id)}
                                                style={deleteButtonStyle}
                                            >
                                                Delete
                                            </button>

                                        </>

                                    )}

                                </td>

                            </tr>

                        ))}

                    </tbody>

                </table>

            </TableContainer>

        </MainLayout>
    );
}

export default WaterRatesPage;

const thStyle = {
    textAlign: "left",
    padding: "12px",
    borderBottom: "1px solid #333"
};

const tdStyle = {
    padding: "12px",
    borderBottom: "1px solid #222"
};

const deleteButtonStyle = {
    backgroundColor: "#dc2626",
    color: "white",
    border: "none",
    padding: "8px 12px",
    borderRadius: "8px",
    cursor: "pointer"
};

const editButtonStyle = {
    backgroundColor: "#2563eb",
    color: "white",
    border: "none",
    padding: "8px 12px",
    borderRadius: "8px",
    cursor: "pointer",
    marginRight: "10px"
};

const saveButtonStyle = {
    backgroundColor: "#16a34a",
    color: "white",
    border: "none",
    padding: "8px 12px",
    borderRadius: "8px",
    cursor: "pointer"
};

const editInputStyle = {
    width: "80px",
    padding: "6px",
    marginRight: "5px",
    backgroundColor: "#222",
    color: "white",
    border: "1px solid #444",
    borderRadius: "6px"
};