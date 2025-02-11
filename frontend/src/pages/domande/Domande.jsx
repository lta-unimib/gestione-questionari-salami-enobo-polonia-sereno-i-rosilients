import React from 'react'

import CreaDomanda from './CreaDomanda'

const Domande = ({ user }) => {
  return (
    <div className='mx-24'>
      <h1 className="text-4xl">Domande</h1>
      <h2 className="mt-8 text-2xl ml-6">Le tue domande</h2>
      <CreaDomanda user={user}/>

    </div>
  )
}

export default Domande