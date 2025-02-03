import React, { useState } from 'react';

const CreaQuestionario = () => {
  const [questionarioNome, setQuestionarioNome] = useState('');
  const [utenteEmail, setUtenteEmail] = useState('');

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

    fetch('http://localhost:8080/api/questionari', {
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
      })
      .catch(error => {
        console.error('Errore:', error);
        alert('Si è verificato un errore durante la creazione del questionario.');
      });
  };

  return (
    <div>
      <h2>Crea un nuovo questionario</h2>
      <input
        type="text"
        placeholder="Nome del Questionario"
        value={questionarioNome}
        onChange={(e) => setQuestionarioNome(e.target.value)}
      />
      <input
        type="email"
        placeholder="Email dell'utente"
        value={utenteEmail}
        onChange={(e) => setUtenteEmail(e.target.value)}
      />
      <button onClick={handleCreaQuestionario}>Crea Questionario</button>
    </div>
  );
};

export default CreaQuestionario;
