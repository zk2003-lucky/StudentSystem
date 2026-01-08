import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/', // 代理到后端 API
});

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    // 在发送请求之前做一些处理
    const token = localStorage.getItem('token'); // 从 localStorage 中获取 token
    if (token) {
      config.headers.Authorization = `Bearer ${token}`; // 将 token 添加到请求头
    }
    return config;
  },
  (error) => {
    // 对请求错误做一些处理
    return Promise.reject(error);
  }
);

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    // 对响应数据做一些处理
    return response;
  },
  (error) => {
    // 对响应错误做一些处理
    if (error.response) {
      
      switch (error.response.status) {
        case 401:
          // 未授权，跳转到登录页
          window.location.href = '/login';
          break;
        case 403:
          // 禁止访问，提示用户无权限
          //alert('错误代码：403,您没有权限访问此资源');
          window.location.href = '/login';
          break;
        case 500:
          // 服务器错误，提示用户
          //alert('服务器内部错误，请稍后重试');
          break;
        default:
          // 其他错误
          //alert(`请求失败：${error.response.data}`);
      }
    }
    return Promise.reject(error);
  }
);

export default api

