import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';

import Home from './pages/home/Home';
import HomeLogged from './pages/home/HomeLogged';
import NavbarWrapper from './components/NavbarWrapper';
import Questionari from './pages/questionari/Questionari';
import CompilaQuestionario from './pages/questionari/CompilaQuestionario';
import ContinuaCompilazioneQuestionario from './pages/questionari/ContinuaCompilazioneQuestionario';
import VisualizzaQuestionario from './pages/questionari/VisualizzaQuestionario';
import Domande from './pages/domande/Domande';
import ResetPassword from './pages/home/ResetPassword';


const isTokenValid = (token) => {
  if (!token) return false;

  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const expirationTime = payload.exp * 1000; 
    return expirationTime > Date.now(); 
  } catch (e) {
    return false; 
  }
};

const App = () => {
  const [user, setUser] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem('jwt');
    if (token && isTokenValid(token)) {
      setUser({ token });
    } else {
      localStorage.removeItem('jwt');
    }
  }, []); // Solo al primo caricamento

  return (
    <div className="font-jersey tracking-widest">
      <BrowserRouter>
        {/* Usa NavbarWrapper per gestire le navbar */}
        <NavbarWrapper user={user} setUser={setUser} />
        
        <Routes>
          {/* Condizioni per mostrare Home o HomeLogged */}
          <Route path="/" element={user ? <HomeLogged /> : <Home />} />

          {/* Rotte protette per gli utenti autenticati */}
          {user ? (
            <>
              <Route path="/questionari" element={<Questionari user={user} />} />
              <Route path="/questionari/compilaQuestionario/:id" element={<CompilaQuestionario /*user={user}*/ />} />
              <Route path="/questionari/:id" element={<VisualizzaQuestionario user={user} />} />
              <Route path="/domande" element={<Domande user={user} />} />
              <Route path="/continuaCompilazioneQuestionario" element={<ContinuaCompilazioneQuestionario />} />
            </>
          ) : (
            <>
              <Route path="/questionari/compilaQuestionario/:id" element={<CompilaQuestionario />} />
              <Route path="/questionari/:id" element={<VisualizzaQuestionario />} />
              <Route path="/reset-password/:token" element={<ResetPassword />} />
            </>
          )}
        </Routes>
      </BrowserRouter>
    </div>
  );
};

export default App;