import React, { useState, useEffect } from 'react';



const Home = () => {


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

      {/* Gestione questionario compilato per utente non registrato */}
      <div className='ml-16 mt-72'>
        <h2 className="text-2xl">
          Gestione questionari compilati
        </h2>
        <div className="flex mt-5">
          <input type="text" placeholder='Inserisci un codice univoco' className='bg-personal-purple bg-opacity-20 text-black px-16' />
          <button className='bg-personal-purple text-white py-2 px-4'>Invia</button>
        </div>
      </div>
    </div>
  );
};

export default Home;
