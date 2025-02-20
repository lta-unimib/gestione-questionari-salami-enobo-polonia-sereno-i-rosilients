import React from 'react';
import { useLocation } from 'react-router-dom';
import NavbarHome from './NavbarHome';
import NavBarLogged from './NavbarLogged';

const NavbarWrapper = ({ user, setUser }) => {
  const location = useLocation(); 

  const isResetPasswordPage = location.pathname.startsWith('/reset-password');

  if (isResetPasswordPage) {
    return null; 
  }

  return user ? <NavBarLogged setUser={setUser} /> : <NavbarHome setUser={setUser} />;
};

export default NavbarWrapper;