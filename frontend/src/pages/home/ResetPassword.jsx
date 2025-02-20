import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

const ResetPassword = () => {
  const { token } = useParams(); 
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [message, setMessage] = useState('');
  const navigate = useNavigate();

  const handleResetPassword = () => {
    if (newPassword !== confirmPassword) {
      setMessage('Le password non corrispondono');
      return;
    }
  
    fetch('http://localhost:8080/auth/reset-password', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ token, newPassword }), 
    })
      .then(async (response) => {
        if (!response.ok) {
          const errorData = await response.json();
          throw new Error(errorData.message || 'Errore nel reset della password');
        }
        setMessage('Password resettata con successo! Chiudi la pagina ed effettua il login con le tue nuove credenziali.'); 
      })
      .catch((error) => {
        setMessage(error.message);
      });
  };

  return (
    <div className="bg-white p-6 rounded-lg w-96 mx-auto mt-8">
      <h2 className="text-2xl mb-4">Reset della Password</h2>
      <input
        type="password"
        placeholder="Nuova Password"
        value={newPassword}
        onChange={(e) => setNewPassword(e.target.value)}
        className="w-full p-2 mb-3 border border-gray-300 rounded"
      />
      <input
        type="password"
        placeholder="Conferma Password"
        value={confirmPassword}
        onChange={(e) => setConfirmPassword(e.target.value)}
        className="w-full p-2 mb-3 border border-gray-300 rounded"
      />
      <button
        className="w-full p-2 bg-blue-500 text-white rounded"
        onClick={handleResetPassword}
      >
        Resetta Password
      </button>
      {message && <p className="mt-3 text-center">{message}</p>}
    </div>
  );
};

export default ResetPassword;