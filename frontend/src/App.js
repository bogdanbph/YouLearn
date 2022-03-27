import React from "react";
import LoginService from "./service/LoginService";
import { ToastContainer, toast } from "react-toastify";
import CustomRoutes from "./components/CustomRoutes";
import "react-toastify/dist/ReactToastify.css";

function App() {
  const handleLogin = (details) => {
    LoginService.submitLoginForm(details.email, details.password)
      .then((res) => {
        localStorage.setItem("user", details.email);
        localStorage.setItem("token", res.data);
        window.location.href = "/";
      })
      .catch((ex) => {
        const errorMessage =
          ex.response !== undefined
            ? ex.response.data.message
            : "Backend is down!";
        toast.error("Login failed! " + errorMessage, {
          position: "top-right",
          autoClose: 5000,
          hideProgressBar: false,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
        });
      });
  };

  const handleLogout = () => {
    localStorage.clear();
    window.location.href = "/login";
  };

  return (
    <div className="App">
      <CustomRoutes login={handleLogin} logout={handleLogout} />
      <ToastContainer
        position="top-right"
        autoClose={5000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
      />
    </div>
  );
}

export default App;
