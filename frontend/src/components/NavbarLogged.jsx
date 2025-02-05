import React from 'react'
import { Link } from 'react-router-dom'

const NavbarLogged = () => {
  return (
    <nav className=''>
        <div className="flex justify-between py-6">
            <div className="ml-16">
                <h1 className="text-3xl text-personal-purple font-semibold">WebSurveys</h1>
            </div>
            <div className="flex justify-end mr-16">
                <div>
                    <Link className='ml-72 hover:italic mr-10' to='/domande'>Domande</Link>
                </div>
                <div>
                    <Link className=' hover:italic mr-10' to='/questionari'>Questionari</Link>
                </div>
                <div className="ml-64">
                    <span>ACCOUNT</span>
                </div>
            </div>
        </div>
    </nav>
  )
}

export default NavbarLogged