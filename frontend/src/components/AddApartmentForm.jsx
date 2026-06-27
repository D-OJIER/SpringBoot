import { useEffect, useState } from "react";

import api from "../api/axios";

function AddApartmentForm({ onSuccess }) {
  const [blocks, setBlocks] = useState([]);

  const [types, setTypes] = useState([]);

  const [formData, setFormData] = useState({
    number: "",
    blockId: "",
    typeId: "",
  });

  useEffect(() => {
    fetchBlocks();
    fetchTypes();
  }, []);

  const fetchBlocks = async () => {
    try {
      const response = await api.get("/blocks");

      setBlocks(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  const fetchTypes = async () => {
    try {
      const response = await api.get("/apartment-types");

      setTypes(response.data);
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
      await api.post("/apartments", {
        number: formData.number,

        block: {
          id: Number(formData.blockId),
        },

        type: {
          id: Number(formData.typeId),
        },
      });

      alert("Apartment Added");

      setFormData({
        number: "",
        blockId: "",
        typeId: "",
      });

      onSuccess();
    } catch (error) {
      console.error(error);

      alert("Failed to add apartment");
    }
  };

  return (
    <form onSubmit={handleSubmit} className="form-panel">
      <h2 className="form-panel__title">Add Apartment</h2>

      <div className="form-grid">
        <input
          type="text"
          name="number"
          placeholder="Apartment Number"
          value={formData.number}
          onChange={handleChange}
          required
          className="form-control"
        />

        <select
          name="blockId"
          value={formData.blockId}
          onChange={handleChange}
          required
          className="form-control"
        >
          <option value="">Select Block</option>

          {blocks.map((block) => (
            <option key={block.id} value={block.id}>
              {block.name}
            </option>
          ))}
        </select>

        <select
          name="typeId"
          value={formData.typeId}
          onChange={handleChange}
          required
          className="form-control"
        >
          <option value="">Select Type</option>

          {types.map((type) => (
            <option key={type.id} value={type.id}>
              {type.name}
            </option>
          ))}
        </select>

        <button type="submit" className="button">
          Add Apartment
        </button>
      </div>
    </form>
  );
}

export default AddApartmentForm;
