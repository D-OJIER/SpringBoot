import { useEffect, useState } from "react";
import api from "../api/axios";

function AddUserForm({ onSuccess }) {
  const [apartments, setApartments] = useState([]);
  const [formData, setFormData] = useState({
    username: "",
    password: "",
    apartmentId: "",
  });

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

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      await api.post("/users", {
        username: formData.username,
        password: formData.password,
        apartmentId: Number(formData.apartmentId),
      });

      alert("Resident User Created Successfully!");

      setFormData({
        username: "",
        password: "",
        apartmentId: "",
      });

      onSuccess();
    } catch (error) {
      console.error(error);
      const errorMsg = error.response?.data?.message ?? "Failed to create user";
      alert(errorMsg);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="form-panel">
      <h2 className="form-panel__title">Create Resident User</h2>

      <div className="form-grid">
        <input
          type="text"
          name="username"
          placeholder="Username"
          value={formData.username}
          onChange={handleChange}
          required
          className="form-control"
        />

        <input
          type="password"
          name="password"
          placeholder="Password"
          value={formData.password}
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
          {apartments.map((apt) => (
            <option key={apt.id} value={apt.id}>
              {apt.number} ({apt.block.name})
            </option>
          ))}
        </select>

        <button type="submit" className="button">
          Create User
        </button>
      </div>
    </form>
  );
}

export default AddUserForm;
