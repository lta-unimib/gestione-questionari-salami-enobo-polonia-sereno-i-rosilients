import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Link } from 'react-router-dom';
import {UserIcon} from '@heroicons/react/24/solid'

const NavbarLogged = ({ setUser }) => {
    const navigate = useNavigate();
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);

    const toggleDropdown = () => {
        setIsDropdownOpen(!isDropdownOpen);
    };

    const handleLogout = async () => {
        try {
            await fetch('http://localhost:8080/auth/logout', {
                method: 'POST',
                credentials: 'include',
            });
            localStorage.removeItem('jwt');
            // Rimuovi il cookie del refresh token
            document.cookie = 'refreshToken=; path=/auth/refresh; max-age=0; Secure; HttpOnly';   
            setUser(null);
            navigate('/');
        } catch (error) {
            console.error('Errore durante il logout:', error);
            alert('Si è verificato un errore durante il logout.');
        }
    };
    

    const handleDeleteProfile = async () => {
        const confirmDelete = window.confirm("Sei sicuro di voler eliminare il tuo profilo? Questa azione è irreversibile!");
        
        if (!confirmDelete) return;

        try {
            const token = localStorage.getItem('jwt');
            const response = await fetch('http://localhost:8080/auth/deleteProfile', {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            });

            if (response.ok) {
                alert("Profilo eliminato con successo.");
                localStorage.removeItem('jwt');
                setUser(null);
                navigate('/');
            } else {
                alert("Errore durante l'eliminazione del profilo.");
            }
        } catch (error) {
            console.error("Errore:", error);
            alert("Si è verificato un errore durante l'eliminazione del profilo.");
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
                        <Link className='hover:italic mr-10' to='/questionari'>Questionari</Link>
                    </div>
                    
                    {/* Gestione Profilo Dropdown */}
                    <div className="relative ml-64">
                    <button 
        onClick={toggleDropdown} 
        className="p-2 rounded-full border-2 border-personal-purple bg-white text-personal-purple hover:bg-personal-purple hover:text-white transition duration-300"
    >
        <UserIcon className="h-6 w-6" />
    </button>

                        {isDropdownOpen && (
                            <div className="absolute right-0 mt-2 w-48 bg-white border rounded shadow-lg">
                                <button 
                                    onClick={handleLogout} 
                                    className="block w-full text-left px-4 py-2 text-gray-800 hover:bg-gray-200"
                                >
                                    Logout
                                </button>
                                <button 
                                    onClick={handleDeleteProfile} 
                                    className="block w-full text-left px-4 py-2 text-red-600 hover:bg-gray-200"
                                >
                                    Elimina Profilo
                                </button>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </nav>
    );
}

export default NavbarLogged;