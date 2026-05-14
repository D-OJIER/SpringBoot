import { useEffect, useState } from "react";
import api from "../api/axios";

function AddDailyLogForm({ onSuccess }) {

    const [apartments, setApartments] = useState([]);

    useEffect(() => {
        fetchApartments();
    }, []);

    const fetchApartments = async () => {

        try {

            const response = await api.get("/apartments");

            setApartments(response.data);

        } catch (error) {

            console.error(error);
        }
    };

    const [formData, setFormData] = useState({
        logDate: "",
        totalLitresConsumed: "",
        guestCount: "",
        apartmentId: ""
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

            await api.post("/daily-logs", {
                logDate: formData.logDate,
                totalLitresConsumed: Number(formData.totalLitresConsumed),
                guestCount: Number(formData.guestCount),
                apartment: {
                    id: Number(formData.apartmentId)
                }
            });

            alert("Daily Log Added");

            setFormData({
                logDate: "",
                totalLitresConsumed: "",
                guestCount: "",
                apartmentId: ""
            });

            onSuccess();

        } catch (error) {

            console.error(error);

            alert("Failed to add log");
        }
    };

    return (

        <form
            onSubmit={handleSubmit}
            style={{
                marginBottom: "20px",
                padding: "20px",
                border: "1px solid #333",
                borderRadius: "12px",
                backgroundColor: "#1b1b1b"
            }}
        >

            <h2>Add Daily Log</h2>

            <input
                type="date"
                name="logDate"
                value={formData.logDate}
                onChange={handleChange}
                required
                style={inputStyle}
            />

            <input
                type="number"
                name="totalLitresConsumed"
                placeholder="Total Litres"
                value={formData.totalLitresConsumed}
                onChange={handleChange}
                required
                style={inputStyle}
            />

            <input
                type="number"
                name="guestCount"
                placeholder="Guest Count"
                value={formData.guestCount}
                onChange={handleChange}
                required
                style={inputStyle}
            />

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

                    <option style={{ width: "100%" }}
                        key={apartment.id}
                        value={apartment.id}
                    >
                        {apartment.number}
                    </option>

                ))}

            </select>

            <button
                type="submit"
                style={buttonStyle}
            >
                Add Log
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

export default AddDailyLogForm;