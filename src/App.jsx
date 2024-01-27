import React from 'react'
import Header from './components/Header'
import { PrivacyPage } from './pages/PrivacyPage'
import Footer from './components/Footer'
import Banner from './pages/Banner'

function App() {
  return (
    <>
    <Header />
    <Banner />
    <PrivacyPage />
    <Footer />
    </>
  )
}

export default App