import React, { useState } from 'react';

const Login = ({ toggleModal, setUser }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleLogin = async (event) => {
    event.preventDefault(); 
    
    const user = {
      email,
      password,
    };
  
    try {
      const response = await fetch('http://localhost:8080/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(user),
      });
  
      let data;
      if (response.ok) {
        try {
          data = await response.json();
        } catch (error) {
          data = await response.text();
        }
      } else {
        data = await response.text();
      }

      if (response.status === 404) {
        alert("Email non esistente");
        return;
      }

      if (response.status === 500) {
        alert("Email o password errate");
        return
      }
  
      if (response.status === 401) {
        alert(data || 'Credenziali errate');
        return;
      }

      if (response.status === 403) {
        alert("Account non verificato. Controlla la tua email.");
        return;
      }
  
      if (response.ok) {
        alert(data.message || 'Login avvenuto con successo');
        localStorage.setItem('jwt', data.token); 
        localStorage.setItem("userEmail", user.email);
  
        if (setUser) {
          setUser(user);
        } else {
          console.error("Errore: setUser non è definito!");
        }
        toggleModal(); 
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
            required
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
            required
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
