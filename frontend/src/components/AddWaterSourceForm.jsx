import { useState } from "react";
import api from "../api/axios";

function AddWaterSourceForm({ onSuccess }) {

    const [formData, setFormData] = useState({
        name: "",
        pricingType: "SLAB",
        supplyType: "MUNICIPAL"
    });

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
                "/water-sources",
                formData
            );

            alert("Water Source Added");

            setFormData({
                name: "",
                pricingType: "SLAB",
                supplyType: "MUNICIPAL"
            });

            onSuccess();

        } catch (error) {

            console.error(error);

            alert("Failed to add source");
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

            <h2>Add Water Source</h2>

            <input
                type="text"
                name="name"
                placeholder="Source Name"
                value={formData.name}
                onChange={handleChange}
                required
                style={inputStyle}
            />

            <select
                name="pricingType"
                value={formData.pricingType}
                onChange={handleChange}
                style={inputStyle}
            >

                <option value="SLAB">
                    SLAB
                </option>

            </select>

            <select
                name="supplyType"
                value={formData.supplyType}
                onChange={handleChange}
                style={inputStyle}
            >

                <option value="MUNICIPAL">
                    MUNICIPAL
                </option>

                <option value="PRIVATE">
                    PRIVATE
                </option>

                <option value="GROUND">
                    GROUND
                </option>

            </select>

            <button
                type="submit"
                style={buttonStyle}
            >
                Add Source
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

export default AddWaterSourceForm;