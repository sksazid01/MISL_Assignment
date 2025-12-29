import api from './api';

const leaveService = {
  getAllLeaves: async (filters = {}) => {
    const params = new URLSearchParams();
    if (filters.status) params.append('status', filters.status);
    if (filters.employeeId) params.append('employeeId', filters.employeeId);
    
    const response = await api.get(`/leaves?${params.toString()}`);
    return response.data;
  },

  getLeaveById: async (id) => {
    const response = await api.get(`/leaves/${id}`);
    return response.data;
  },

  getLeavesByEmployee: async (employeeId) => {
    const response = await api.get(`/leaves/employee/${employeeId}`);
    return response.data;
  },

  getPendingLeaves: async () => {
    const response = await api.get('/leaves/pending');
    return response.data;
  },

  applyForLeave: async (leaveData) => {
    const response = await api.post('/leaves', leaveData);
    return response.data;
  },

  updateLeave: async (id, leaveData) => {
    const response = await api.put(`/leaves/${id}`, leaveData);
    return response.data;
  },

  updateLeaveStatus: async (id, statusData) => {
    const response = await api.patch(`/leaves/${id}/status`, statusData);
    return response.data;
  },

  deleteLeave: async (id) => {
    const response = await api.delete(`/leaves/${id}`);
    return response.data;
  },

  getEmployeeLeaveStats: async (employeeId) => {
    const response = await api.get(`/leaves/stats/employee/${employeeId}`);
    return response.data;
  },
};

export default leaveService;
