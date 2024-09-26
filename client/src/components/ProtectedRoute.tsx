import { Outlet, Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export const ProtectedRoute = () => {
  const {token, authorities} = useAuth();
  const storedToken = localStorage.getItem('token');

  return token || storedToken ? <Outlet /> : <Navigate to="/bejelentkezes" />;
}
