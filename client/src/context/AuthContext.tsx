import { createContext, useContext, useState, useEffect } from 'react';

interface AuthContextType {
  token: string | null;
  username: string | null;
  authorities: string[];
  setAuth: (token: string, username: string, authorities: string[]) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [token, setToken] = useState<string | null>(null);
  const [username, setUsername] = useState<string | null>(null);
  const [authorities, setAuthorities] = useState<string[]>([]);

  // Set states
  useEffect(() => {
    const storedToken = localStorage.getItem('token');
    const storedUsername = localStorage.getItem('username');
    const storedAuthorities = JSON.parse(localStorage.getItem('authorities') || '[]');
    if (storedToken) {
      setToken(storedToken);
    }
    if (storedUsername) {
      setUsername(storedUsername);
    }
    if (storedAuthorities) {
      setAuthorities(storedAuthorities);
    }
  }, []);

  // Set in localstorage and state
  const setAuth = (token: string, username: string, authorities: string[]) => {
    localStorage.setItem('token', token);
    localStorage.setItem('username', username);
    localStorage.setItem('authorities', JSON.stringify(authorities));
    setToken(token);
    setUsername(username);
    setAuthorities(authorities);
  };

  // Clear localstorage and state
  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('authorities');
    setToken(null);
    setUsername(null);
    setAuthorities([]);
  };

  return (
    <AuthContext.Provider value={{ token, username, authorities, setAuth, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};
