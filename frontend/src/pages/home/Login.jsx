import React, { useState } from 'react';

const Login = ({ toggleModal }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleLogin = async (event) => {
    event.preventDefault(); // Evita il comportamento di submit del form
    console.log('Login in corso...');  // Testa se la funzione viene chiamata
  
    const user = {
      email,
      password,
    };
  
    try {
      const response = await fetch('http://localhost:8080/utente/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(user),
      });
  
      const data = await response.json();
  
      if (response.status === 401) {
        alert(data.message || 'Credenziali errate');
        return;
      }
  
      if (response.ok) {
        alert(data.message); // Login riuscito
        console.log('Login avvenuto con successo:', data); // Qui stampiamo il messaggio del backend
        toggleModal(); // Chiude il modal dopo il login
      }
    } catch (error) {
      console.error('Errore:', error);
      alert('Si è verificato un errore durante il login.');
    }
  };
  

  return (
    <div className="bg-white p-6 rounded-lg w-96">
      <h2 className="text-2xl mb-4">Login</h2>

      <form onSubmit={handleLogin}>
        <div className="form-group">
          <label>Email</label>
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full p-2 mb-3 border border-gray-300 rounded"
          />
        </div>

        <div className="form-group">
          <label>Password</label>
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full p-2 mb-3 border border-gray-300 rounded"
          />
        </div>

        <button
          type="submit"
          className="w-full p-2 bg-blue-500 text-white rounded"
        >
          Accedi
        </button>
      </form>

      <p className="mt-3 text-center">
        Non hai un account?{' '}
        <button
          className="text-blue-500"
          onClick={() => toggleModal('register')}
        >
          Registrati
        </button>
      </p>
    </div>
  );
};

export default Login;
