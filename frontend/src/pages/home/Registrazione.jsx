import React, { useState } from 'react';

const Registration = ({ toggleModal, onRegistrationSuccess}) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

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
          const errorData = await response.json(); // Legge il messaggio di errore dal backend
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
        alert(error.message); // Mostra il messaggio di errore specifico
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
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="w-full p-2 mb-3 border border-gray-300 rounded"
          required
        />
        <input
          type="password"
          placeholder="Conferma Password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          className="w-full p-2 mb-3 border border-gray-300 rounded"
          required
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
    </div>
  );
};

export default Registration;
