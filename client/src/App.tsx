import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import { Home } from './pages/Home';
import { Register } from './pages/Register';
import 'react-toastify/dist/ReactToastify.css';
import { Login } from './pages/Login';
import { AuthProvider } from './context/AuthContext';

function App() {

  return (
    <>
      <AuthProvider>
      <Router>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/regisztracio" element={<Register />} />
          <Route path="/bejelentkezes" element={<Login />} />
        </Routes>
      </Router>
      <ToastContainer />
    </AuthProvider>
    </>
  )
}

export default App
