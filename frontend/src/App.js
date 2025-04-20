import React, { useState, useEffect } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./components/Login";
import Register from "./components/Register";
import Dashboard from "./components/Dashboard";
import UserList from "./components/UserList";
import EditUser from "./components/EditUser";
import MusicList from "./components/MusicList";
import AddMusic from "./components/AddMusic";
import Favorites from "./components/Favorites";
import Lyrics from "./components/Lyrics";

const PrivateRoute = ({ element, requiredRole, token, role }) => {
    if (!token) {
        return <Navigate to="/auth/login" />;
    }

    if (requiredRole && role !== requiredRole) {
        return <h2 style={{ color: "red", textAlign: "center" }}>Chỉ Admin mới có quyền truy cập!</h2>;
    }

    return element;
};

function App() {
    const [token, setToken] = useState(localStorage.getItem("token"));
    const [role, setRole] = useState(localStorage.getItem("role"));

    useEffect(() => {
        const handleStorageChange = () => {
            setToken(localStorage.getItem("token"));
            setRole(localStorage.getItem("role"));
        };

        window.addEventListener("storage", handleStorageChange);
        return () => window.removeEventListener("storage", handleStorageChange);
    }, []);

    return (
        <Router>
            <Routes>
                <Route path="/auth/login" element={<Login setToken={setToken} setRole={setRole} />} />
                <Route path="/auth/register" element={<Register />} />
                <Route path="/dashboard" element={<PrivateRoute element={<Dashboard />} token={token} role={role} />} />
        
                
                <Route path="/users" element={<PrivateRoute element={<UserList />} token={token} role={role} />} />
                <Route path="/users/edit/:id" element={<PrivateRoute element={<EditUser />} token={token} role={role} />} />
                <Route path="/musiclist" element={<PrivateRoute element={<MusicList />} token={token} role={role} />} />
                <Route path="/addmusic" element={<PrivateRoute element={<AddMusic />} token={token} role={role} />} />
                <Route path="/favorites" element={<PrivateRoute element={<Favorites />} token={token} role={role} />} />
                <Route path="/lyrics/:id" element={<PrivateRoute element={<Lyrics />} token={token} role={role} />} />
                <Route path="/" element={<Navigate to={token ? "/dashboard" : "/auth/login"} />} />
            </Routes>
        </Router>
    );
}

export default App;
