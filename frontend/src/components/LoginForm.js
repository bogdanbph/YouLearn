import React, { useState } from "react";
import "../styles/LoginForm.css";
import NavigationBar from "./NavigationBar";

const LoginForm = (props) => {
  const [details, setDetails] = useState({ email: "", password: "" });

  const submitHandler = (e) => {
    e.preventDefault();

    props.login(details);
  };

  const handleRegister = (e) => {
    e.preventDefault();

    window.location.href = "/register";
  };

  return (
    <>
      <NavigationBar logout={props.logout} />
      <form onSubmit={submitHandler}>
        <div className="form-inner">
          <h2>Login</h2>
          <div className="form-group">
            <label htmlFor="email">Email: </label>
            <input
              type="text"
              name="email"
              id="email"
              onChange={(e) =>
                setDetails({ ...details, email: e.target.value })
              }
              value={details.email}
            />
          </div>
          <div className="form-group">
            <label htmlFor="password">
              Password:{" "}
              <i
                class="far fa-eye"
                id="togglePassword"
                style={{
                  marginLeft: "5px",
                  cursor: "pointer",
                  display: "inline-block",
                }}
                onClick={(e) => {
                  const password = document.getElementById("password");
                  const type =
                    password.getAttribute("type") === "password"
                      ? "text"
                      : "password";
                  password.setAttribute("type", type);
                }}
              ></i>
            </label>
            <input
              type="password"
              name="password"
              id="password"
              onChange={(e) =>
                setDetails({ ...details, password: e.target.value })
              }
              value={details.password}
            />
          </div>
          <input type="submit" value="LOGIN" />
          <input
            id="register-button"
            type="submit"
            value="REGISTER"
            onClick={handleRegister}
          />
        </div>
      </form>
    </>
  );
};

export default LoginForm;
