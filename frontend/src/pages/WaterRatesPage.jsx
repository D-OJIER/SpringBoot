import { useEffect, useState } from "react";

import MainLayout from "../layouts/MainLayout";
import api from "../api/axios";
import AddWaterRateForm from "../components/AddWaterRateForm";
import TableContainer from "../components/TableContainer";

function WaterRatesPage() {
  const [rates, setRates] = useState([]);

  const [editingRateId, setEditingRateId] = useState(null);
  const [editForm, setEditForm] = useState({});

  const startEdit = (rate) => {
    setEditingRateId(rate.id);

    setEditForm({
      minLitres: rate.minLitres,
      maxLitres: rate.maxLitres,
      ratePerLitre: rate.ratePerLitre,
      effectiveFrom: rate.effectiveFrom,
      effectiveTo: rate.effectiveTo,
    });
  };

  const handleEditChange = (e) => {
    setEditForm({
      ...editForm,
      [e.target.name]: e.target.value,
    });
  };

  const saveEdit = async (rate) => {
    try {
      await api.put(
        `/water-rates/${rate.id}`,

        {
          ...rate,

          minLitres: Number(editForm.minLitres),

          maxLitres: Number(editForm.maxLitres),

          ratePerLitre: Number(editForm.ratePerLitre),

          effectiveFrom: editForm.effectiveFrom,

          effectiveTo: editForm.effectiveTo,
        },
      );

      setEditingRateId(null);

      fetchRates();
    } catch (error) {
      console.error(error);

      alert("Failed to update");
    }
  };

  useEffect(() => {
    fetchRates();
  }, []);

  const fetchRates = async () => {
    try {
      const response = await api.get("/water-rates");

      setRates(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  const deleteRate = async (id) => {
    const confirmed = window.confirm("Delete this rate?");

    if (!confirmed) return;

    try {
      await api.delete(`/water-rates/${id}`);

      fetchRates();
    } catch (error) {
      console.error(error);

      alert("Failed to delete rate");
    }
  };

  return (
    <MainLayout>
      <header className="page-header">
        <h1 className="page-title">Water Rates</h1>
      </header>

      <AddWaterRateForm onSuccess={fetchRates} />

      <TableContainer title="Water Rates">
        <table className="data-table">
          <thead>
            <tr>
              <th>Source</th>

              <th>Range</th>

              <th>Rate</th>

              <th>Effective From</th>

              <th>Actions</th>
            </tr>
          </thead>

          <tbody>
            {rates.map((rate) => (
              <tr key={rate.id}>
                <td>{rate.source.name}</td>

                <td>
                  {editingRateId === rate.id ? (
                    <div>
                      <input
                        name="minLitres"
                        value={editForm.minLitres}
                        onChange={handleEditChange}
                        className="form-control form-control--small"
                      />

                      <input
                        name="maxLitres"
                        value={editForm.maxLitres}
                        onChange={handleEditChange}
                        className="form-control form-control--small"
                      />
                    </div>
                  ) : (
                    <>
                      {rate.minLitres}
                      {" - "}
                      {rate.maxLitres}
                    </>
                  )}
                </td>

                <td>
                  {editingRateId === rate.id ? (
                    <input
                      name="ratePerLitre"
                      value={editForm.ratePerLitre}
                      onChange={handleEditChange}
                      className="form-control form-control--small"
                    />
                  ) : (
                    <>₹{rate.ratePerLitre}</>
                  )}
                </td>

                <td>{rate.effectiveFrom}</td>
                <td>
                  {editingRateId === rate.id ? (
                    <button
                      onClick={() => saveEdit(rate)}
                      className="button button--success"
                    >
                      Save
                    </button>
                  ) : (
                    <>
                      <button
                        onClick={() => startEdit(rate)}
                        className="button"
                      >
                        Edit
                      </button>

                      <button
                        onClick={() => deleteRate(rate.id)}
                        className="button button--danger"
                      >
                        Delete
                      </button>
                    </>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </TableContainer>
    </MainLayout>
  );
}

export default WaterRatesPage;
