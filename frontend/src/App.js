import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';

import Home from './pages/home/Home';
import HomeLogged from './pages/home/HomeLogged';
import NavbarHome from './components/NavbarHome';
import NavBarLogged from './components/NavbarLogged';
import Questionari from './pages/questionari/Questionari';
import CompilaQuestionario from './pages/questionari/CompilaQuestionario';
import Domande from './pages/domande/Domande';


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
    <div className="font-jersey tracking-widest">
      <BrowserRouter>
        {user ? <NavBarLogged setUser={setUser} /> : <NavbarHome setUser={setUser} />}
        
        <Routes>
          {/* Condizioni per mostrare Home o HomeLogged */}
          <Route path="/" element={user ? <HomeLogged /> : <Home />} />

          {/* Rotte protette per gli utenti autenticati */}
          {user && (
            <>
              <Route path="/questionari" element={<Questionari user={user} />} />
              <Route path="/questionari/:id" element={<CompilaQuestionario user={user} />} />
              <Route path="/domande" element={<Domande user={user} />} />
            </>
          )}
        </Routes>
      </BrowserRouter>
    </div>
  );
};

export default App;
