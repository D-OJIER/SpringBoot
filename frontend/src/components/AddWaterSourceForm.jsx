import { useState } from "react";
import api from "../api/axios";

function AddWaterSourceForm({ onSuccess }) {
  const [formData, setFormData] = useState({
    name: "",
    pricingType: "SLAB",
    supplyType: "MUNICIPAL",
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
      await api.post("/water-sources", formData);

      alert("Water Source Added");

      setFormData({
        name: "",
        pricingType: "SLAB",
        supplyType: "MUNICIPAL",
      });

      onSuccess();
    } catch (error) {
      console.error(error);

      alert("Failed to add source");
    }
  };

  return (
    <form onSubmit={handleSubmit} className="form-panel">
      <h2 className="form-panel__title">Add Water Source</h2>

      <div className="form-grid">
        <input
          type="text"
          name="name"
          placeholder="Source Name"
          value={formData.name}
          onChange={handleChange}
          required
          className="form-control"
        />

        <select
          name="pricingType"
          value={formData.pricingType}
          onChange={handleChange}
          className="form-control"
        >
          <option value="SLAB">SLAB</option>
        </select>

        <select
          name="supplyType"
          value={formData.supplyType}
          onChange={handleChange}
          className="form-control"
        >
          <option value="MUNICIPAL">MUNICIPAL</option>

          <option value="PRIVATE">PRIVATE</option>

          <option value="GROUND">GROUND</option>
        </select>

        <button type="submit" className="button">
          Add Source
        </button>
      </div>
    </form>
  );
}

export default AddWaterSourceForm;
