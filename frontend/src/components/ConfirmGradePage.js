import React from "react";
import "../styles/HomePage.css";

class ConfirmGradePage extends React.Component {
  constructor() {
    super();
  }

  componentDidMount() {
    setTimeout(function () {
      window.location.href = "/login";
    }, 2500);
  }

  render() {
    return (
      <div className="submission-page">
        <div className="common-body" id="common-body-home">
          <h2 id="welcome-text">
            Congratulations! <br />
            Your grade has been submitted!
          </h2>
        </div>
      </div>
    );
  }
}

export default ConfirmGradePage;
