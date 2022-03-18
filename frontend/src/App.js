import React from 'react';
import LoginForm from './components/LoginForm';
import LoginService from './service/LoginService';
import {Route, Routes, Navigate} from 'react-router-dom';
import HomePage from './components/HomePage';
import ProfilePage from './components/ProfilePage';

function App() {
  const handleLogin = details => {
    LoginService.submitLoginForm(details.email, details.password)
      .then(res => {
        localStorage.setItem('user', details.email);
        localStorage.setItem('token', res.data);
        window.location.href = "/"
      })
      .catch(ex => {
        console.log(ex);
      });
  }

  const handleLogout = () => {
    localStorage.removeItem('user');
    window.location.href = '/login';
  }

  return (
    <div className="App">
      {/* {(user.username !== "") && <LoginForm login={handleLogin} error={error}/>} */}
      <Routes>
        <Route exact path="/login" element={<LoginForm login={handleLogin}/>}/>
        <Route path="/" element={<HomePage logout={handleLogout}/>}/>
        <Route path="/profile" element={localStorage.getItem('user') ? <ProfilePage logout={handleLogout}/> :  <Navigate to="/"/>}/>
      </Routes>
    </div>
  );
}

export default App;
