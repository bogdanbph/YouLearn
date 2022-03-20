import React from "react";
import NavigationBar from "./NavigationBar";
import '../styles/HomePage.css';

const HomePage = (props) => {

    return (
        <div className="welcome">
            <NavigationBar logout={props.logout}/>

            <div className="common-body" id="common-body-home">
                <h2 id='welcome-text'>Welcome{localStorage.getItem('user') ? <span>, {localStorage.getItem('user').split('@')[0]}!</span> : '!'}</h2>
            </div>
        </div>
    );
}

export default HomePage;