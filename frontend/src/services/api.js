import axios from "axios";

// ===============================
// ==== CẤU HÌNH CHUNG ==========
// ===============================
const API_URL = "http://localhost:8080/api"; // Đảm bảo đúng URL của server backend

// Lấy token từ localStorage
const getToken = () => localStorage.getItem("token");

// ✅ Instance cho các request có token
const axiosInstance = axios.create({
  baseURL: API_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// ✅ Instance cho các request không cần token (login, register)
const axiosPublic = axios.create({
  baseURL: API_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Interceptor thêm Authorization header vào mọi request nếu có token
axiosInstance.interceptors.request.use(
  (config) => {
    const token = getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor xử lý các lỗi từ response
axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      const { status } = error.response;
      if (status === 401) {
        console.error("Unauthorized: Token không hợp lệ hoặc đã hết hạn.");
        localStorage.removeItem("token");
        window.location.href = "/auth/login";
      } else if (status === 403) {
        console.error("Forbidden: Không có quyền truy cập.");
        alert("Bạn không có quyền truy cập vào chức năng này!");
      }
    }
    return Promise.reject(error);
  }
);

//
// ===============================
// ==== API LIÊN QUAN TỚI USER ====
// ===============================
//

export const getUsers = async () => {
  const response = await axiosInstance.get("/users/list");
  return response.data;
};

export const getUserById = async (id) => {
  const response = await axiosInstance.get(`/users/${id}`);
  return response.data;
};

export const updateUser = (id, userData) =>
  axiosInstance.put(`/users/${id}`, userData);

export const deleteUser = (id) =>
  axiosInstance.delete(`/users/${id}`);

//
// ===============================
// ==== API LIÊN QUAN TỚI XÁC THỰC ====
// ===============================
//

// ✅ Dùng axiosPublic để tránh lỗi 403
export const register = (user) =>
  axiosPublic.post("/auth/register", user);

export const login = async (user) => {
  const response = await axiosPublic.post("/auth/login", user);
  const token = response.data.token;
  if (token) {
    localStorage.setItem("token", token);
  }
  return response.data;
};

//
// ===============================
// ==== API LIÊN QUAN TỚI BÀI HÁT ====
// ===============================
//

export const getSongs = async () => {
  const response = await axiosInstance.get("/songs");
  return response.data;
};

export const getSongById = async (id) => {
  const response = await axiosInstance.get(`/songs/${id}`);
  return response.data;
};

export const getSongUrl = async (id) => {
  const response = await axiosInstance.get(`/songs/${id}/play`);
  return response.data;
};

export const searchSongs = async (query) => {
  const response = await axiosInstance.get(`/songs/search?query=${encodeURIComponent(query)}`);
  return response.data;
};

export const uploadSong = (formData) =>
  axiosInstance.post("/songs/upload", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });

export const updateSong = (id, data) =>
  axiosInstance.put(`/songs/${id}`, data);

export const deleteSong = (id) =>
  axiosInstance.delete(`/songs/${id}`);

//
// ===============================
// ==== API LIÊN QUAN TỚI YÊU THÍCH ====
// ===============================
//

export const favoriteSong = (id) =>
  axiosInstance.post(`/songs/${id}/favorite`);

export const unfavoriteSong = (id) =>
  axiosInstance.delete(`/songs/${id}/favorite`);

export const getFavoriteSongs = async () => {
  const response = await axiosInstance.get("/songs/favorites");
  return response.data;
};
