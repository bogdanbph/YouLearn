import React, { useState } from 'react';
import '../styles/LoginForm.css';
import RegisterService from '../service/RegisterService';
import { toast } from 'react-toastify';
import NavigationBar from './NavigationBar';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faInfoCircle } from '@fortawesome/fontawesome-free-solid';

const RegisterForm = (props) => {
    const [details, setDetails] = useState({firstName:"", lastName:"", email:"", password:"", gender:"male", role: "REGULAR_USER"});

    const submitHandler = e => {
        e.preventDefault();

        RegisterService.submitRegisterForm(details)
        .then(() => {
            toast.success("Registration successful! Please confirm your account on email!", {
                position: "top-right",
                autoClose: 5000,
                hideProgressBar: false,
                closeOnClick: true,
                pauseOnHover: true,
                draggable: true,
                progress: undefined
            });
            setTimeout(function(){
                window.location.href = "/login";
            }, 2000);
        })
        .catch(ex => {
            const errorMessage = ex.response !== undefined ? ex.response.data.message : "Backend is down!";
            toast.error("Register failed! " + errorMessage, {
                position: "top-right",
                autoClose: 5000,
                hideProgressBar: false,
                closeOnClick: true,
                pauseOnHover: true,
                draggable: true,
                progress: undefined
            });
        });
    }

    return (
        <>
            <NavigationBar logout={props.logout}/>
            <form onSubmit={submitHandler}>
                <div className="form-inner">
                    <h2>Register</h2>
                    <div className="form-group">
                        <label htmlFor="first-name">First Name: </label>
                        <input type="text" name="first-name" id="first-name" onChange={e => setDetails({...details, firstName: e.target.value})} value={details.firstName}/>
                    </div>
                    <div className="form-group">
                        <label htmlFor="last-name">Last Name: </label>
                        <input type="text" name="last-name" id="last-name" onChange={e => setDetails({...details, lastName: e.target.value})} value={details.lastName}/>
                    </div>
                    <div className="form-group">
                        <label htmlFor="email">Email: </label>
                        <input type="text" name="email" id="email" onChange={e => setDetails({...details, email: e.target.value})} value={details.email}/>
                    </div>
                    <div className="form-group">
                        <label htmlFor="password">Password: <FontAwesomeIcon icon={faInfoCircle} title="The password should contain one capital letter, one number and at least 8 characters."/></label>
                        <input type="password" name="password" id="password" onChange={e => setDetails({...details, password: e.target.value})} value={details.password}/>
                    </div>
                    <div className="form-group">
                        <label htmlFor="gender">Gender: </label>
                        <select name="gender" id="gender" onChange={e => {
                            console.log(e.target.value);
                            setDetails({...details, gender: e.target.value})
                        }} value={details.gender}>
                            <option value="male">Male</option>
                            <option value="female">Female</option>
                        </select>
                    </div>
                    <input type="submit" value="REGISTER"/>
                </div>
            </form>
        </>
    )
}

export default RegisterForm;