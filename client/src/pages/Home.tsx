import { useAuth } from "../context/AuthContext"

export const Home = () => {
  const {authorities, username, lastLogin} = useAuth();

  return (
    <div className='flex items-center flex-col h-full'>
      <div className="flex">
      <h2 className='font-bold mr-2'>Felhasználó név:</h2>
      <span>{username}</span>
      </div>
      <div className="flex">
      <h2 className='font-bold mr-2'>Szerepkör(ök): </h2>
      <span>
      {authorities.map((authority, index) => (
        <span key={index}>{authority}</span>
      ))}
      </span>
      </div>
      <div className="flex">
      <h2 className='font-bold mr-2'>Utolsó bejelentkezés időpontja:</h2>
      <span>{lastLogin ? lastLogin : 'Nincs rögzített bejelentkezési időpont'}</span>
      </div>
    </div>
  )
}
