import api from './api';
import axios from "axios";

const API_URL = '/api/auth';

export const login = async (user) => {
  const response = await api.post(`${API_URL}/login`, user);
  return response.data;
};
export const register = async (userData) => {
  try {
    const response = await axios.post(`${API_URL}/register`, {
      username: userData.username,
      password: userData.password,
      confirmPassword: userData.confirmPassword
    });

    // 成功响应处理
    if (response.data.status === 200) {
      return response.data;
    }

    // 处理业务逻辑错误
     new Error(response.data.message || '注册失败');

  } catch (error) {
    // 统一错误处理
    let errorMessage = '注册失败';

    if (error.response) {
      // 从后端响应中获取详细信息
      errorMessage = error.response.data?.message || `服务器错误 (${error.response.status})`;
    } else if (error.request) {
      errorMessage = '无法连接到服务器';
    }

    throw new Error(errorMessage);
  }
};
// api.interceptors.request.use(config => {
//   config.headers['X-Requested-With'] = 'XMLHttpRequest';
//   return config;
// });
// api.interceptors.response.use(
//     response => response,
//     error => {
//       if (!error.response) {
//         return Promise.reject(new Error('网络连接异常，请检查服务器状态'));
//       }
//       return Promise.reject(error);
//     }
// );
// export const register = async (user) => {
//   const response =  await api.post(`${API_URL}/register`, user);
//   return response.data;
// };
// export const register = async (user) => {
//   try {
//     const response = await axios.post(
//          `${API_URL}/register`,
//         {
//           username: user.username,
//           password: user.password,
//           confirmPassword: user.confirmPassword
//         }
//     );
//
//     // 成功响应处理
//     if (response.data.status === 200) {
//       return response.data;
//     }
//
//     // 自定义错误抛出
//     throw new Error(response.data.message || '注册失败');
//
//   } catch (error) {
//     // 统一错误处理
//     if (error.response) {
//       // 后端返回的错误
//       const serverMsg = error.response.data?.message;
//       throw new Error(serverMsg || `请求失败: ${error.response.status}`);
//     } else if (error.request) {
//       // 请求未收到响应
//       throw new Error('网络连接异常，请检查服务器状态');
//     } else {
//       // 前端代码错误
//       throw new Error('请求配置错误: ' + error.message);
//     }
//   }
// };

