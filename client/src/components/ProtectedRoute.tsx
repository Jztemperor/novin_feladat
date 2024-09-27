import { Outlet, Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

interface ProtectedRouteProps {
  allowedAuthorities: string[];
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  allowedAuthorities,
}) => {
  // Get token and authorities
  const { token, authorities } = useAuth();
  const storedToken = localStorage.getItem("token");

  // Check if user has authority to access the page
  const hasRequiredAuthority = authorities.some((authority) => {
    return allowedAuthorities.includes(authority);
  });

  // Redirect if token is not present
  if (!token && !storedToken) {
    return <Navigate to="/bejelentkezes" />;
  }

  // Redirect based on authority
  return hasRequiredAuthority ? <Outlet /> : <Navigate to="/" />;
};
