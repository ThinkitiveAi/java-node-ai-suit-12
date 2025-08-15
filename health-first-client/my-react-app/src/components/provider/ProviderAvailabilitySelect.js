import React, { useState } from 'react';
import './ProviderAvailabilitySelect.css';

const ProviderAvailabilitySelect = ({ onContinue }) => {
  const [selectedProvider, setSelectedProvider] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  // Mock data for providers
  const providers = [
    { id: 1, name: 'Dr. Sarah Johnson', specialty: 'Cardiology', availability: 'Available' },
    { id: 2, name: 'Dr. Michael Chen', specialty: 'Dermatology', availability: 'Available' },
    { id: 3, name: 'Dr. Emily Rodriguez', specialty: 'Pediatrics', availability: 'Busy' },
    { id: 4, name: 'Dr. David Thompson', specialty: 'Orthopedics', availability: 'Available' },
    { id: 5, name: 'Dr. Lisa Wang', specialty: 'Neurology', availability: 'Off Duty' },
  ];

  const filteredProviders = providers.filter(provider =>
    provider.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    provider.specialty.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleProviderSelect = (provider) => {
    setSelectedProvider(provider);
  };

  const handleContinue = () => {
    if (selectedProvider) {
      setIsLoading(true);
      // Simulate API call
      setTimeout(() => {
        setIsLoading(false);
        console.log('Selected provider:', selectedProvider);
        // Call onContinue callback
        if (onContinue) {
          onContinue(selectedProvider);
        }
      }, 1000);
    }
  };

  const getAvailabilityColor = (availability) => {
    switch (availability) {
      case 'Available':
        return '#27ae60';
      case 'Busy':
        return '#f39c12';
      case 'Off Duty':
        return '#e74c3c';
      default:
        return '#95a5a6';
    }
  };

  return (
    <div className="provider-availability-select">
      <div className="header">
        <h1>Provider Availability</h1>
        <p>Select a provider to manage their availability</p>
      </div>

      <div className="search-section">
        <div className="search-container">
          <div className="search-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M21 21L16.514 16.506L21 21ZM19 10.5C19 15.194 15.194 19 10.5 19C5.806 19 2 15.194 2 10.5C2 5.806 5.806 2 10.5 2C15.194 2 19 5.806 19 10.5Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          </div>
          <input
            type="text"
            placeholder="Search by provider name or specialty"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="search-input"
          />
        </div>
      </div>

      <div className="providers-list">
        <div className="list-header">
          <h3>Available Providers</h3>
          <span className="provider-count">{filteredProviders.length} providers</span>
        </div>

        <div className="providers-grid">
          {filteredProviders.map((provider) => (
            <div
              key={provider.id}
              className={`provider-card ${selectedProvider?.id === provider.id ? 'selected' : ''}`}
              onClick={() => handleProviderSelect(provider)}
            >
              <div className="provider-avatar">
                <div className="avatar-placeholder">
                  {provider.name.split(' ').map(n => n[0]).join('')}
                </div>
              </div>
              
              <div className="provider-info">
                <h4 className="provider-name">{provider.name}</h4>
                <p className="provider-specialty">{provider.specialty}</p>
                <div className="availability-status">
                  <span 
                    className="status-dot"
                    style={{ backgroundColor: getAvailabilityColor(provider.availability) }}
                  ></span>
                  <span className="status-text">{provider.availability}</span>
                </div>
              </div>

              <div className="select-indicator">
                {selectedProvider?.id === provider.id && (
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="#233853" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                  </svg>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>

      <div className="action-buttons">
        <button 
          className="continue-button"
          disabled={!selectedProvider || isLoading}
          onClick={handleContinue}
        >
          {isLoading ? (
            <span className="loading-spinner">
              <div className="spinner"></div>
              Loading...
            </span>
          ) : (
            'Continue'
          )}
        </button>
      </div>
    </div>
  );
};

export default ProviderAvailabilitySelect; 