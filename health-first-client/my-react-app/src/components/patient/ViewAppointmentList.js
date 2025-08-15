import React, { useState, useEffect } from 'react';
import './ViewAppointmentList.css';

const ViewAppointmentList = () => {
  const [appointments, setAppointments] = useState([]);
  const [filteredAppointments, setFilteredAppointments] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');
  const [isLoading, setIsLoading] = useState(true);
  const [selectedAppointment, setSelectedAppointment] = useState(null);

  // Mock data for appointments
  const mockAppointments = [
    {
      id: 1,
      patientName: 'John Smith',
      provider: 'Dr. Sarah Johnson',
      date: '2024-01-15',
      time: '10:00 AM',
      type: 'General Consultation',
      status: 'confirmed',
      mode: 'in-person',
      amount: 150
    },
    {
      id: 2,
      patientName: 'Emily Davis',
      provider: 'Dr. Michael Chen',
      date: '2024-01-16',
      time: '2:30 PM',
      type: 'Follow-up Visit',
      status: 'pending',
      mode: 'video',
      amount: 120
    },
    {
      id: 3,
      patientName: 'Robert Wilson',
      provider: 'Dr. Emily Rodriguez',
      date: '2024-01-17',
      time: '9:15 AM',
      type: 'Physical Examination',
      status: 'completed',
      mode: 'in-person',
      amount: 200
    },
    {
      id: 4,
      patientName: 'Lisa Brown',
      provider: 'Dr. David Thompson',
      date: '2024-01-18',
      time: '11:45 AM',
      type: 'Specialist Consultation',
      status: 'cancelled',
      mode: 'home',
      amount: 180
    },
    {
      id: 5,
      patientName: 'Michael Johnson',
      provider: 'Dr. Sarah Johnson',
      date: '2024-01-19',
      time: '3:00 PM',
      type: 'Laboratory Test',
      status: 'confirmed',
      mode: 'in-person',
      amount: 95
    }
  ];

  useEffect(() => {
    // Simulate API call
    setTimeout(() => {
      setAppointments(mockAppointments);
      setFilteredAppointments(mockAppointments);
      setIsLoading(false);
    }, 1000);
  }, []);

  useEffect(() => {
    let filtered = appointments;

    // Filter by search term
    if (searchTerm) {
      filtered = filtered.filter(appointment =>
        appointment.patientName.toLowerCase().includes(searchTerm.toLowerCase()) ||
        appointment.provider.toLowerCase().includes(searchTerm.toLowerCase()) ||
        appointment.type.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    // Filter by status
    if (statusFilter !== 'all') {
      filtered = filtered.filter(appointment => appointment.status === statusFilter);
    }

    setFilteredAppointments(filtered);
  }, [appointments, searchTerm, statusFilter]);

  const getStatusColor = (status) => {
    switch (status) {
      case 'confirmed':
        return '#10B981';
      case 'pending':
        return '#F59E0B';
      case 'completed':
        return '#3B82F6';
      case 'cancelled':
        return '#EF4444';
      default:
        return '#6B7280';
    }
  };

  const getStatusLabel = (status) => {
    switch (status) {
      case 'confirmed':
        return 'Confirmed';
      case 'pending':
        return 'Pending';
      case 'completed':
        return 'Completed';
      case 'cancelled':
        return 'Cancelled';
      default:
        return status;
    }
  };

  const getModeIcon = (mode) => {
    switch (mode) {
      case 'video':
        return (
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M15 10L21 6V18L15 14M3 6H11L15 2H3C1.89543 2 1 2.89543 1 4V20C1 21.1046 1.89543 22 3 22H15C16.1046 22 17 21.1046 17 20V14" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
        );
      case 'home':
        return (
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M3 9L12 2L21 9V20C21 20.5304 20.7893 21.0391 20.4142 21.4142C20.0391 21.7893 19.5304 22 19 22H5C4.46957 22 3.96086 21.7893 3.58579 21.4142C3.21071 21.0391 3 20.5304 3 20V9Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            <path d="M9 22V12H15V22" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
        );
      default:
        return (
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M17 21V19C17 17.9391 16.5786 16.9217 15.8284 16.1716C15.0783 15.4214 14.0609 15 13 15H5C3.93913 15 2.92172 15.4214 2.17157 16.1716C1.42143 16.9217 1 17.9391 1 19V21M23 21V19C22.9993 18.1137 22.7044 17.2528 22.1614 16.5523C21.6184 15.8519 20.8581 15.3516 20 15.13M16 3.13C16.8604 3.35031 17.623 3.85071 18.1676 4.55232C18.7122 5.25392 19.0078 6.11683 19.0078 7.005C19.0078 7.89317 18.7122 8.75608 18.1676 9.45768C17.623 10.1593 16.8604 10.6597 16 10.88M13 7C13 9.20914 11.2091 11 9 11C6.79086 11 5 9.20914 5 7C5 4.79086 6.79086 3 9 3C11.2091 3 13 4.79086 13 7Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
        );
    }
  };

  const handleAppointmentClick = (appointment) => {
    setSelectedAppointment(appointment);
  };

  const handleCloseDetails = () => {
    setSelectedAppointment(null);
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      weekday: 'short',
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  };

  if (isLoading) {
    return (
      <div className="appointment-list-container">
        <div className="loading-container">
          <div className="spinner"></div>
          <p>Loading appointments...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="appointment-list-container">
      <div className="header">
        <div className="header-content">
          <h1>Appointment List</h1>
          <p>Manage and view all patient appointments</p>
        </div>
        <button className="new-appointment-btn">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 5V19M5 12H19" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
          New Appointment
        </button>
      </div>

      <div className="filters-section">
        <div className="search-container">
          <svg className="search-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M21 21L16.514 16.506L21 21ZM19 10.5C19 15.194 15.194 19 10.5 19C5.806 19 2 15.194 2 10.5C2 5.806 5.806 2 10.5 2C15.194 2 19 5.806 19 10.5Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
          <input
            type="text"
            placeholder="Search appointments..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="search-input"
          />
        </div>

        <div className="status-filters">
          <button
            className={`status-filter ${statusFilter === 'all' ? 'active' : ''}`}
            onClick={() => setStatusFilter('all')}
          >
            All
          </button>
          <button
            className={`status-filter ${statusFilter === 'confirmed' ? 'active' : ''}`}
            onClick={() => setStatusFilter('confirmed')}
          >
            Confirmed
          </button>
          <button
            className={`status-filter ${statusFilter === 'pending' ? 'active' : ''}`}
            onClick={() => setStatusFilter('pending')}
          >
            Pending
          </button>
          <button
            className={`status-filter ${statusFilter === 'completed' ? 'active' : ''}`}
            onClick={() => setStatusFilter('completed')}
          >
            Completed
          </button>
          <button
            className={`status-filter ${statusFilter === 'cancelled' ? 'active' : ''}`}
            onClick={() => setStatusFilter('cancelled')}
          >
            Cancelled
          </button>
        </div>
      </div>

      <div className="appointments-list">
        <div className="list-header">
          <h3>Appointments ({filteredAppointments.length})</h3>
        </div>

        {filteredAppointments.length === 0 ? (
          <div className="empty-state">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2" stroke="currentColor" strokeWidth="2"/>
              <line x1="16" y1="2" x2="16" y2="6" stroke="currentColor" strokeWidth="2"/>
              <line x1="8" y1="2" x2="8" y2="6" stroke="currentColor" strokeWidth="2"/>
              <line x1="3" y1="10" x2="21" y2="10" stroke="currentColor" strokeWidth="2"/>
            </svg>
            <h4>No appointments found</h4>
            <p>Try adjusting your search or filters</p>
          </div>
        ) : (
          <div className="appointments-grid">
            {filteredAppointments.map((appointment) => (
              <div
                key={appointment.id}
                className="appointment-card"
                onClick={() => handleAppointmentClick(appointment)}
              >
                <div className="appointment-header">
                  <div className="patient-info">
                    <h4>{appointment.patientName}</h4>
                    <p>{appointment.provider}</p>
                  </div>
                  <div className="appointment-status">
                    <span
                      className="status-badge"
                      style={{ backgroundColor: getStatusColor(appointment.status) }}
                    >
                      {getStatusLabel(appointment.status)}
                    </span>
                  </div>
                </div>

                <div className="appointment-details">
                  <div className="detail-row">
                    <span className="detail-label">Date:</span>
                    <span className="detail-value">{formatDate(appointment.date)}</span>
                  </div>
                  <div className="detail-row">
                    <span className="detail-label">Time:</span>
                    <span className="detail-value">{appointment.time}</span>
                  </div>
                  <div className="detail-row">
                    <span className="detail-label">Type:</span>
                    <span className="detail-value">{appointment.type}</span>
                  </div>
                  <div className="detail-row">
                    <span className="detail-label">Mode:</span>
                    <span className="detail-value mode">
                      {getModeIcon(appointment.mode)}
                      {appointment.mode.charAt(0).toUpperCase() + appointment.mode.slice(1)}
                    </span>
                  </div>
                  <div className="detail-row">
                    <span className="detail-label">Amount:</span>
                    <span className="detail-value amount">${appointment.amount}</span>
                  </div>
                </div>

                <div className="appointment-actions">
                  <button className="action-btn edit">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M11 4H4C3.46957 4 2.96086 4.21071 2.58579 4.58579C2.21071 4.96086 2 5.46957 2 6V20C2 20.5304 2.21071 21.0391 2.58579 21.4142C2.96086 21.7893 3.46957 22 4 22H18C18.5304 22 19.0391 21.7893 19.4142 21.4142C19.7893 21.0391 20 20.5304 20 20V13" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                      <path d="M18.5 2.50023C18.8978 2.10297 19.4374 1.87891 20 1.87891C20.5626 1.87891 21.1022 2.10297 21.5 2.50023C21.8978 2.89749 22.1218 3.43705 22.1218 3.99973C22.1218 4.56241 21.8978 5.10197 21.5 5.49923L12 15.0002L8 16.0002L9 12.0002L18.5 2.50023Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                    </svg>
                    Edit
                  </button>
                  <button className="action-btn cancel">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                    </svg>
                    Cancel
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Appointment Details Modal */}
      {selectedAppointment && (
        <div className="appointment-details-modal">
          <div className="modal-content">
            <div className="modal-header">
              <h3>Appointment Details</h3>
              <button className="close-btn" onClick={handleCloseDetails}>
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                </svg>
              </button>
            </div>
            <div className="modal-body">
              <div className="detail-item">
                <span className="label">Patient:</span>
                <span className="value">{selectedAppointment.patientName}</span>
              </div>
              <div className="detail-item">
                <span className="label">Provider:</span>
                <span className="value">{selectedAppointment.provider}</span>
              </div>
              <div className="detail-item">
                <span className="label">Date:</span>
                <span className="value">{formatDate(selectedAppointment.date)}</span>
              </div>
              <div className="detail-item">
                <span className="label">Time:</span>
                <span className="value">{selectedAppointment.time}</span>
              </div>
              <div className="detail-item">
                <span className="label">Type:</span>
                <span className="value">{selectedAppointment.type}</span>
              </div>
              <div className="detail-item">
                <span className="label">Status:</span>
                <span className="value">
                  <span
                    className="status-badge"
                    style={{ backgroundColor: getStatusColor(selectedAppointment.status) }}
                  >
                    {getStatusLabel(selectedAppointment.status)}
                  </span>
                </span>
              </div>
              <div className="detail-item">
                <span className="label">Mode:</span>
                <span className="value">
                  {getModeIcon(selectedAppointment.mode)}
                  {selectedAppointment.mode.charAt(0).toUpperCase() + selectedAppointment.mode.slice(1)}
                </span>
              </div>
              <div className="detail-item">
                <span className="label">Amount:</span>
                <span className="value">${selectedAppointment.amount}</span>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ViewAppointmentList; 