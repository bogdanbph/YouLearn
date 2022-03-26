import React from "react";
import NavigationBar from "./NavigationBar";
import '../styles/HomePage.css';
import UserService from "../service/UserService";
import { toast } from 'react-toastify'; 

class HomePage extends React.Component {

    constructor(props) {
        super(props);
    }

    async componentDidMount() {
        if (localStorage.getItem('role') === null && localStorage.getItem('user')) {
            await UserService.retrieveRoleForUser(localStorage.getItem('user'), localStorage.getItem('token'))
                .then(res => {
                    localStorage.setItem('role', res.data);
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
    }

    render() {
        return (
            <div className="welcome">
                <NavigationBar logout={this.props.logout}/>
    
                <div className="common-body" id="common-body-home">
                    <h2 id='welcome-text'>Welcome{localStorage.getItem('user') ? <span>, {localStorage.getItem('user').split('@')[0]}!</span> : '!'}</h2>
                </div>
            </div>
        );
    }

}

export default HomePage;