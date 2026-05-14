import { useEffect, useState } from "react";

import api from "../api/axios";

function AddWaterRateForm({ onSuccess }) {

    const [sources, setSources] = useState([]);

    const [formData, setFormData] = useState({
        minLitres: "",
        maxLitres: "",
        ratePerLitre: "",
        effectiveFrom: "",
        effectiveTo: "",
        sourceId: ""
    });

    useEffect(() => {
        fetchSources();
    }, []);

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

            await api.post("/water-rates", {

                minLitres:
                    Number(formData.minLitres),

                maxLitres:
                    Number(formData.maxLitres),

                ratePerLitre:
                    Number(formData.ratePerLitre),

                effectiveFrom:
                    formData.effectiveFrom,

                effectiveTo:
                    formData.effectiveTo,

                source: {
                    id: Number(formData.sourceId)
                }
            });

            alert("Rate Added");

            setFormData({
                minLitres: "",
                maxLitres: "",
                ratePerLitre: "",
                effectiveFrom: "",
                effectiveTo: "",
                sourceId: ""
            });

            onSuccess();

        } catch (error) {

            console.error(error);

            alert("Failed to add rate");
        }
    };

    return (

        <form
            onSubmit={handleSubmit}
            style={{
                backgroundColor: "#1b1b1b",
                padding: "20px",
                borderRadius: "16px",
                marginBottom: "20px",
                border: "1px solid #333"
            }}
        >

            <h2>Add Water Rate</h2>

            <input
                type="number"
                name="minLitres"
                placeholder="Min Litres"
                value={formData.minLitres}
                onChange={handleChange}
                required
                style={inputStyle}
            />

            <input
                type="number"
                name="maxLitres"
                placeholder="Max Litres"
                value={formData.maxLitres}
                onChange={handleChange}
                required
                style={inputStyle}
            />

            <input
                type="number"
                step="0.01"
                name="ratePerLitre"
                placeholder="Rate Per Litre"
                value={formData.ratePerLitre}
                onChange={handleChange}
                required
                style={inputStyle}
            />

            <input
                type="date"
                name="effectiveFrom"
                value={formData.effectiveFrom}
                onChange={handleChange}
                required
                style={inputStyle}
            />

            <input
                type="date"
                name="effectiveTo"
                value={formData.effectiveTo}
                onChange={handleChange}
                required
                style={inputStyle}
            />

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

            <button
                type="submit"
                style={buttonStyle}
            >
                Add Rate
            </button>

        </form>
    );
}

const inputStyle = {
    width: "98%",
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

export default AddWaterRateForm;