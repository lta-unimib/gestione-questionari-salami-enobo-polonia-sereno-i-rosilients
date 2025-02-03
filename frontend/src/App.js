import React, { useState, useEffect } from 'react';
import Modal from 'react-modal';
import CreaQuestionario from './CreaQuestionario';
import './App.css';

const App = () => {
  const [isModalOpen, setIsModalOpen] = useState(false); // Stato per aprire/chiudere il popup
  const [isLogin, setIsLogin] = useState(true); // Stato per determinare se è il login o la registrazione
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState(''); // Per confermare la password
  const [verificationCode, setVerificationCode] = useState(''); // Codice di verifica per email
  const [isCreatingQuestionario, setIsCreatingQuestionario] = useState(false); // Stato per la creazione del questionario
  const [questionarioNome, setQuestionarioNome] = useState('');
  const [utenteEmail, setUtenteEmail] = useState('');

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

    fetch('http://localhost:8080/utente/registrazione', {
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

  /*
  const handleVerifyEmail = () => {
    const verificationData = {
      email: email,
      tokenInserito: verificationCode,
    };

    fetch('http://localhost:8080/utente/verifica-email', {
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

  */

  
  const handleCreaQuestionario = () => {
    if (!questionarioNome || !utenteEmail) {
      alert('Compila tutti i campi.');
      return;
    }
  
    const questionarioData = {
      nome: questionarioNome,
      utente: {
        email: utenteEmail,
      },
    };
  
    fetch('http://localhost:8080/questionari', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(questionarioData),
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Errore nella creazione del questionario');
        }
        return response.json();
      })
      .then(data => {
        alert('Questionario creato con successo!');
        setQuestionarioNome(''); // Resetta il nome
        setUtenteEmail(''); // Resetta l'email
        setIsCreatingQuestionario(false); // Nascondi il modulo dopo la creazione
      })
      .catch(error => {
        console.error('Errore:', error);
        alert('Si è verificato un errore durante la creazione del questionario.');
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


      {/* Sezione per la Creazione del Questionario */}
      {!isCreatingQuestionario ? (
        <div>
          <button onClick={() => setIsCreatingQuestionario(true)}>Crea Questionario</button>
        </div>
      ) : (
        <div>
          <input
            type="text"
            placeholder="Nome del Questionario"
            value={questionarioNome}
            onChange={(e) => setQuestionarioNome(e.target.value)}
          />
          <input
            type="email"
            placeholder="Email Utente"
            value={utenteEmail}
            onChange={(e) => setUtenteEmail(e.target.value)}
          />
          <button onClick={handleCreaQuestionario}>Crea</button>
          <button onClick={() => setIsCreatingQuestionario(false)}>Annulla</button>
        </div>
      )}
    </div>
  );
};

export default App;
