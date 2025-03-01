import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';

import Home from './pages/home/Home';
import HomeLogged from './pages/home/HomeLogged';
import NavbarHome from './components/NavbarHome';
import NavBarLogged from './components/NavbarLogged';
import Questionari from './pages/questionari/Questionari';
import CompilaQuestionario from './components/CompilaQuestionario';
import Compilazioni from './pages/compilazioni/Compilazioni';
import VisualizzaQuestionario from './pages/questionari/VisualizzaQuestionario';
import VisualizzaQuestionarioCompilato from './pages/compilazioni/VisualizzaQuestionarioCompilato';
import Domande from './pages/domande/Domande';
import VisualizzaCompilazioniUtenti from './pages/questionari/VisualizzaCompilazioniUtenti';


// Funzione per decodificare il token e verificarne la scadenza
const isTokenValid = (token) => {
  if (!token) return false;

  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const expirationTime = payload.exp * 1000; 
    return expirationTime > Date.now(); // Verifica se il token è scaduto
  } catch (e) {
    return false; // Se il token non è valido, restituiamo false
  }
};

const App = () => {
  const [user, setUser] = useState(null);

  // Verifica se il token JWT è presente in localStorage al caricamento
  useEffect(() => {
    const token = localStorage.getItem('jwt');
    if (token && isTokenValid(token)) {
      setUser({ token });
    } else {
      localStorage.removeItem('jwt');
    }
  }, []); // Solo al primo caricamento

  return (
    <div className="font-jersey bg-gray-50 min-h-screen tracking-widest">
      <BrowserRouter>
        {user ? <NavBarLogged setUser={setUser} /> : <NavbarHome setUser={setUser} />}
        
        <Routes>
          <Route path="/" element={user ? <HomeLogged /> : <Home />} />
          <Route path="/questionari/compilaQuestionario/:id" element={<CompilaQuestionario />} />
          <Route path="/questionari/visualizzaQuestionarioCompilato/:idCompilazione/:idQuestionario" element={<VisualizzaQuestionarioCompilato />} />

          {/* Rotte protette per gli utenti autenticati */}
          {user ? (
            <>
              <Route path="/questionari" element={<Questionari user={user} />} />
              <Route path="/questionari/:id" element={<VisualizzaQuestionario user={user} />} />
              <Route path="/domande" element={<Domande user={user} />} />
              <Route path="/questionari/compilazioni" element={<Compilazioni />} />
              <Route path="/visualizzaCompilazioniDiTutti/:id" element={<VisualizzaCompilazioniUtenti />} />
            </>
          ) : (
            <>
              <Route path="/questionari/:id" element={<VisualizzaQuestionario />} />
            </>
          )}
        </Routes>
      </BrowserRouter>
    </div>
  );
};

export default App;