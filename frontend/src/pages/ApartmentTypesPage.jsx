import { useEffect, useState } from "react";

import MainLayout from "../layouts/MainLayout";
import TableContainer from "../components/TableContainer";
import api from "../api/axios";

function ApartmentTypesPage() {
  const [types, setTypes] = useState([]);

  const [formData, setFormData] = useState({
    name: "",
    baseOccupancy: "",
    litresPerPerson: "",
  });

  useEffect(() => {
    fetchTypes();
  }, []);

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
      await api.post("/apartment-types", {
        name: formData.name,

        baseOccupancy: Number(formData.baseOccupancy),

        litresPerPerson: Number(formData.litresPerPerson),
      });

      alert("Apartment Type Added");

      setFormData({
        name: "",
        baseOccupancy: "",
        litresPerPerson: "",
      });

      fetchTypes();
    } catch (error) {
      console.error(error);

      alert("Failed to add type");
    }
  };

  return (
    <MainLayout>
      <header className="page-header">
        <h1 className="page-title">Apartment Types</h1>
      </header>

      <form onSubmit={handleSubmit} className="form-panel">
        <h2 className="form-panel__title">Add Apartment Type</h2>

        <div className="form-grid">
          <input
            type="text"
            name="name"
            placeholder="Type Name"
            value={formData.name}
            onChange={handleChange}
            required
            className="form-control"
          />

          <input
            type="number"
            name="baseOccupancy"
            placeholder="Base Occupancy"
            value={formData.baseOccupancy}
            onChange={handleChange}
            required
            className="form-control"
          />

          <input
            type="number"
            step="0.01"
            name="litresPerPerson"
            placeholder="Litres Per Person"
            value={formData.litresPerPerson}
            onChange={handleChange}
            required
            className="form-control"
          />

          <button type="submit" className="button">
            Add Type
          </button>
        </div>
      </form>

      <TableContainer title="Apartment Types">
        <table className="data-table">
          <thead>
            <tr>
              <th>Name</th>

              <th>Occupancy</th>

              <th>Litres / Person</th>
            </tr>
          </thead>

          <tbody>
            {types.map((type) => (
              <tr key={type.id}>
                <td>{type.name}</td>

                <td>{type.baseOccupancy}</td>

                <td>{type.litresPerPerson}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </TableContainer>
    </MainLayout>
  );
}

export default ApartmentTypesPage;
