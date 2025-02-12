import React from 'react';
import { Link } from 'react-router-dom';

const NavbarLogged = ({ setUser }) => {

    const handleLogout = async () => {
        try {
          // Effettua la chiamata per disconnettere l'utente (opzionale)
          await fetch('http://localhost:8080/auth/logout', {
            method: 'POST',
            credentials: 'include', // Mantieni la coerenza se hai bisogno di sessioni, altrimenti puoi rimuoverlo
          });
      
          // Rimuove il token dal sessionStorage
          sessionStorage.removeItem('jwt'); 
      
          // Rimuove l'utente dallo stato globale (se applicabile)
          setUser(null);
        } catch (error) {
          console.error('Errore durante il logout:', error);
          alert('Si è verificato un errore durante il logout.');
        }
      };
      
      

  return (
    <nav className=''>
        <div className="flex justify-between py-6">
            <div className="ml-16">
                <Link className='text-3xl text-personal-purple font-semibold' to='/'>WebSurveys</Link>
            </div>
            <div className="flex justify-end mr-16">
                <div className='my-auto'>
                    <Link className='ml-72 hover:italic mr-10' to='/domande'>Domande</Link>
                </div>
                <div className='my-auto'>
                    <Link className=' hover:italic mr-10' to='/questionari'>Questionari</Link>
                </div>
                <div className="ml-64">
                  <button 
                    onClick={handleLogout} 
                    className="bg-red-500 text-white py-2 px-4 rounded hover:bg-red-700"
                  >
                    Logout
                  </button>
                </div>
            </div>
        </div>
    </nav>
  );
}

export default NavbarLogged;
