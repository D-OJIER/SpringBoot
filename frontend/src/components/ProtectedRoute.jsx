import { Navigate } from "react-router-dom";

function isTokenExpired(token) {
  try {
    const payload = token.split(".")[1];
    if (!payload) return true;
    let base64 = payload.replace(/-/g, "+").replace(/_/g, "/");
    while (base64.length % 4) base64 += "=";
    const json = atob(base64);
    const data = JSON.parse(json);
    if (!data.exp) return true;
    return data.exp * 1000 < Date.now();
  } catch (e) {
    return true;
  }
}

function ProtectedRoute({ children }) {
  const token = localStorage.getItem("token");

  if (!token) {
    return <Navigate to="/login" />;
  }

  if (isTokenExpired(token)) {
    localStorage.removeItem("token");
    return <Navigate to="/login" />;
  }

  return children;
}

export default ProtectedRoute;
