import React, { useState, useEffect } from 'react';
import './PatientLogin.css';

const PatientLogin = ({ onLogin }) => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    rememberMe: false
  });

  const [errors, setErrors] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);

  // Validation functions
  const validateEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  const validatePassword = (password) => {
    return password.length >= 8;
  };

  const validateField = (name, value) => {
    switch (name) {
      case 'email':
        if (!value) {
          return 'Email is required';
        }
        if (!validateEmail(value)) {
          return 'Please enter a valid email address';
        }
        return '';
      case 'password':
        if (!value) {
          return 'Password is required';
        }
        if (!validatePassword(value)) {
          return 'Password must be at least 8 characters long';
        }
        return '';
      default:
        return '';
    }
  };

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    const fieldValue = type === 'checkbox' ? checked : value;
    
    setFormData(prev => ({
      ...prev,
      [name]: fieldValue
    }));

    // Real-time validation
    if (value !== undefined) {
      const error = validateField(name, value);
      setErrors(prev => ({
        ...prev,
        [name]: error
      }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validate all fields
    const newErrors = {};
    Object.keys(formData).forEach(key => {
      if (key !== 'rememberMe') {
        const error = validateField(key, formData[key]);
        if (error) {
          newErrors[key] = error;
        }
      }
    });

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setIsLoading(true);
    
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      // Simulate successful login
      setIsSuccess(true);
      
      // Call onLogin callback after success
      setTimeout(() => {
        console.log('Patient login successful:', formData);
        if (onLogin) {
          onLogin();
        }
      }, 1000);
      
    } catch (error) {
      console.error('Login error:', error);
      setErrors({ general: 'Login failed. Please try again.' });
    } finally {
      setIsLoading(false);
    }
  };

  const handleForgotPassword = () => {
    console.log('Forgot password clicked');
    // Add your forgot password logic here
  };

  return (
    <div className="patient-login-container">
      <div className="patient-login-card">
        <div className="patient-login-header">
          <h1>Patient Portal</h1>
          <p>Sign in to access your health records</p>
        </div>

        {isSuccess && (
          <div className="success-message">
            <div className="success-icon">✓</div>
            <p>Login successful! Redirecting...</p>
          </div>
        )}

        {!isSuccess && (
          <form onSubmit={handleSubmit} className="patient-login-form">
            {errors.general && (
              <div className="error-message general-error">
                {errors.general}
              </div>
            )}

            <div className="form-group">
              <label htmlFor="email">Email Address</label>
              <input
                type="email"
                id="email"
                name="email"
                value={formData.email}
                onChange={handleInputChange}
                className={errors.email ? 'error' : ''}
                placeholder="Enter your email"
                disabled={isLoading}
              />
              {errors.email && <span className="error-text">{errors.email}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="password">Password</label>
              <div className="password-input-container">
                <input
                  type={showPassword ? 'text' : 'password'}
                  id="password"
                  name="password"
                  value={formData.password}
                  onChange={handleInputChange}
                  className={errors.password ? 'error' : ''}
                  placeholder="Enter your password"
                  disabled={isLoading}
                />
                <button
                  type="button"
                  className="password-toggle"
                  onClick={() => setShowPassword(!showPassword)}
                  disabled={isLoading}
                >
                  {showPassword ? '👁️' : '👁️‍🗨️'}
                </button>
              </div>
              {errors.password && <span className="error-text">{errors.password}</span>}
            </div>

            <div className="form-options">
              <label className="checkbox-container">
                <input
                  type="checkbox"
                  name="rememberMe"
                  checked={formData.rememberMe}
                  onChange={handleInputChange}
                  disabled={isLoading}
                />
                <span className="checkmark"></span>
                Remember me
              </label>
              <button
                type="button"
                className="forgot-password"
                onClick={handleForgotPassword}
                disabled={isLoading}
              >
                Forgot Password?
              </button>
            </div>

            <button
              type="submit"
              className="login-button"
              disabled={isLoading}
            >
              {isLoading ? (
                <span className="loading-spinner">
                  <div className="spinner"></div>
                  Signing in...
                </span>
              ) : (
                'Sign In'
              )}
            </button>

            <div className="signup-link">
              <p>
                Don't have an account?{' '}
                <a href="#" onClick={(e) => { e.preventDefault(); console.log('Sign up clicked'); }}>
                  Sign up here
                </a>
              </p>
            </div>
          </form>
        )}
      </div>
    </div>
  );
};

export default PatientLogin; 