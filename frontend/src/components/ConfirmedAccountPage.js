import React from "react";
import '../styles/HomePage.css';

class ConfirmedAccountPage extends React.Component {

    constructor() {
        super();
    }

    componentDidMount() {
        setTimeout(function() {
            window.location.href="/login";
        }, 2500);
    }

    render() {
        return (
            <div className="confirmed-page">
                <div className="common-body" id="common-body-home">
                    <h2 id='welcome-text'>Congratulations! <br/>Your account has been confirmed!</h2>
                </div>
            </div>
        );
    }
}

export default ConfirmedAccountPage;