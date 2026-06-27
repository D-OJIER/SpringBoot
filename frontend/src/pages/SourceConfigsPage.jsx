import { useEffect, useState } from "react";

import MainLayout from "../layouts/MainLayout";
import TableContainer from "../components/TableContainer";
import api from "../api/axios";

function SourceConfigsPage() {
  const [configs, setConfigs] = useState([]);

  const [apartments, setApartments] = useState([]);

  const [sources, setSources] = useState([]);

  const [formData, setFormData] = useState({
    apartmentId: "",
    sourceId: "",
    ratioPercent: "",
  });

  useEffect(() => {
    fetchConfigs();
    fetchApartments();
    fetchSources();
  }, []);

  const fetchConfigs = async () => {
    try {
      const response = await api.get("/apartment-source-configs");

      setConfigs(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  const fetchApartments = async () => {
    try {
      const response = await api.get("/apartments");

      setApartments(response.data);
    } catch (error) {
      console.error(error);
    }
  };

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
      await api.post("/apartment-source-configs", {
        ratioPercent: Number(formData.ratioPercent),

        apartment: {
          id: Number(formData.apartmentId),
        },

        source: {
          id: Number(formData.sourceId),
        },
      });

      setFormData({
        apartmentId: "",
        sourceId: "",
        ratioPercent: "",
      });

      fetchConfigs();
    } catch (error) {
      console.error(error);

      alert("Failed to add config");
    }
  };

  return (
    <MainLayout>
      <header className="page-header">
        <h1 className="page-title">Source Configurations</h1>
      </header>

      <form onSubmit={handleSubmit} className="form-panel">
        <h2 className="form-panel__title">Add Configuration</h2>

        <div className="form-grid">
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

          <input
            type="number"
            name="ratioPercent"
            placeholder="Ratio %"
            value={formData.ratioPercent}
            onChange={handleChange}
            required
            className="form-control"
          />

          <button type="submit" className="button">
            Add Config
          </button>
        </div>
      </form>

      <TableContainer title="Source Configurations">
        <table className="data-table">
          <thead>
            <tr>
              <th>Apartment</th>

              <th>Source</th>

              <th>Ratio %</th>
            </tr>
          </thead>

          <tbody>
            {configs.map((config) => (
              <tr key={config.id}>
                <td>{config.apartment.number}</td>

                <td>{config.source.name}</td>

                <td>{config.ratioPercent}%</td>
              </tr>
            ))}
          </tbody>
        </table>
      </TableContainer>
    </MainLayout>
  );
}

export default SourceConfigsPage;
