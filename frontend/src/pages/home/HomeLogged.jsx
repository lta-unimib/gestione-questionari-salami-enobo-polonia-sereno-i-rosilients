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
      {/* <CreaQuestionario />
      <CreaDomanda /> */}
      <div className="flex justify-center mt-16">
        <div className="flex flex-col gap-4">
          <h1 className='text-5xl font-semibold text-personal-purple text-center'>WebSurveys</h1>
          <input type="text" placeholder='Cerca un questionario' className='bg-personal-purple bg-opacity-20 text-black py-2 px-52 rounded-lg'/>
        </div>
      </div>

    </div>
  );
};

export default Home;
