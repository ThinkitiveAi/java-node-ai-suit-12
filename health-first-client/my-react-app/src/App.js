import React, { useState } from 'react';
import './App.css';
import ProviderLogin from './components/provider/ProviderLogin';
import PatientLogin from './components/patient/PatientLogin';
import ProviderAvailabilitySelect from './components/provider/ProviderAvailabilitySelect';
import ProviderAvailabilitySet from './components/provider/ProviderAvailabilitySet';
import BookAppointment from './components/patient/BookAppointment';
import ViewAppointmentList from './components/patient/ViewAppointmentList';

function App() {
  const [currentPortal, setCurrentPortal] = useState('provider'); // 'provider' or 'patient'
  const [currentView, setCurrentView] = useState('login'); // 'login', 'availability-select', 'availability-set', 'book-appointment', 'appointment-list'
  const [showBookAppointment, setShowBookAppointment] = useState(false);

  const switchToProvider = () => {
    setCurrentPortal('provider');
    setCurrentView('login');
  };
  
  const switchToPatient = () => {
    setCurrentPortal('patient');
    setCurrentView('login');
  };

  const renderContent = () => {
    if (currentPortal === 'provider') {
      switch (currentView) {
        case 'login':
          return <ProviderLogin onLogin={() => setCurrentView('availability-select')} />;
        case 'availability-select':
          return <ProviderAvailabilitySelect onContinue={() => setCurrentView('availability-set')} />;
        case 'availability-set':
          return <ProviderAvailabilitySet onBack={() => setCurrentView('availability-select')} />;
        default:
          return <ProviderLogin onLogin={() => setCurrentView('availability-select')} />;
      }
    } else {
      switch (currentView) {
        case 'login':
          return <PatientLogin onLogin={() => setCurrentView('appointment-list')} />;
        case 'appointment-list':
          return <ViewAppointmentList />;
        default:
          return <PatientLogin onLogin={() => setCurrentView('appointment-list')} />;
      }
    }
  };

  return (
    <div className="App">
      <div className="portal-selector">
        <button 
          className={`portal-btn ${currentPortal === 'provider' ? 'active' : ''}`}
          onClick={switchToProvider}
        >
          Provider Portal
        </button>
        <button 
          className={`portal-btn ${currentPortal === 'patient' ? 'active' : ''}`}
          onClick={switchToPatient}
        >
          Patient Portal
        </button>
      </div>

      {/* Navigation for Provider Portal */}
      {currentPortal === 'provider' && currentView !== 'login' && (
        <div className="view-navigation">
          <button 
            className={`nav-btn ${currentView === 'availability-select' ? 'active' : ''}`}
            onClick={() => setCurrentView('availability-select')}
          >
            Select Provider
          </button>
          <button 
            className={`nav-btn ${currentView === 'availability-set' ? 'active' : ''}`}
            onClick={() => setCurrentView('availability-set')}
          >
            Set Availability
          </button>
        </div>
      )}

      {/* Navigation for Patient Portal */}
      {currentPortal === 'patient' && currentView !== 'login' && (
        <div className="view-navigation">
          <button 
            className={`nav-btn ${currentView === 'appointment-list' ? 'active' : ''}`}
            onClick={() => setCurrentView('appointment-list')}
          >
            Appointment List
          </button>
          <button 
            className="nav-btn"
            onClick={() => setShowBookAppointment(true)}
          >
            Book Appointment
          </button>
        </div>
      )}
      
      {renderContent()}

      {/* Book Appointment Modal */}
      {showBookAppointment && (
        <BookAppointment onClose={() => setShowBookAppointment(false)} />
      )}
    </div>
  );
}

export default App;
