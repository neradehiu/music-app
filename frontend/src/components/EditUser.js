import React, { useState, useEffect, useCallback } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getUserById, updateUser } from "../services/api";
import "./EditUser.css";

const EditUser = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [user, setUser] = useState({ username: "" });

    const fetchUser = useCallback(async () => {
        try {
            const data = await getUserById(id);
            setUser({ username: data.username }); // Chỉ lấy username, bỏ role
        } catch (error) {
            console.error("Lỗi khi lấy thông tin tài khoản:", error);
        }
    }, [id]);

    useEffect(() => {
        fetchUser();
    }, [fetchUser]);

    const handleChange = (e) => {
        setUser({ ...user, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await updateUser(id, user); // Không gửi role lên backend
            navigate("/users");
        } catch (error) {
            console.error("Lỗi khi cập nhật tài khoản:", error);
        }
    };

    return (
        <div className="edit-user-container">
            <h2>Chỉnh sửa tài khoản</h2>
            <form onSubmit={handleSubmit}>
                <label>Username:</label>
                <input type="text" name="username" value={user.username} onChange={handleChange} required />

                <button type="submit">Lưu</button>
            </form>
        </div>
    );
};

export default EditUser;
