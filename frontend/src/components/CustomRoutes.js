import {Route, Routes, Navigate} from 'react-router-dom';
import HomePage from './HomePage';
import ProfilePage from './ProfilePage';
import LoginForm from './LoginForm';
import RegisterForm from './RegisterForm';
import CoursesPage from './CoursesPage';
import ConfirmedAccountPage from './ConfirmedAccountPage';


export default function CustomRoutes(props) {
    return (
      <Routes>
        <Route exact path="/login" element={<LoginForm login={props.login}/>}/>
        <Route path="/" element={<HomePage logout={props.logout}/>}/>
        <Route path="/profile" element={localStorage.getItem('user') ? <ProfilePage logout={props.logout}/> :  <Navigate to="/"/>}/>
        <Route exact path="/register" element={<RegisterForm/>}/>
        <Route path="/courses" element={localStorage.getItem('user') ? <CoursesPage logout={props.logout}/> :  <Navigate to="/"/>}/>
        <Route path="/confirmed" element={window.location.search.includes("confirmed") ? <ConfirmedAccountPage logout={props.logout}/> :  <Navigate to="/"/>}/>
      </Routes>
    );
}