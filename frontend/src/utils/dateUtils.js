/**
 * Utility functions for date formatting
 */

/**
 * Format a date string to localized date string
 * Handles null/undefined values and invalid dates gracefully
 * @param {string|Date} dateValue - The date value to format
 * @param {string} defaultValue - Default value to return if date is invalid
 * @returns {string} Formatted date string or default value
 */
export const formatDate = (dateValue, defaultValue = 'N/A') => {
  if (!dateValue) return defaultValue;
  
  try {
    const date = new Date(dateValue);
    if (isNaN(date.getTime())) {
      console.error('Invalid date:', dateValue);
      return defaultValue;
    }
    return date.toLocaleDateString();
  } catch (error) {
    console.error('Error formatting date:', dateValue, error);
    return defaultValue;
  }
};

/**
 * Format a date string to localized date and time string
 * @param {string|Date} dateValue - The date value to format
 * @param {string} defaultValue - Default value to return if date is invalid
 * @returns {string} Formatted date-time string or default value
 */
export const formatDateTime = (dateValue, defaultValue = 'N/A') => {
  if (!dateValue) return defaultValue;
  
  try {
    const date = new Date(dateValue);
    if (isNaN(date.getTime())) {
      console.error('Invalid date:', dateValue);
      return defaultValue;
    }
    return date.toLocaleString();
  } catch (error) {
    console.error('Error formatting date-time:', dateValue, error);
    return defaultValue;
  }
};

/**
 * Format date for input fields (YYYY-MM-DD)
 * @param {string|Date} dateValue - The date value to format
 * @returns {string} Formatted date string in YYYY-MM-DD format
 */
export const formatInputDate = (dateValue) => {
  if (!dateValue) return '';
  
  try {
    const date = new Date(dateValue);
    if (isNaN(date.getTime())) return '';
    
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  } catch (error) {
    console.error('Error formatting input date:', dateValue, error);
    return '';
  }
};
