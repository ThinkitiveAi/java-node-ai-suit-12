import React, { useState } from 'react';
import './ProviderAvailabilitySet.css';

const ProviderAvailabilitySet = ({ onBack }) => {
  const [selectedProvider, setSelectedProvider] = useState({
    id: 1,
    name: 'Dr. Sarah Johnson',
    specialty: 'Cardiology'
  });
  
  const [availability, setAvailability] = useState({
    monday: { available: true, startTime: '09:00', endTime: '17:00' },
    tuesday: { available: true, startTime: '09:00', endTime: '17:00' },
    wednesday: { available: true, startTime: '09:00', endTime: '17:00' },
    thursday: { available: true, startTime: '09:00', endTime: '17:00' },
    friday: { available: true, startTime: '09:00', endTime: '17:00' },
    saturday: { available: false, startTime: '09:00', endTime: '17:00' },
    sunday: { available: false, startTime: '09:00', endTime: '17:00' }
  });

  const [isLoading, setIsLoading] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);

  const days = [
    { key: 'monday', label: 'Monday' },
    { key: 'tuesday', label: 'Tuesday' },
    { key: 'wednesday', label: 'Wednesday' },
    { key: 'thursday', label: 'Thursday' },
    { key: 'friday', label: 'Friday' },
    { key: 'saturday', label: 'Saturday' },
    { key: 'sunday', label: 'Sunday' }
  ];

  const handleDayToggle = (dayKey) => {
    setAvailability(prev => ({
      ...prev,
      [dayKey]: {
        ...prev[dayKey],
        available: !prev[dayKey].available
      }
    }));
  };

  const handleTimeChange = (dayKey, field, value) => {
    setAvailability(prev => ({
      ...prev,
      [dayKey]: {
        ...prev[dayKey],
        [field]: value
      }
    }));
  };

  const handleSave = async () => {
    setIsLoading(true);
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 2000));
      setIsSuccess(true);
      setTimeout(() => {
        setIsSuccess(false);
        console.log('Availability saved:', availability);
      }, 2000);
    } catch (error) {
      console.error('Error saving availability:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleCopySchedule = (fromDay) => {
    const schedule = availability[fromDay];
    const updatedAvailability = {};
    
    days.forEach(day => {
      if (day.key !== fromDay) {
        updatedAvailability[day.key] = { ...schedule };
      }
    });
    
    setAvailability(prev => ({
      ...prev,
      ...updatedAvailability
    }));
  };

  const handleBackClick = () => {
    if (onBack) {
      onBack();
    }
  };

  return (
    <div className="provider-availability-set">
      <div className="header">
        <div className="provider-info">
          <div className="provider-avatar">
            <div className="avatar-placeholder">
              {selectedProvider.name.split(' ').map(n => n[0]).join('')}
            </div>
          </div>
          <div className="provider-details">
            <h1>{selectedProvider.name}</h1>
            <p>{selectedProvider.specialty}</p>
          </div>
        </div>
        <button className="back-button" onClick={handleBackClick}>
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M19 12H5M12 19L5 12L12 5" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
          Back to Providers
        </button>
      </div>

      <div className="content">
        <div className="schedule-section">
          <div className="section-header">
            <h2>Weekly Schedule</h2>
            <p>Set your availability for each day of the week</p>
          </div>

          <div className="schedule-grid">
            {days.map((day) => (
              <div key={day.key} className={`day-card ${availability[day.key].available ? 'available' : 'unavailable'}`}>
                <div className="day-header">
                  <div className="day-toggle">
                    <input
                      type="checkbox"
                      id={day.key}
                      checked={availability[day.key].available}
                      onChange={() => handleDayToggle(day.key)}
                    />
                    <label htmlFor={day.key} className="day-label">
                      {day.label}
                    </label>
                  </div>
                  {availability[day.key].available && (
                    <button 
                      className="copy-button"
                      onClick={() => handleCopySchedule(day.key)}
                      title="Copy this schedule to other days"
                    >
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M8 4V2C8 1.44772 8.44772 1 9 1H21C21.5523 1 22 1.44772 22 2V14C22 14.5523 21.5523 15 21 15H19M8 4H6C5.44772 4 5 4.44772 5 5V17C5 17.5523 5.44772 18 6 18H18C18.5523 18 19 17.5523 19 17V15M8 4H9C9.55228 4 10 4.44772 10 5V7C10 7.55228 9.55228 8 9 8H8" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                      </svg>
                    </button>
                  )}
                </div>

                {availability[day.key].available && (
                  <div className="time-slots">
                    <div className="time-input-group">
                      <label>Start Time</label>
                      <input
                        type="time"
                        value={availability[day.key].startTime}
                        onChange={(e) => handleTimeChange(day.key, 'startTime', e.target.value)}
                        className="time-input"
                      />
                    </div>
                    <div className="time-separator">to</div>
                    <div className="time-input-group">
                      <label>End Time</label>
                      <input
                        type="time"
                        value={availability[day.key].endTime}
                        onChange={(e) => handleTimeChange(day.key, 'endTime', e.target.value)}
                        className="time-input"
                      />
                    </div>
                  </div>
                )}

                {!availability[day.key].available && (
                  <div className="unavailable-message">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M18.364 18.364A9 9 0 1 1 5.636 5.636a9 9 0 0 1 12.728 12.728zM12 8v4m0 4h.01" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                    </svg>
                    <span>Not Available</span>
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>

        <div className="action-section">
          <div className="action-buttons">
            <button className="cancel-button">
              Cancel
            </button>
            <button 
              className="save-button"
              onClick={handleSave}
              disabled={isLoading}
            >
              {isLoading ? (
                <span className="loading-spinner">
                  <div className="spinner"></div>
                  Saving...
                </span>
              ) : isSuccess ? (
                <span className="success-message">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M20 6L9 17L4 12" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                  </svg>
                  Saved!
                </span>
              ) : (
                'Save Schedule'
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProviderAvailabilitySet; 