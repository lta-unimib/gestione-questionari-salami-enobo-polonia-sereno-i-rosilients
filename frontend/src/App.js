import React, { useEffect, useState } from 'react';
import { BrowserRouter, Routes, Route } from "react-router-dom";

import Home from "./pages/home/Home";
import HomeLogged from "./pages/home/HomeLogged";
import Navbar from './components/NavbarHome';
import NavBarLogged from './components/NavbarLogged';
import Questionari from "./pages/questionari/Questionari";
import Domande from "./pages/domande/Domande";

const App = () => {
  const [user, setUser] = useState(null);

  useEffect(() => {
    fetch("http://localhost:8080/utente/info", {
      method: "GET",
      credentials: "include",
    })
      .then(response => response.ok ? response.json() : Promise.reject())
      .then(data => setUser(data))
      .catch(() => setUser(null));

  }, []);

  return (
    <div className="font-jersey tracking-widest">
      <BrowserRouter>
        {user ? <NavBarLogged setUser={setUser} /> : <Navbar setUser={setUser} />}
        <Routes>
          <Route path="/" element={user ? <HomeLogged/> : <Home />} />
          {user && (
            <>
              <Route path="/questionari" element={<Questionari user={user} />} />
              <Route path="/domande" element={<Domande user={user} />} /> 
            </>
          )}
        </Routes>
      </BrowserRouter>
    </div>
  );
};

export default App;
