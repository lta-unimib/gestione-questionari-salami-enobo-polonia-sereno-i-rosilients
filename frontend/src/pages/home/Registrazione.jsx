import React, { useState } from 'react';

const Registration = ({ toggleModal, onRegistrationSuccess }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showForgotPasswordModal, setShowForgotPasswordModal] = useState(false);
  const [forgotPasswordEmail, setForgotPasswordEmail] = useState('');
  const [isLoading, setIsLoading] = useState(false); 
  const [errorMessage, setErrorMessage] = useState(''); 

  const handleRegister = () => {
    if (password !== confirmPassword) {
      alert('Le password non corrispondono');
      return;
    }

    const newUser = { email, password };

    fetch('http://localhost:8080/auth/signup', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(newUser),
    })
      .then(async (response) => {
        if (!response.ok) {
          const errorData = await response.json();
          if (response.status === 400 && errorData.message === "Email già registrata") {
            throw new Error("Email già registrata. Prova ad accedere o usa un'altra email.");
          }
          throw new Error(errorData.message || 'Errore nella registrazione');
        }
        alert('Registrazione riuscita! Controlla la tua email per la verifica.');
        onRegistrationSuccess(email);
      })
      .catch((error) => {
        console.error('Errore:', error);
        alert(error.message);
      });
  };

  const handleForgotPassword = () => {
    setIsLoading(true); 
    setErrorMessage(''); 
  
    fetch('http://localhost:8080/auth/forgot-password', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: forgotPasswordEmail }),
    })
      .then(async (response) => {
        if (!response.ok) {
          const errorData = await response.json();
          // Gestisci messaggi di errore specifici
          if (errorData.message === "Utente non trovato") {
            throw new Error("Email non trovata. Verifica l'email inserita.");
          }
          throw new Error(errorData.message || 'Errore nella richiesta di reset della password');
        }
        alert('Email di reset inviata con successo. Controlla la tua casella di posta.');
        setShowForgotPasswordModal(false); 
      })
      .catch((error) => {
        console.error('Errore:', error);
        setErrorMessage(error.message); 
      })
      .finally(() => {
        setIsLoading(false); 
      });
  };

  return (
    <div className="bg-white p-6 rounded-lg w-96">
      <h2 className="text-2xl mb-4">Registrati</h2>

      <form onSubmit={(e) => { e.preventDefault(); handleRegister(); }}>
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
        <input
          type="password"
          placeholder="Conferma Password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          className="w-full p-2 mb-3 border border-gray-300 rounded"
        />
        <button
          className="w-full p-2 bg-blue-500 text-white rounded"
          type='submit'
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

      <p className="mt-3 text-center">
        <button
          className="text-blue-500"
          onClick={() => setShowForgotPasswordModal(true)}
        >
          Password dimenticata?
        </button>
      </p>

      {/* Modal per "Password dimenticata" */}
      {showForgotPasswordModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
          <div className="bg-white p-6 rounded-lg w-96">
            <h2 className="text-2xl mb-4">Password dimenticata</h2>
            <input
              type="email"
              placeholder="Inserisci la tua email"
              value={forgotPasswordEmail}
              onChange={(e) => setForgotPasswordEmail(e.target.value)}
              className="w-full p-2 mb-3 border border-gray-300 rounded"
              disabled={isLoading} 
            />
            {errorMessage && ( 
              <p className="text-red-500 mb-3">{errorMessage}</p>
            )}
            <button
              className="w-full p-2 bg-blue-500 text-white rounded flex items-center justify-center"
              onClick={handleForgotPassword}
              disabled={isLoading} 
            >
              {isLoading ? (
                <>
                  <svg
                    className="animate-spin h-5 w-5 mr-3 text-white"
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                  >
                    <circle
                      className="opacity-25"
                      cx="12"
                      cy="12"
                      r="10"
                      stroke="currentColor"
                      strokeWidth="4"
                    ></circle>
                    <path
                      className="opacity-75"
                      fill="currentColor"
                      d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                    ></path>
                  </svg>
                  Invio in corso...
                </>
              ) : (
                'Invia richiesta'
              )}
            </button>
            <button
              className="w-full p-2 mt-2 bg-gray-300 text-gray-700 rounded"
              onClick={() => setShowForgotPasswordModal(false)}
              disabled={isLoading} 
            >
              Annulla
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Registration;