import api from './api';

const API_URL = 'api/clubs';

export const getClubs = async () => {
  const response = await api.get(API_URL);
  return response.data;
};

export const getClubById = async (id) => {
  const response = await api.get(`${API_URL}/${id}`);
  return response.data;
};

export const createClub = async (club) => {
  const response = await api.post(API_URL, club);
  return response.data;
};

export const updateClub = async (club) => {
  const response = await api.post(API_URL, club);
  return response.data;
};

export const deleteClub = async (id) => {
  const response = await api.delete(`${API_URL}/${id}`);
  return response.data;
};

export const uploadLogo = async (id, file) => {
  const formData = new FormData();
  formData.append('file', file);
  const response = await api.post(`${API_URL}/${id}/logo`, formData);
  return response.data;
};


export const downloadLogo = async (id) => {
  const response = await api.get(`${API_URL}/${id}/logo`, { responseType: 'blob' });
  return response.data;
};
