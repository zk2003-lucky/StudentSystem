import api from './api';

const API_URL = 'api/members';

export const getMembers = async () => {
  const response = await api.get(API_URL);
  return response.data;
};

export const getMemberById = async (id) => {
  const response = await api.get(`${API_URL}/${id}`);
  return response.data;
};

export const createMember = async (member) => {
  const response = await api.post(API_URL, member);
  return response.data;
};

export const updateMember = async (member) => {
  const response = await api.post(API_URL, member);
  return response.data;
};

export const deleteMember = async (id) => {
  const response = await api.delete(`${API_URL}/${id}`);
  return response.data;
};
