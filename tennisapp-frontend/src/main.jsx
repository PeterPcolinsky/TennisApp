import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import './App.css';
import App from './App.jsx'

// 🔥 Auto-logout pri spustení aplikácie
sessionStorage.removeItem("username");
sessionStorage.removeItem("password");

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
