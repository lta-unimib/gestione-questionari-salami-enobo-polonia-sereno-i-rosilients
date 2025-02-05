import React, { useState, useEffect } from 'react';

import CreaQuestionario from '../questionari/CreaQuestionario';
import CreaDomanda from '../domande/CreaDomanda';

const Home = () => {

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

  return (
    <div className="p-4">
      <CreaQuestionario />
      <CreaDomanda />
    </div>
  );
};

export default Home;
