import React, { useState } from 'react';
import './BookAppointment.css';

const BookAppointment = ({ onClose }) => {
  const [formData, setFormData] = useState({
    patientName: '',
    appointmentMode: 'in-person',
    provider: '',
    appointmentType: '',
    estimatedAmount: '',
    dateTime: '',
    reason: ''
  });

  const [errors, setErrors] = useState({});
  const [isLoading, setIsLoading] = useState(false);

  // Mock data
  const providers = [
    { id: 1, name: 'Dr. Sarah Johnson', specialty: 'Cardiology' },
    { id: 2, name: 'Dr. Michael Chen', specialty: 'Dermatology' },
    { id: 3, name: 'Dr. Emily Rodriguez', specialty: 'Pediatrics' },
    { id: 4, name: 'Dr. David Thompson', specialty: 'Orthopedics' },
  ];

  const appointmentTypes = [
    'General Consultation',
    'Follow-up Visit',
    'Emergency Visit',
    'Specialist Consultation',
    'Physical Examination',
    'Laboratory Test'
  ];

  const handleInputChange = (field, value) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));

    // Clear error when user starts typing
    if (errors[field]) {
      setErrors(prev => ({
        ...prev,
        [field]: ''
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};

    if (!formData.patientName.trim()) {
      newErrors.patientName = 'Patient name is required';
    }

    if (!formData.provider) {
      newErrors.provider = 'Please select a provider';
    }

    if (!formData.appointmentType) {
      newErrors.appointmentType = 'Please select appointment type';
    }

    if (!formData.estimatedAmount) {
      newErrors.estimatedAmount = 'Estimated amount is required';
    } else if (isNaN(formData.estimatedAmount) || parseFloat(formData.estimatedAmount) <= 0) {
      newErrors.estimatedAmount = 'Please enter a valid amount';
    }

    if (!formData.dateTime) {
      newErrors.dateTime = 'Date and time is required';
    }

    if (!formData.reason.trim()) {
      newErrors.reason = 'Reason for visit is required';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setIsLoading(true);
    
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      console.log('Appointment booked:', formData);
      // Handle success - could show success message or redirect
      alert('Appointment booked successfully!');
      onClose();
      
    } catch (error) {
      console.error('Error booking appointment:', error);
      setErrors({ general: 'Failed to book appointment. Please try again.' });
    } finally {
      setIsLoading(false);
    }
  };

  const handleClose = () => {
    onClose();
  };

  return (
    <div className="book-appointment-overlay">
      <div className="book-appointment-modal">
        <div className="modal-header">
          <h2>Schedule New Appointment</h2>
          <button className="close-button" onClick={handleClose}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </button>
        </div>

        <form onSubmit={handleSubmit} className="appointment-form">
          {errors.general && (
            <div className="error-message general-error">
              {errors.general}
            </div>
          )}

          <div className="form-section">
            <div className="form-row">
              <div className="form-group full-width">
                <label>Patient Name</label>
                <div className="input-container">
                  <input
                    type="text"
                    placeholder="Search & Select Patient"
                    value={formData.patientName}
                    onChange={(e) => handleInputChange('patientName', e.target.value)}
                    className={errors.patientName ? 'error' : ''}
                  />
                  <svg className="dropdown-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M6 9L12 15L18 9" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                  </svg>
                </div>
                {errors.patientName && <span className="error-text">{errors.patientName}</span>}
              </div>
            </div>

            <div className="form-row">
              <div className="form-group full-width">
                <label>Appointment Mode</label>
                <div className="radio-group">
                  <label className="radio-option">
                    <input
                      type="radio"
                      name="appointmentMode"
                      value="in-person"
                      checked={formData.appointmentMode === 'in-person'}
                      onChange={(e) => handleInputChange('appointmentMode', e.target.value)}
                    />
                    <span className="radio-custom"></span>
                    <span className="radio-label">In-Person</span>
                  </label>
                  <label className="radio-option">
                    <input
                      type="radio"
                      name="appointmentMode"
                      value="video"
                      checked={formData.appointmentMode === 'video'}
                      onChange={(e) => handleInputChange('appointmentMode', e.target.value)}
                    />
                    <span className="radio-custom"></span>
                    <span className="radio-label">Video Call</span>
                  </label>
                  <label className="radio-option">
                    <input
                      type="radio"
                      name="appointmentMode"
                      value="home"
                      checked={formData.appointmentMode === 'home'}
                      onChange={(e) => handleInputChange('appointmentMode', e.target.value)}
                    />
                    <span className="radio-custom"></span>
                    <span className="radio-label">Home</span>
                  </label>
                </div>
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Provider</label>
                <div className="input-container">
                  <input
                    type="text"
                    placeholder="Search Provider"
                    value={formData.provider}
                    onChange={(e) => handleInputChange('provider', e.target.value)}
                    className={errors.provider ? 'error' : ''}
                  />
                  <svg className="dropdown-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M6 9L12 15L18 9" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                  </svg>
                </div>
                {errors.provider && <span className="error-text">{errors.provider}</span>}
              </div>

              <div className="form-group">
                <label>Appointment Type</label>
                <div className="input-container">
                  <input
                    type="text"
                    placeholder="Select Type"
                    value={formData.appointmentType}
                    onChange={(e) => handleInputChange('appointmentType', e.target.value)}
                    className={errors.appointmentType ? 'error' : ''}
                  />
                  <svg className="dropdown-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M6 9L12 15L18 9" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                  </svg>
                </div>
                {errors.appointmentType && <span className="error-text">{errors.appointmentType}</span>}
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Estimated Amount ($)</label>
                <div className="input-container">
                  <input
                    type="number"
                    placeholder="Enter Amount"
                    value={formData.estimatedAmount}
                    onChange={(e) => handleInputChange('estimatedAmount', e.target.value)}
                    className={errors.estimatedAmount ? 'error' : ''}
                    min="0"
                    step="0.01"
                  />
                </div>
                {errors.estimatedAmount && <span className="error-text">{errors.estimatedAmount}</span>}
              </div>

              <div className="form-group">
                <label>Date & Time</label>
                <div className="input-container">
                  <svg className="calendar-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2" stroke="currentColor" strokeWidth="2"/>
                    <line x1="16" y1="2" x2="16" y2="6" stroke="currentColor" strokeWidth="2"/>
                    <line x1="8" y1="2" x2="8" y2="6" stroke="currentColor" strokeWidth="2"/>
                    <line x1="3" y1="10" x2="21" y2="10" stroke="currentColor" strokeWidth="2"/>
                  </svg>
                  <input
                    type="datetime-local"
                    value={formData.dateTime}
                    onChange={(e) => handleInputChange('dateTime', e.target.value)}
                    className={errors.dateTime ? 'error' : ''}
                  />
                </div>
                {errors.dateTime && <span className="error-text">{errors.dateTime}</span>}
              </div>
            </div>

            <div className="form-row">
              <div className="form-group full-width">
                <label>Reason for Visit</label>
                <div className="input-container">
                  <textarea
                    placeholder="Enter Reason"
                    value={formData.reason}
                    onChange={(e) => handleInputChange('reason', e.target.value)}
                    className={errors.reason ? 'error' : ''}
                    rows="4"
                  />
                </div>
                {errors.reason && <span className="error-text">{errors.reason}</span>}
              </div>
            </div>
          </div>

          <div className="form-actions">
            <button
              type="submit"
              className="save-button"
              disabled={isLoading}
            >
              {isLoading ? (
                <span className="loading-spinner">
                  <div className="spinner"></div>
                  Booking...
                </span>
              ) : (
                'Save & Close'
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default BookAppointment; 