import React from "react";
import NavigationBar from "./NavigationBar";
import "../styles/CoursesPage.css";
import CourseService from "../service/CourseService";
import Modal from "react-modal";
import { toast } from "react-toastify";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faInfoCircle } from "@fortawesome/fontawesome-free-solid";
import UserService from "../service/UserService";

class CoursesPage extends React.Component {
  constructor() {
    super();

    this.state = {
      courses: [],
      showModal: false,
      modalDetails: {
        email: localStorage.getItem("user"),
        numberOfChapters: 0,
        courseLink: "",
        courseName: "",
        price: 0.0,
        description: "",
      },
      availability: new Map(),
      visibleCourses: 0
    };

    this.handleEnroll = this.handleEnroll.bind(this);
  }

  async componentDidMount() {
    await UserService.retrieveUserProfile(
      localStorage.getItem("user"),
      localStorage.getItem("token")
    )
      .then()
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
    await this.retrieveCourses();

    Modal.setAppElement("body");
  }

  retrieveCourses = async () => {
    const token = localStorage.getItem("token");

    if (token) {
      await CourseService.retrieveCourses(localStorage.getItem("token"))
        .then(async (res) => {
          let courses = res.data;
          this.setState({
            courses: await Promise.all(
              courses.map(async (course) => {
                const urlParams = new URL(course.courseYoutubeId);
                let thumbnailLink =
                  "https://img.youtube.com/vi/" +
                  urlParams.searchParams.get("v") +
                  "/hqdefault.jpg";
                await this.checkEnrolled(course.id);
                if (course.isVisible || course.emailInstructor === localStorage.getItem("user")) {
                  this.setState({
                    visibleCourses: this.state.visibleCourses + 1
                  })
                }
                return (
                  <div key={course.id} style={{display: course.isVisible || course.emailInstructor === localStorage.getItem("user") ? "block" : "none"}}className="grid-item">
                    <div
                      className="card"
                      style={{ width: "18rem", height: "auto" }}
                    >
                      <div className="content-card">
                        <p className="description-card">
                          <h3>{course.courseName}</h3> (
                          {course.numberOfChapters} chapters)
                        </p>
                        {/* <iframe onLoad={this.handleOnclick} id="java-vid" width="200" height="200" src={course.link} title={course.title} frameBorder={0} allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowFullScreen></iframe>                         */}
                        <img
                          src={thumbnailLink}
                          height={"150px"}
                          id="course-image"
                        ></img>
                        <p className="description-card">
                          <br></br>${course.price.toFixed(2)}&nbsp;&nbsp;&nbsp;{" "}
                          <button
                            className="btn btn-warning"
                            onClick={() =>
                              this.handleEnroll(
                                course.id,
                                urlParams.searchParams.get("list"),
                                course.courseName,
                                course.numberOfChapters
                              )
                            }
                          >
                            {this.state.availability.get(course.id)
                              ? "Continue"
                              : "Enroll"}
                          </button>
                        </p>
                      </div>
                    </div>
                  </div>
                );
              })
            ),
          });
        })
        .catch((ex) => {
          const errorMessage =
            ex.response !== undefined
              ? ex.response.data.message
              : "Backend is down!";
          toast.error("Courses page could not be loaded! " + errorMessage, {
            position: "top-right",
            autoClose: 5000,
            hideProgressBar: false,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
          });

          if (
            ex.response !== undefined &&
            ex.response.data.message.includes("expire")
          ) {
            setTimeout(function () {
              localStorage.clear();
              window.location.href = "/login";
            }, 2500);
          }
        });
    }
  };

  handleEnroll = async (courseId, courseYoutubeId, courseName, chapters) => {
    if (this.state.availability.get(courseId) == false) {
      await CourseService.enroll(
        courseId,
        localStorage.getItem("user"),
        localStorage.getItem("token")
      )
        .then(() => {
          this.state.availability.set(courseId, true);
          toast.success("Enrollment successful!", {
            position: "top-right",
            autoClose: 5000,
            hideProgressBar: false,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
          });
          setTimeout(function () {
            window.location.reload();
          }, 2500);
        })
        .catch((ex) => {
          const errorMessage =
            ex.response !== undefined
              ? ex.response.data.message
              : "Backend is down!";
          toast.error("Courses page could not be loaded! " + errorMessage, {
            position: "top-right",
            autoClose: 5000,
            hideProgressBar: false,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
          });

          if (
            ex.response !== undefined &&
            ex.response.data.message.includes("expire")
          ) {
            setTimeout(function () {
              localStorage.clear();
              window.location.href = "/login";
            }, 2500);
          }
        });
    } else {
      window.location.href =
        "/courses/playlistId=" +
        courseYoutubeId +
        "&chapters=" +
        chapters +
        "&playlistName=" +
        courseName.replace(" ", "-");
    }
  };

  checkEnrolled = async (courseId) => {
    await CourseService.checkEnrolled(
      courseId,
      localStorage.getItem("user"),
      localStorage.getItem("token")
    )
      .then((res) => {
        this.state.availability.set(courseId, res.data);
      })
      .catch((ex) => {
        const errorMessage =
          ex.response !== undefined
            ? ex.response.data.message
            : "Backend is down!";
        toast.error("Courses page could not be loaded! " + errorMessage, {
          position: "top-right",
          autoClose: 5000,
          hideProgressBar: false,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
        });

        if (
          ex.response !== undefined &&
          ex.response.data.message.includes("expire")
        ) {
          setTimeout(function () {
            localStorage.clear();
            window.location.href = "/login";
          }, 2500);
        }
      });
  };

