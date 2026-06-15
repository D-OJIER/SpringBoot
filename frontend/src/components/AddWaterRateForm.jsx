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
    sourceId: "",
  });

  useEffect(() => {
    fetchSources();
  }, []);

  const fetchSources = async () => {
    try {
      const response = await api.get("/water-sources");

      setSources(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      await api.post("/water-rates", {
        minLitres: Number(formData.minLitres),

        maxLitres: Number(formData.maxLitres),

        ratePerLitre: Number(formData.ratePerLitre),

        effectiveFrom: formData.effectiveFrom,

        effectiveTo: formData.effectiveTo,

        source: {
          id: Number(formData.sourceId),
        },
      });

      alert("Rate Added");

      setFormData({
        minLitres: "",
        maxLitres: "",
        ratePerLitre: "",
        effectiveFrom: "",
        effectiveTo: "",
        sourceId: "",
      });

      onSuccess();
    } catch (error) {
      console.error(error);

      alert("Failed to add rate");
    }
  };

  return (
    <form onSubmit={handleSubmit} className="form-panel">
      <h2 className="form-panel__title">Add Water Rate</h2>

      <div className="form-grid">
        <input
          type="number"
          name="minLitres"
          placeholder="Min Litres"
          value={formData.minLitres}
          onChange={handleChange}
          required
          className="form-control"
        />

        <input
          type="number"
          name="maxLitres"
          placeholder="Max Litres"
          value={formData.maxLitres}
          onChange={handleChange}
          required
          className="form-control"
        />

        <input
          type="number"
          step="0.01"
          name="ratePerLitre"
          placeholder="Rate Per Litre"
          value={formData.ratePerLitre}
          onChange={handleChange}
          required
          className="form-control"
        />

        <input
          type="date"
          name="effectiveFrom"
          value={formData.effectiveFrom}
          onChange={handleChange}
          required
          className="form-control"
        />

        <input
          type="date"
          name="effectiveTo"
          value={formData.effectiveTo}
          onChange={handleChange}
          required
          className="form-control"
        />

        <select
          name="sourceId"
          value={formData.sourceId}
          onChange={handleChange}
          required
          className="form-control"
        >
          <option value="">Select Source</option>

          {sources.map((source) => (
            <option key={source.id} value={source.id}>
              {source.name}
            </option>
          ))}
        </select>

        <button type="submit" className="button">
          Add Rate
        </button>
      </div>
    </form>
  );
}

export default AddWaterRateForm;
