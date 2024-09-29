import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { ToastContainer } from "react-toastify";
import { Home } from "./pages/Home";
import { Register } from "./pages/Register";
import "react-toastify/dist/ReactToastify.css";
import { Login } from "./pages/Login";
import { AuthProvider } from "./context/AuthContext";
import { ProtectedRoute } from "./components/ProtectedRoute";
import { NavBar } from "./components/NavBar";
import { CreateInvoice } from "./pages/CreateInvoice";
import { Invoices } from "./pages/Invoices";
import { Invoice } from "./pages/Invoice";
import { Admin } from "./pages/Admin";

function App() {
  return (
    <>
      <AuthProvider>
        <Router>
          <NavBar />
          <Routes>
            <Route
              element={
                <ProtectedRoute
                  allowedAuthorities={[
                    "Konyvelo",
                    "Adminisztrator",
                    "Felhasznalo",
                  ]}
                />
              }
            >
              {""}
              <Route path="/" element={<Home />}></Route>
            </Route>
            <Route path="/regisztracio" element={<Register />} />
            <Route path="/bejelentkezes" element={<Login />} />
            <Route
              element={
                <ProtectedRoute
                  allowedAuthorities={["Konyvelo", "Adminisztrator"]}
                />
              }
            >
              {""}
              <Route
                path="/szamla-letrehoz"
                element={<CreateInvoice />}
              ></Route>
            </Route>

            <Route
              element={
                <ProtectedRoute
                  allowedAuthorities={[
                    "Konyvelo",
                    "Adminisztrator",
                    "Felhasznalo",
                  ]}
                />
              }
            >
              {""}
              <Route path="/szamlak" element={<Invoices />}></Route>
            </Route>

            <Route
              element={
                <ProtectedRoute
                  allowedAuthorities={[
                    "Konyvelo",
                    "Adminisztrator",
                    "Felhasznalo",
                  ]}
                />
              }
            >
              {""}
              <Route path="/szamla/:invoiceId" element={<Invoice />}></Route>
            </Route>

            <Route
              element={
                <ProtectedRoute allowedAuthorities={["Adminisztrator"]} />
              }
            >
              {""}
              <Route path="/admin/" element={<Admin />}></Route>
            </Route>
          </Routes>
        </Router>
        <ToastContainer />
      </AuthProvider>
    </>
  );
}

export default App;
