import { useEffect, useState } from "react";
import MainLayout from "../layouts/MainLayout";
import TableContainer from "../components/TableContainer";
import api from "../api/axios";
import AddUserForm from "../components/AddUserForm";

function UsersPage() {
  const [users, setUsers] = useState([]);

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const response = await api.get("/users");
      setUsers(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <MainLayout>
      <header className="page-header">
        <h1 className="page-title">User Management</h1>
      </header>

      <AddUserForm onSuccess={fetchUsers} />

      <TableContainer title="Users List">
        <table className="data-table">
          <thead>
            <tr>
              <th>Username</th>
              <th>Role</th>
              <th>Apartment Number</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id}>
                <td>{user.username}</td>
                <td>{user.role}</td>
                <td>{user.apartmentNumber || "N/A"}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </TableContainer>
    </MainLayout>
  );
}

export default UsersPage;
