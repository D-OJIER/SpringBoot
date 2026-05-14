import { useEffect, useState } from "react";

import api from "../api/axios";

function AddApartmentForm({ onSuccess }) {

    const [blocks, setBlocks] = useState([]);

    const [types, setTypes] = useState([]);

    const [formData, setFormData] = useState({
        number: "",
        blockId: "",
        typeId: ""
    });

    useEffect(() => {

        fetchBlocks();
        fetchTypes();

    }, []);

    const fetchBlocks = async () => {

        try {

            const response =
                await api.get("/blocks");

            setBlocks(response.data);

        } catch (error) {

            console.error(error);
        }
    };

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

            await api.post("/apartments", {

                number: formData.number,

                block: {
                    id: Number(formData.blockId)
                },

                type: {
                    id: Number(formData.typeId)
                }
            });

            alert("Apartment Added");

            setFormData({
                number: "",
                blockId: "",
                typeId: ""
            });

            onSuccess();

        } catch (error) {

            console.error(error);

            alert("Failed to add apartment");
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

            <h2>Add Apartment</h2>

            <input
                type="text"
                name="number"
                placeholder="Apartment Number"
                value={formData.number}
                onChange={handleChange}
                required
                style={inputStyle}
            />

            <select
                name="blockId"
                value={formData.blockId}
                onChange={handleChange}
                required
                style={inputStyle}
            >

                <option value="">
                    Select Block
                </option>

                {blocks.map(block => (

                    <option
                        key={block.id}
                        value={block.id}
                    >
                        {block.name}
                    </option>

                ))}

            </select>

            <select
                name="typeId"
                value={formData.typeId}
                onChange={handleChange}
                required
                style={inputStyle}
            >

                <option value="">
                    Select Type
                </option>

                {types.map(type => (

                    <option
                        key={type.id}
                        value={type.id}
                    >
                        {type.name}
                    </option>

                ))}

            </select>

            <button
                type="submit"
                style={buttonStyle}
            >
                Add Apartment
            </button>

        </form>
    );
}

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

export default AddApartmentForm;