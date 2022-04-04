import NavigationBar from "./NavigationBar";
import UserService from "../service/UserService";
import React from "react";
import stockprofilepic from "../assets/stockprofilepic.jpg";
import stockprofilepic_female from "../assets/stockprofilepic_female.jpg";
import "../styles/ProfilePage.css";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "../styles/ProfilePage.scss";

class ProfilePage extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      info: [],
    };
  }

  componentDidMount() {
    this.retrieveData();
  }

  loadFile(event) {
    var image = document.getElementById("profile-pic");

    if (event.target.files[0] !== undefined) {
      image.src = URL.createObjectURL(event.target.files[0]);
      image.style.height = "150px";
      image.style.width = "150px";
    }
  }

  retrieveData = () => {
    const token = localStorage.getItem("token");
    const email = localStorage.getItem("user");
    if (token) {
      UserService.retrieveUserProfile(email, token)
        .then(async (res) => {
          const username = res.data.username;
          const role = res.data.authorities[0].authority;
          const gender = res.data.gender;
          await UserService.retrieveCertifications(email, token).then((res) => {
            const certifications = res.data;
            this.setState({
              info: (
                <div>
                  <div
                    className="card profile"
                    id="profile-info"
                    style={{ width: "18rem" }}
                  >
                    <div class="profile-pic">
                      <label class="-label" for="file">
                        <span class="glyphicon glyphicon-camera"></span>
                        <span>Change Image</span>
                      </label>
                      <input id="file" type="file" onChange={this.loadFile} />

                      {gender.toLowerCase() === "male" ? (
                        <img
                          src={stockprofilepic}
                          id="profile-pic"
                          alt="Card Image Cap"
                          width="200"
                        />
                      ) : (
                        <img
                          id="profile-pic"
                          src={stockprofilepic_female}
                          alt="Card Image Cap"
                          width="200"
                        />
                      )}
                    </div>

                    <div className="card-body">
                      <div className="card-text">Username: {username}</div>
                      <div className="card-text">Role: {role}</div>
                    </div>
                  </div>
                  <div className="card certification" id="certification-info">
                    <div style={{ fontSize: "45px" }}>
                      {certifications.length > 0 ? (
                        <div>
                          &nbsp;&nbsp;&nbsp;
                          <p style={{ fontSize: "45px", marginLeft: "2%" }}>
                            Certifications:{" "}
                          </p>
                          <div className="certifications">
                            {certifications.map((certification) => (
                              <ul
                                className="certification-list-item grid-item"
                                style={{
                                  border: "1px solid red",
                                  borderRadius: "10px",
                                }}
                              >
                                <li>Course: {certification.courseName}</li>
                                <li>
                                  Instructor: {certification.instructorName}
                                </li>
                                <li>
                                  Registerd at: {certification.registeredAt}
                                </li>
                                <li>
                                  Completed at: {certification.completedAt}
                                </li>
                              </ul>
                            ))}
                          </div>
                        </div>
                      ) : (
                        <a
                          id="no-certifications"
                          style={{
                            fontSize: "45px",
                            marginLeft: "20px",
                            marginRight: "20px",
                          }}
                          href="/courses"
                        >
                          No certifications available.
                        </a>
                      )}
                    </div>
                  </div>
                </div>
              ),
            });
          });
        })
        .catch((ex) => {
          const errorMessage =
            ex.response !== undefined
              ? ex.response.data.message
              : "Backend is down!";
          toast.error("Profile info could not be retrieved! " + errorMessage, {
            position: "top-right",
            autoClose: 5000,
            hideProgressBar: false,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
          });

          if (
            (ex.response !== undefined &&
              ex.response.data.message.includes("expire")) ||
            ex.response.data.message.includes("There is no user")
          ) {
            setTimeout(function () {
              localStorage.clear();
              window.location.href = "/login";
            }, 2500);
          }
        });
    } else {
      toast.error(
        "No token present in localStorage. Redirected to login page! ",
        {
          position: "top-right",
          autoClose: 5000,
          hideProgressBar: false,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
        }
      );
      setTimeout(function () {
        localStorage.clear();
        window.location.href = "/login";
      }, 2500);
    }
  };

  render() {
    return (
      <div className="profile-page">
        <NavigationBar logout={this.props.logout} />
        <div className="common-body" id="common-body-profile">
          {this.state.info}
        </div>
      </div>
    );
  }
}

export default ProfilePage;
