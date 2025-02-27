import React, { useState } from 'react';
import { register } from '../../services/homeService';

const Registration = ({ toggleModal, onRegistrationSuccess }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);

  
const handleRegister = async () => {
  if (password !== confirmPassword) {
    alert('Le password non corrispondono');
    return;
  }

  setLoading(true);

  const newUser = { email, password };

  try {
    // Usa il metodo register di AuthServices
    const response = await register(newUser.email, newUser.password);

    alert('Registrazione riuscita! Controlla la tua email per la verifica.');
    onRegistrationSuccess(email);
  } catch (error) {
    console.error('Errore:', error);
    alert(error.message);
  } finally {
    setLoading(false);
  }
};

  return (
    <div className="bg-white p-8 rounded-lg shadow-lg w-96 border border-purple-100">
      <h2 className="text-2xl mb-6 font-bold text-center text-purple-900">Crea il tuo account</h2>
      
      <form onSubmit={(e) => { e.preventDefault(); handleRegister(); }} className="space-y-4">
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
            placeholder="Crea una password"
            minLength={8}
            maxLength={20}
            pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,20}$"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full p-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all"
            required
            onInvalid={(e) => e.target.setCustomValidity("La password deve contenere almeno 8 caratteri, una maiuscola, una minuscola e un numero.")}
            onInput={(e) => e.target.setCustomValidity("")} // Rimuove il messaggio quando l'utente corregge
            title="La password deve contenere almeno 8 caratteri, una lettera maiuscola, una minuscola e un numero."
          />
        </div>
        
        <div>
          <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700 mb-1">Conferma Password</label>
          <input
            id="confirmPassword"
            type="password"
            placeholder="Conferma la tua password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            className="w-full p-3 border border-gray-300 rounded-md focus:ring-2 focus:ring-purple-500 focus:border-purple-500 transition-all"
            required
          />
        </div>
        
        <button
          className={`w-full p-3 rounded-md text-white font-medium transition-all ${loading ? 'bg-purple-400' : 'bg-purple-700 hover:bg-purple-800'}`}
          type="submit"
          disabled={loading}
          style={{ backgroundColor: loading ? '#6b46c1' : '#3603CD' }}
        >
          {loading ? (
            <div className="flex items-center justify-center">
              <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              Registrazione in corso...
            </div>
          ) : (
            'Registrati'
          )}
        </button>
      </form>
      
      <div className="mt-6 text-center">
        <p className="text-gray-600">
          Hai già un account?{' '}
          <button 
            className="text-purple-700 font-medium hover:text-purple-900 hover:underline transition-all" 
            onClick={() => toggleModal('login')}
          >
            Accedi qui
          </button>
        </p>
      </div>
    </div>
  );
};

export default Registration;