import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Link } from 'react-router-dom';
import { UserIcon, ArrowLeftStartOnRectangleIcon, TrashIcon } from '@heroicons/react/24/solid';

const NavbarLogged = ({ setUser }) => {
    const navigate = useNavigate();
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const dropdownRef = useRef(null); // Riferimento al dropdown
    const [isDropdownProfileOpen, setIsDropdownProfileOpen] = useState(false);
    const [isVerified, setIsVerified] = useState(false)

    const handleClickOutside = (event) => {
        if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
            setIsDropdownOpen(false);
            setIsDropdownProfileOpen(false);
        }
    };

    useEffect(() => {
        if (isDropdownOpen) {
            document.addEventListener("mousedown", handleClickOutside);
        } else {
            document.removeEventListener("mousedown", handleClickOutside);
        }
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, [isDropdownOpen]);

    const handleLogout = async () => {
        try {
            await fetch('http://localhost:8080/auth/logout', {
                method: 'POST',
                credentials: 'include',
            });
            localStorage.removeItem('jwt');
            localStorage.removeItem('userEmail');
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
        <nav>
            <div className="flex justify-between py-6 px-4 md:px-16">
                <div className="ml-4">
                    <Link className='text-3xl text-personal-purple font-semibold' to='/'>WebSurveys</Link>
                </div>
                <div className="flex justify-end space-x-6 w-full">  
                    <div className='my-auto'>
                        <Link 
                            className="ml-62 hover:italic whitespace-nowrap mr-10" 
                            to="/questionari/compilazioni"
                        >
                            Compilazioni
                        </Link>
                    </div>                  
                    <div className='my-auto'>
                        <Link className='hover:italic mr-10' to='/domande'>Domande</Link>
                    </div>
                    <div className='my-auto'>
                        <Link className='hover:italic mr-10' to='/questionari'>Questionari</Link>
                    </div>
                    
                    {/* Gestione Profilo Dropdown */}
                    <div className="relative ml-64" ref={dropdownRef}>
                    <button 
                        onClick={() => setIsDropdownOpen(prev => !prev)} 
                        className="p-2 rounded-full border-2 border-personal-purple bg-white text-personal-purple hover:bg-personal-purple hover:text-white transition duration-300"
                    >
                        <UserIcon className="h-6 w-6" />
                    </button>
                    {isDropdownOpen && (
                        <div className="absolute right-0 mt-2 w-60 bg-white border rounded shadow-xl py-1 transition-all duration-200 ease-in-out">
                            <button className='text-gray-500 ml-4 pointer-events-none'> 
                                <span>{localStorage.getItem('userEmail')}</span>
                            </button>

                            <div className="border-t border-gray-300 my-1" />

                            <button 
                                onClick={handleDeleteProfile} 
                                className="flex justify-between gap-4 w-full px-4 py-2 text-red-600 hover:bg-gray-200"
                            >
                                <span>Elimina Account</span>
                                <TrashIcon className='w-6'/>
                            </button>

                            <div className="border-t border-gray-300 my-1" />

                            <button 
                                onClick={handleLogout} 
                                className="flex justify-between gap-4 w-full text-left px-4 py-2 text-blue-500 hover:bg-gray-200"
                            >
                                <span>Logout</span>
                                <ArrowLeftStartOnRectangleIcon className='w-6' />
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
