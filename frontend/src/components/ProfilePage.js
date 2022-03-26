import NavigationBar from "./NavigationBar";
import UserService from "../service/UserService";
import React from "react";
import stockprofilepic from '../assets/stockprofilepic.jpg';
import stockprofilepic_female from '../assets/stockprofilepic_female.jpg';
import '../styles/ProfilePage.css';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

class ProfilePage extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            info: []
        };
    }

    componentDidMount() {
        this.retrieveData();
    }

    retrieveData = () => {
        const token = localStorage.getItem('token');
        const email = localStorage.getItem('user');
        if (token) {
            UserService.retrieveUserProfile(email, token)
            .then(res => {
                const username = res.data.username;
                const role = res.data.authorities[0].authority;
                const gender = res.data.gender;

                this.setState({
                    info: <>
                            {gender.toLowerCase() === 'male' ? 
                                <img id="profile-pic" src={stockprofilepic} alt="Card Image Cap"/>
                                :
                                <img id="profile-pic" src={stockprofilepic_female} alt="Card Image Cap"/>
                            }
                            <div className="card-body">
                                <p className="card-text">Username: {username}</p>
                                <p className="card-text">Role: {role}</p>
                            </div>
                        </>
                });
            })
            .catch(ex => {
                const errorMessage = ex.response !== undefined ? ex.response.data.message : "Backend is down!";
                toast.error("Profile info could not be retrieved! " + errorMessage, {
                    position: "top-right",
                    autoClose: 5000,
                    hideProgressBar: false,
                    closeOnClick: true,
                    pauseOnHover: true,
                    draggable: true,
                    progress: undefined
                });

                if (ex.response !== undefined && ex.response.data.message.includes('expire') || ex.response.data.message.includes('There is no user')) {
                    setTimeout(function() {
                        localStorage.clear();
                        window.location.href="/login";
                    }, 2500);
                }
            })
        }
        else {
            toast.error("No token present in localStorage. Redirected to login page! ", {
                position: "top-right",
                autoClose: 5000,
                hideProgressBar: false,
                closeOnClick: true,
                pauseOnHover: true,
                draggable: true,
                progress: undefined
            });
            setTimeout(function() {
                localStorage.clear();
                window.location.href="/login";
            }, 2500);
        }
    }

    render() {
        return (
            <div className="profile-page">
                <NavigationBar logout={this.props.logout}/>
                <div className="common-body" id="common-body-profile">
                    <div className="card" style={{width:'18rem'}}>
                        {this.state.info}
                    </div>
                </div>
            </div>
        );
    }   
}

export default ProfilePage;