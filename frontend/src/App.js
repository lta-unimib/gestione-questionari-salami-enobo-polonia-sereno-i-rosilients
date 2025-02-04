import React  from 'react';
import { BrowserRouter, Routes, Route } from "react-router-dom";

import Home from "./pages/home/Home";

import Navbar from './components/NavbarHome'

const App = () => {

  return (
    <BrowserRouter>
        <Navbar />
        <Routes>
            <Route path="/" element={<Home />} />
        </Routes>
    </BrowserRouter>
  );
};

export default App;
