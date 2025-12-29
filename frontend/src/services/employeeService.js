import api from './api';

const employeeService = {
  getAllEmployees: async () => {
    const response = await api.get('/employees');
    return response.data;
  },

  getEmployeeById: async (id) => {
    const response = await api.get(`/employees/${id}`);
    return response.data;
  },

  getEmployeesByDepartment: async (department) => {
    const response = await api.get(`/employees/department/${department}`);
    return response.data;
  },

  getActiveEmployees: async () => {
    const response = await api.get('/employees/active');
    return response.data;
  },

  getMyEmployee: async () => {
    const response = await api.get('/employees/me');
    return response.data;
  },

  createEmployee: async (employeeData) => {
    const response = await api.post('/employees', employeeData);
    return response.data;
  },

  updateEmployee: async (id, employeeData) => {
    const response = await api.put(`/employees/${id}`, employeeData);
    return response.data;
  },

  activateEmployee: async (id) => {
    const response = await api.patch(`/employees/${id}/activate`);
    return response.data;
  },

  deactivateEmployee: async (id) => {
    const response = await api.patch(`/employees/${id}/deactivate`);
    return response.data;
  },

  deleteEmployee: async (id) => {
    const response = await api.delete(`/employees/${id}`);
    return response.data;
  },
};

export default employeeService;
