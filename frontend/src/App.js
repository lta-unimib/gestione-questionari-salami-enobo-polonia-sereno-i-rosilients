import React, { useState, useEffect } from 'react';
import Modal from 'react-modal';
import './App.css';

const App = () => {
  const [isModalOpen, setIsModalOpen] = useState(false); // Stato per aprire/chiudere il popup
  const [isLogin, setIsLogin] = useState(true); // Stato per determinare se è il login o la registrazione
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState(''); // Per confermare la password
  const [verificationCode, setVerificationCode] = useState(''); // Codice di verifica per email

  useEffect(() => {
    // Imposta l'elemento app principale per il Modal
    Modal.setAppElement('#root');
  }, []);

  const toggleModal = () => {
    setIsModalOpen(!isModalOpen);
  };

  const handleRegister = () => {
    if (password !== confirmPassword) {
      alert('Le password non corrispondono');
      return;
    }

    const newUser = {
      email: email,
      password: password,
    };

    fetch('http://localhost:8080/api/utente/registrazione', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(newUser),
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Errore nella registrazione');
        }
        alert('Registrazione riuscita! Controlla la tua email per la verifica.');
        toggleModal();
      })
      .catch(error => {
        console.error('Errore:', error);
        alert('Si è verificato un errore durante la registrazione. Riprova più tardi.');
      });
  };

  const handleVerifyEmail = () => {
    const verificationData = {
      email: email,
      tokenInserito: verificationCode,
    };

    fetch('http://localhost:8080/api/utente/verifica-email', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(verificationData),
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Errore nella verifica del codice');
        }
        alert('Email verificata con successo!');
      })
      .catch(error => {
        console.error('Errore:', error);
        alert('Codice di verifica non valido o errore nel server.');
      });
  };

  const handleSwitchToRegister = () => {
    setIsLogin(false);
  };

  const handleSwitchToLogin = () => {
    setIsLogin(true);
  };

  return (
    <div className="App">
      <h1>Homepage</h1>
      <button onClick={toggleModal}>Login / Sign In</button>

      {/* Modal per Login e Registrazione */}
      <Modal
        isOpen={isModalOpen}
        onRequestClose={toggleModal}
        contentLabel="Login Modal"
        className="modal"
        overlayClassName="overlay"
      >
        <h2>{isLogin ? 'Login' : 'Registrati'}</h2>

        {/* Form di Login */}
        {isLogin ? (
          <div>
            <input
              type="email"
              placeholder="Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
            <input
              type="password"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            <button>Accedi</button>

            <p>
              Non hai un account?{' '}
              <button onClick={handleSwitchToRegister}>Registrati</button>
            </p>
          </div>
        ) : (
          // Form di Registrazione
          <div>
            <input
              type="email"
              placeholder="Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
            <input
              type="password"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            <input
              type="password"
              placeholder="Conferma Password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
            />
            <button onClick={handleRegister}>Registrati</button>

            <p>
              Hai già un account?{' '}
              <button onClick={handleSwitchToLogin}>Accedi</button>
            </p>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default App;
