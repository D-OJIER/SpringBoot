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
    apartmentId: "",
  });

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      await api.post("/daily-logs", {
        logDate: formData.logDate,
        totalLitresConsumed: Number(formData.totalLitresConsumed),
        guestCount: Number(formData.guestCount),
        apartmentId: Number(formData.apartmentId),
      });

      alert("Daily Log Added");

      setFormData({
        logDate: "",
        totalLitresConsumed: "",
        guestCount: "",
        apartmentId: "",
      });

      onSuccess();
    } catch (error) {
      console.error(error);

      alert("Failed to add log");
    }
  };

  return (
    <form onSubmit={handleSubmit} className="form-panel">
      <h2 className="form-panel__title">Add Daily Log</h2>

      <div className="form-grid">
        <input
          type="date"
          name="logDate"
          value={formData.logDate}
          onChange={handleChange}
          required
          className="form-control"
        />

        <input
          type="number"
          name="totalLitresConsumed"
          placeholder="Total Litres"
          value={formData.totalLitresConsumed}
          onChange={handleChange}
          required
          className="form-control"
        />

        <input
          type="number"
          name="guestCount"
          placeholder="Guest Count"
          value={formData.guestCount}
          onChange={handleChange}
          required
          className="form-control"
        />

        <select
          name="apartmentId"
          value={formData.apartmentId}
          onChange={handleChange}
          required
          className="form-control"
        >
          <option value="">Select Apartment</option>

          {apartments.map((apartment) => (
            <option key={apartment.id} value={apartment.id}>
              {apartment.number}
            </option>
          ))}
        </select>

        <button type="submit" className="button">
          Add Log
        </button>
      </div>
    </form>
  );
}

export default AddDailyLogForm;
