import React, { useState } from 'react';

const Login = ({ toggleModal }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleLogin = () => {
    const user = {
      email: email,
      password: password,
    };

    fetch('http://localhost:8080/utente/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(user),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error('Errore nel login');
        }
        alert('Login effettuato con successo!');
        toggleModal(); // Chiude il modal dopo il login
      })
      .catch((error) => {
        console.error('Errore:', error);
        alert('Credenziali errate o errore nel server.');
      });
  };

  return (
    <div className="bg-white p-6 rounded-lg w-96">
      <h2 className="text-2xl mb-4">Login</h2>

      <input
        type="email"
        placeholder="Email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        className="w-full p-2 mb-3 border border-gray-300 rounded"
      />
      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        className="w-full p-2 mb-3 border border-gray-300 rounded"
      />
      <button
        className="w-full p-2 bg-blue-500 text-white rounded"
        onClick={handleLogin}
      >
        Accedi
      </button>

      <p className="mt-3 text-center">
        Non hai un account?{' '}
        <button className="text-blue-500" onClick={() => toggleModal('register')}>
          Registrati
        </button>
      </p>
    </div>
  );
};

export default Login;
