import React, { useEffect, useState } from "react";
import { getUsers, deleteUser } from "../services/api";
import { useNavigate } from "react-router-dom";
import "./UserList.css";

const UserList = () => {
    const [users, setUsers] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        try {
            const data = await getUsers();
            setUsers(data);
        } catch (error) {
            console.error("Lỗi khi lấy danh sách user:", error);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm("Bạn có chắc chắn muốn xóa user này không?")) {
            try {
                await deleteUser(id);
                fetchUsers(); // Load lại danh sách sau khi xóa
            } catch (error) {
                console.error("Lỗi khi xóa user:", error);
            }
        }
    };

    return (
        <div className="user-list-container">
            <h2>Danh sách tài khoản</h2>
            <table className="user-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Role</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    {users.map((user) => (
                        <tr key={user.id}>
                            <td>{user.id}</td>
                            <td>{user.username}</td>
                            <td>{user.role}</td>
                            <td>
                                <button className="edit-btn" onClick={() => navigate(`/users/edit/${user.id}`)}>
                                    Chỉnh sửa
                                </button>
                                <button className="delete-btn" onClick={() => handleDelete(user.id)}>
                                    Xóa
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default UserList;
