import React from 'react'
import { Link } from 'react-router-dom'

const NavbarLogged = () => {
  return (
    <nav className=''>
        <div className="absolute left-[5%] w-20 h-20">
            <h1 className='text-3xl'>WebSurveys</h1>
        </div>
        <div className="flex justify-evenly  bg-custom-fixtec py-4">
            <div>
                <Link className='ml-72 hover:italic mr-10' to='/domande'>Domande</Link>
            </div>
            <div>
                <Link className='ml-72 hover:italic mr-10' to='/questionari'>Questionari</Link>
            </div>
            <div className="">
                <span>ACCOUNT</span>
            </div>
        </div>
    </nav>
  )
}

export default NavbarLogged