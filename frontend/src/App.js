import React  from 'react';
import { BrowserRouter, Routes, Route } from "react-router-dom";

import Home from "./pages/home/Home";
import HomeLogged from "./pages/home/HomeLogged";

import Navbar from './components/NavbarHome';
import NavBarLogged from './components/NavbarLogged';

const App = () => {

  return (
    <div className='font-jersey tracking-widest '>
      <BrowserRouter>
          <Navbar />
          <Routes>
              <Route path="/" element={<Home />} />
          </Routes>
      </BrowserRouter>
    </div>
  );
};

export default App;
