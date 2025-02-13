import React, { useState } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';

import Home from './pages/home/Home';
import HomeLogged from './pages/home/HomeLogged';
import Navbar from './components/NavbarHome';
import NavBarLogged from './components/NavbarLogged';
import Questionari from './pages/questionari/Questionari';
import CompilaQuestionario from './pages/questionari/CompilaQuestionario';
import Domande from './pages/domande/Domande';

const App = () => {
  const [user, setUser] = useState(null);

  return (
    <div className="font-jersey tracking-widest">
      <BrowserRouter>
        {user ? <NavBarLogged setUser={setUser} /> : <Navbar setUser={setUser} />}
        
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