  handleSubmit = async (event) => {
    event.preventDefault();

    var expression =
      /[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)?/gi;
    var regex = new RegExp(expression);
    if (
      this.state.modalDetails.courseLink.match(regex) &&
      this.state.modalDetails.courseName.length > 3 &&
      this.state.modalDetails.numberOfChapters > 0 &&
      this.state.modalDetails.description.length > 25 &&
      this.state.modalDetails.price >= 0
    ) {
      if (localStorage.getItem("token")) {
        await CourseService.uploadCourse(
          this.state.modalDetails,
          localStorage.getItem("token")
        )
          .then((res) => {
            this.retrieveCourses();
          })
          .catch((ex) => {
            const errorMessage =
              ex.response !== undefined
                ? ex.response.data.message
                : "Backend is down!";
            toast.error("Courses page could not be loaded! " + errorMessage, {
              position: "top-right",
              autoClose: 5000,
              hideProgressBar: false,
              closeOnClick: true,
              pauseOnHover: true,
              draggable: true,
              progress: undefined,
            });

            if (
              ex.response !== undefined &&
              ex.response.data.message.includes("expire")
            ) {
              setTimeout(function () {
                localStorage.clear();
                window.location.href = "/login";
              }, 2500);
            }
          });
        this.setState({ showModal: false });
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
    } else {
      toast.error("Recheck the requirements for each field.", {
        position: "top-right",
        autoClose: 5000,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
      });
    }
  };

  render() {
    return (
      <div className="courses-page">
        <NavigationBar logout={this.props.logout} />
        <button
          type="button"
          id="add-course"
          style={{
            display:
              localStorage.getItem("role") === "INSTRUCTOR" ? "block" : "none",
          }}
          onClick={() => this.setState({ showModal: true })}
        >
          ADD COURSE
        </button>

        <Modal
          isOpen={this.state.showModal}
          onRequestClose={() => this.setState({ showModal: false })}
          contentLabel="ADD COURSE"
          className="add-course"
          overlayClassName="myoverlay"
          closeTimeoutMS={500}
        >
          <h2 id="header-form">Add Course</h2>
          <form id="form-add-course">
            <div className="form-inner">
              <div className="form-group">
                <label htmlFor="course-name">
                  Name:{" "}
                  <FontAwesomeIcon
                    icon={faInfoCircle}
                    title="The course name should have at least 3 characters."
                  />
                </label>
                <input
                  type="text"
                  name="course-name"
                  id="course-name"
                  onChange={(e) =>
                    this.setState((prevState) => ({
                      modalDetails: {
                        ...prevState.modalDetails,
                        courseName: e.target.value,
                      },
                    }))
                  }
                  value={this.state.modalDetails.courseName}
                />
              </div>
              <div className="form-group">
                <label htmlFor="number-chapters">
                  Number of chapters:{" "}
                  <FontAwesomeIcon
                    icon={faInfoCircle}
                    title="You should have at least one chapter."
                  />
                </label>
                <input
                  type="number"
                  name="number-chapters"
                  id="number-chapters"
                  onChange={(e) =>
                    this.setState((prevState) => ({
                      modalDetails: {
                        ...prevState.modalDetails,
                        numberOfChapters: e.target.value,
                      },
                    }))
                  }
                  value={this.state.modalDetails.numberOfChapters}
                />
              </div>
              <div className="form-group">
                <label htmlFor="course-link">
                  Course Playlist Link:{" "}
                  <FontAwesomeIcon
                    icon={faInfoCircle}
                    title="It should be a valid HTTPS link. Please add here the link from your first chapter including the playlistId. E.g.: https://www.youtube.com/watch?v=VHbSopMyc4M&list=PLBlnK6fEyqRjKA_NuK9mHmlk0dZzuP1P5"
                  />
                </label>
                <input
                  type="text"
                  name="course-link"
                  id="course-link"
                  onChange={(e) =>
                    this.setState((prevState) => ({
                      modalDetails: {
                        ...prevState.modalDetails,
                        courseLink: e.target.value,
                      },
                    }))
                  }
                  value={this.state.modalDetails.courseLink}
                />
              </div>
              <div className="form-group">
                <label htmlFor="course-price">Price: </label>
                <input
                  type="number"
                  name="course-price"
                  id="course-price"
                  onChange={(e) =>
                    this.setState((prevState) => ({
                      modalDetails: {
                        ...prevState.modalDetails,
                        price: e.target.value,
                      },
                    }))
                  }
                  value={this.state.modalDetails.price}
                />
              </div>
              <div className="form-group">
                <label htmlFor="course-description">
                  Description:{" "}
                  <FontAwesomeIcon
                    icon={faInfoCircle}
                    title="The description should contain at least 25 characters."
                  />
                </label>
                <input
                  type="text"
                  name="course-description"
                  id="course-description"
                  onChange={(e) =>
                    this.setState((prevState) => ({
                      modalDetails: {
                        ...prevState.modalDetails,
                        description: e.target.value,
                      },
                    }))
                  }
                  value={this.state.modalDetails.description}
                />
              </div>

              <input
                type="submit"
                onClick={this.handleSubmit}
                value="ADD COURSE"
              />
            </div>
          </form>
        </Modal>
        <div className="common-body" id="common-body-courses">
          <div className="grid-container">{this.state.visibleCourses === 0 ? <p style={{marginTop: "25px"}}>No courses available.</p> : this.state.courses }</div>
        </div>
      </div>
    );
  }
}

export default CoursesPage;
