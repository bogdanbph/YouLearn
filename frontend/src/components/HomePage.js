import React from "react";
import NavigationBar from "./NavigationBar";
import "../styles/HomePage.css";
import UserService from "../service/UserService";
import { toast } from "react-toastify";
import jwtDecode from "jwt-decode";

class HomePage extends React.Component {
  constructor(props) {
    super(props);
  }

  async componentDidMount() {
    const token = localStorage.getItem("token");

    if (token !== null && token !== undefined) {
      const decodedToken = jwtDecode(token);
      if (decodedToken.exp * 1000 < new Date().getTime()) {
        toast.error("Token expired!", {
          position: "top-right",
          autoClose: 5000,
          hideProgressBar: false,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
        });
        setInterval(function () {
          localStorage.clear();
          window.location.href = "/login";
        }, 1500);
      }
    }

    if (localStorage.getItem("role") === null && localStorage.getItem("user")) {
      await UserService.retrieveRoleForUser(
        localStorage.getItem("user"),
        localStorage.getItem("token")
      )
        .then((res) => {
          localStorage.setItem("role", res.data);
        })
        .catch((ex) => {
          const errorMessage =
            ex.response !== undefined
              ? ex.response.data.message
              : "Backend is down!";
          toast.error("Unexpected error! " + errorMessage, {
            position: "top-right",
            autoClose: 5000,
            hideProgressBar: false,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
          });
        });
    }
  }

  render() {
    return (
      <div className="welcome">
        <NavigationBar logout={this.props.logout} />

        <div className="common-body" id="common-body-home">
          <h2 id="welcome-text">
            Welcome
            {localStorage.getItem("user") ? (
              <span>, {localStorage.getItem("user").split("@")[0]}!</span>
            ) : (
              "!"
            )}
            <br />
            Student Progress Tracker App
          </h2>
        </div>
      </div>
    );
  }
}

export default HomePage;
