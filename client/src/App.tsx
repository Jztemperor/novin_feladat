import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import { Home } from './pages/Home';
import { Register } from './pages/Register';
import 'react-toastify/dist/ReactToastify.css';
import { Login } from './pages/Login';
import { AuthProvider } from './context/AuthContext';
import {ProtectedRoute} from './components/ProtectedRoute';
import { NavBar } from './components/NavBar';

function App() {

  return (
    <>
      <AuthProvider>
        <NavBar>
        <Router>
        <Routes>
          <Route element={<ProtectedRoute/>}>
          {""}
          <Route path='/' element={<Home/>}></Route>
          </Route>
          <Route path="/regisztracio" element={<Register />} />
          <Route path="/bejelentkezes" element={<Login />} />
        </Routes>
      </Router>
        </NavBar>
      <ToastContainer />
    </AuthProvider>
    </>
  )
}

export default App
