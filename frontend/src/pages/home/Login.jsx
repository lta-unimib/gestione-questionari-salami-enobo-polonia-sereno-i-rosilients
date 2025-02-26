import React, { useState } from 'react';

const Login = ({ toggleModal, setUser }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const handleLogin = async (event) => {
    event.preventDefault();
    setLoading(true);
    
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
        return;
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
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-white p-8 rounded-lg shadow-lg w-96 border border-purple-100">
      <h2 className="text-2xl mb-6 font-bold text-center text-purple-900">Accedi al tuo account</h2>

      <form onSubmit={handleLogin} className="space-y-4">
        <div>
          <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">Email</label>
          <input
            id="email"
            type="email"
            placeholder="La tua email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full p-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all"
            required
          />
        </div>

        <div>
          <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">Password</label>
          <input
            id="password"
            type="password"
            placeholder="La tua password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full p-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all"
            required
          />
        </div>

        <div className="text-right">
          <button 
            type="button" 
            className="text-sm text-purple-700 hover:text-purple-900 hover:underline transition-all"
            onClick={() => alert("Funzionalità di recupero password non ancora implementata")}
          >
            Password dimenticata?
          </button>
        </div>

        <button
          type="submit"
          className={`w-full p-3 rounded-md text-white font-medium transition-all ${loading ? 'bg-purple-400' : 'bg-purple-700 hover:bg-purple-800'}`}
          disabled={loading}
          style={{ backgroundColor: loading ? '#6b46c1' : '#3603CD' }}
        >
          {loading ? (
            <div className="flex items-center justify-center">
              <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              Autenticazione in corso...
            </div>
          ) : (
            'Accedi'
          )}
        </button>
      </form>

      <div className="mt-6 text-center">
        <p className="text-gray-600">
          Non hai un account?{' '}
          <button 
            className="text-purple-700 font-medium hover:text-purple-900 hover:underline transition-all" 
            onClick={() => toggleModal('register')}
          >
            Registrati qui
          </button>
        </p>
      </div>
    </div>
  );
};

export default Login;