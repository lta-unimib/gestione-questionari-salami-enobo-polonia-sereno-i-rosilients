import React, { useState } from 'react';

import EmailVerification from '../../components/registrazione/EmailVerification'

const Registration = ({ toggleModal }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showVerification, setShowVerification] = useState(false);

  const handleRegister = () => {
    if (password !== confirmPassword) {
      alert('Le password non corrispondono');
      return;
    }

    const newUser = {
      email: email,
      password: password,
    };

    fetch('http://localhost:8080/utente/registrazione', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(newUser),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error('Errore nella registrazione');
        }
        alert('Registrazione riuscita! Controlla la tua email per la verifica.');
        setShowVerification(true);
        // toggleModal(); // Chiude il modal dopo la registrazione
      })
      .catch((error) => {
        console.error('Errore:', error);
        alert('Si è verificato un errore durante la registrazione. Riprova più tardi.');
      });
  };

  return showVerification ? (
    <EmailVerification email={email} onSuccess={() => toggleModal("login")} />
    ) : (
    <div className="bg-white p-6 rounded-lg w-96">
      <h2 className="text-2xl mb-4">Registrati</h2>
      
      <form onSubmit={handleRegister}>
        <label>Email</label>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className="w-full p-2 mb-3 border border-gray-300 rounded"
        />
        <label>Password</label>
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="w-full p-2 mb-3 border border-gray-300 rounded"
        />
        <label>Conferma Password</label>
        <input
          type="password"
          placeholder="Conferma Password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          className="w-full p-2 mb-3 border border-gray-300 rounded"
        />
        <button
          type='submit'
          className="w-full p-2 bg-blue-500 text-white rounded"
        >
          Registrati
        </button>
      </form>

      <p className="mt-3 text-center">
        Hai già un account?{' '}
        <button className="text-blue-500" onClick={() => toggleModal('login')}>
          Accedi
        </button>
      </p>
    </div>
  );
};

export default Registration;
