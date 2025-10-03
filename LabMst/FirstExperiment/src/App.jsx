import { useState, useEffect } from 'react'

function Home() {
  return (
    <div>
      <h1 className="text-3xl font-bold">Home Page</h1>
      <p className="text-gray-600 mt-2">Welcome to the home page!</p>
    </div>
  )
}

function About() {
  return (
    <div>
      <h1 className="text-3xl font-bold">About Page</h1>
      <p className="text-gray-600 mt-2">routing app.</p>
    </div>
  )
}

function Contact() {
  return (
    <div>
      <h1 className="text-3xl font-bold">Contact Page</h1>
      <p className="text-gray-600 mt-2">Email: 23bcs11290@cuchd.in</p>
    </div>
  )
}

function App() {
  const [currentPath, setCurrentPath] = useState(window.location.hash.slice(1) || '/')

  useEffect(() => {
    const onHashChange = () => {
      setCurrentPath(window.location.hash.slice(1) || '/')
    }
    
    window.addEventListener('hashchange', onHashChange)
    return () => window.removeEventListener('hashchange', onHashChange)
  }, [])

  const navigate = (path) => {
    window.location.hash = path
  }

  const renderPage = () => {
    switch(currentPath) {
      case '/':
        return <Home />
      case '/about':
        return <About />
      case '/contact':
        return <Contact />
      default:
        return <div><h1 className="text-3xl font-bold text-red-600">404 - Page Not Found</h1></div>
    }
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow-md p-4">
        <div className="container mx-auto flex gap-4">
          <a 
            href="#/" 
            onClick={() => navigate('/')}
            className={`text-blue-600 hover:text-blue-800 ${currentPath === '/' ? 'font-bold' : ''}`}
          >
            Home
          </a>
          <span className="text-gray-400">|</span>
          <a 
            href="#/about" 
            onClick={() => navigate('/about')}
            className={`text-blue-600 hover:text-blue-800 ${currentPath === '/about' ? 'font-bold' : ''}`}
          >
            About
          </a>
          <span className="text-gray-400">|</span>
          <a 
            href="#/contact" 
            onClick={() => navigate('/contact')}
            className={`text-blue-600 hover:text-blue-800 ${currentPath === '/contact' ? 'font-bold' : ''}`}
          >
            Contact
          </a>
        </div>
      </nav>
      
      <div className="container mx-auto p-8">
        {renderPage()}
      </div>
    </div>
  )
}

export default App