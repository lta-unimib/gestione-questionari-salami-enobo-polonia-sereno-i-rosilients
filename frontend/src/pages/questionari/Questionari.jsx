import React from 'react'

import CreaQuestionario from './CreaQuestionario'

const Questionari = ({ setUser }) => {
  return (
    <div>
      <h1 className="text-4xl">Questionari</h1>
      <CreaQuestionario setUser={setUser}/>

    </div>
  )
}

export default Questionari