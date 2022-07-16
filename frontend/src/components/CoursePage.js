import React from "react";
import NavigationBar from "./NavigationBar";
import { toast } from "react-toastify";
import CourseService from "../service/CourseService";
import "../styles/CoursePage.css";
import Modal from "react-modal";
import update from "react-addons-update";

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faWindowClose } from "@fortawesome/fontawesome-free-solid";
import { faInfoCircle } from "@fortawesome/fontawesome-free-solid";
import { toHaveStyle } from "@testing-library/jest-dom/dist/matchers";

const YOUTUBE_PLAYLIST_ITEMS_API =
  "https://www.googleapis.com/youtube/v3/playlistItems";

export async function getPlaylist() {}

class CoursePage extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      body: [],
      isCourseValid: false,
      isCourseCompleted: false,
      isCertificationObtained: false,
      numberOfChapters: 0,
      numberOfCompletedChapters: 0,
      existsAssessment: false,
      instrutorEmail: "",
      courseId: "",
      assessmentModal: [],
      modalQuestions: ["Question 1"],
      numberOfQuestions: 1,
      modalQuestionsTexts: ["", "", "", "", ""],
      showCreateAssessmentModal: false,
      showTakeAssessmentModal: false,
      showEditCourseModal: false,
      questions: [""],
      questionsTexts: ["", "", "", "", ""],
      isAssessmentTaken: false,
      isCourseVisible: false,
      playlistName: "",
      description: "",
      price: 0.0,
      courseName: "",
      courseDetails: {
        numberOfChapters: 0,
        courseLink: "",
        courseName: "",
        price: 0.0,
        description: "",
      },
    };
  }

  async componentDidMount() {
    Modal.setAppElement("body");

    const playlistUrl = window.location.pathname.split("/")[2];
    const playlistId = playlistUrl.split("=")[1].split("&")[0];
    const playlistName = playlistUrl.split("=")[3].replace("-", " ");
    const chapters = playlistUrl.split("=")[2].split("&")[0];

    this.setState({
      numberOfChapters: chapters,
      courseId: playlistId,
      playlistName: playlistName,
    });

    await this.checkValidPlaylist(playlistId);
    if (this.state.isCourseValid === true) {
      await CourseService.getCourseAvailability(
        localStorage.getItem("token"),
        playlistId
      )
        .then((res) => {
          this.setState({
            isCourseVisible: res.data,
          });
        })
        .catch((ex) => {});

      await CourseService.retrieveInstructorEmail(
        localStorage.getItem("token"),
        playlistId
      )
        .then((res) => {
          this.setState({
            instrutorEmail: res.data,
          });
        })
        .catch((ex) => {});

      if (
        this.state.isCourseVisible === true ||
        this.state.instrutorEmail === localStorage.getItem("user")
      ) {
        await this.renderData(playlistId, playlistName, chapters);

        await CourseService.isAssessmentTaken(
          localStorage.getItem("token"),
          localStorage.getItem("user"),
          playlistId
        )
          .then((res) => {
            this.setState({
              isAssessmentTaken: res.data,
            });
          })
          .catch((ex) => {});

        if (this.state.isAssessmentTaken === false) {
          await CourseService.retrieveQuestions(
            localStorage.getItem("token"),
            playlistId
          )
            .then((res) => {
              this.setState({
                questions: res.data,
                numberOfQuestions: res.data.length,
              });
            })
            .catch((ex) => {});
        }

        await CourseService.checkIfCertificationIsObtained(
          localStorage.getItem("user"),
          localStorage.getItem("token"),
          playlistId
        ).then((res) => {
          this.setState({
            isCertificationObtained: res.data,
          });

          if (
            this.state.isCertificationObtained === true ||
            this.state.isAssessmentTaken === true
          ) {
            document.getElementById("take-assessment").style.display = "none";
          }
        });
        await this.state.body?.props?.children[2]?.props?.children?.forEach(
          async (element) => {
            await CourseService.checkCompletedChapters(
              localStorage.getItem("user"),
              localStorage.getItem("token"),
              element.key,
              playlistId
            ).then((res) => {
              if (res.data === false) {
                document.getElementById("complete" + element.key).textContent =
                  "Mark as Complete";
              } else {
                document.getElementById("complete" + element.key).textContent =
                  "Mark as Incomplete";
                this.state.numberOfCompletedChapters++;
                if (
                  this.state.numberOfChapters ==
                    this.state.numberOfCompletedChapters &&
                  this.state.isCertificationObtained === false &&
                  this.state.isAssessmentTaken === false
                ) {
                  document.getElementById("take-assessment").style.display =
                    "block";
                }
              }
            });
          }
        );

        await CourseService.checkIfAssessmentExistsForCourse(
          localStorage.getItem("token"),
          playlistId
        )
          .then((res) => {
            if (res.data.questions.length > 0) {
              this.setState({
                existsAssessment: true,
              });
            }
          })
          .catch((ex) => {});

        if (
          this.state.existsAssessment === false &&
          this.state.instrutorEmail === localStorage.getItem("user")
        ) {
          document.getElementById("create-assessment").style.display = "block";
          if (this.state.isCourseValid === true) {
            document.getElementById("edit-course").style.right = "380px";
          } else {
            document.getElementById("edit-course").style.right = "388px";
          }
        } else {
          document.getElementById("create-assessment").style.display = "none";
          document.getElementById("set-availability").style.right = "15px";
          document.getElementById("edit-course").style.right = "188px";
        }

        if (!this.state.isCourseVisible) {
          document.getElementById("edit-course").style.right =
            parseInt(
              document
                .getElementById("edit-course")
                .style.right.split("px")[0] - 18
            ) + "px";
        }

        if (this.state.instrutorEmail === localStorage.getItem("user")) {
          document.getElementById("set-availability").style.display = "block";

          await CourseService.getCourseByCourseId(
            localStorage.getItem("token"),
            playlistId
          )
            .then((res) => {
              this.state.courseDetails.courseLink = res.data.courseYoutubeId;
              this.state.courseDetails.courseName = res.data.courseName;
              this.state.courseDetails.numberOfChapters =
                res.data.numberOfChapters;
              this.state.courseDetails.price = res.data.price;
              this.state.courseDetails.description = res.data.description;

              this.state.courseName = res.data.courseName;
              this.state.description = res.data.description;
              this.state.price = res.data.price;

              if (chapters != res.data.numberOfChapters) {
                toast.error("Invalid number of chapters required!", {
                  position: "top-right",
                  autoClose: 5000,
                  hideProgressBar: false,
                  closeOnClick: true,
                  pauseOnHover: true,
                  draggable: true,
                  progress: undefined,
                });
                setTimeout(function () {
                  window.location.href = "/courses";
                }, 2500);
              }
            })
            .catch((ex) => {
              toast.error("Course unavailable with id: " + playlistId, {
                position: "top-right",
                autoClose: 5000,
                hideProgressBar: false,
                closeOnClick: true,
                pauseOnHover: true,
                draggable: true,
                progress: undefined,
              });
              setTimeout(function () {
                window.location.href = "/courses";
              }, 2500);
            });
        } else {
          document.getElementById("set-availability").style.display = "none";
        }
      } else {
        toast.error(
          "Course with id: " + playlistId + " is not available anymore.",
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
          window.location.href = "/courses";
        }, 2500);
      }
    } else {
      toast.error("Course unavailable with id: " + playlistId, {
        position: "top-right",
        autoClose: 5000,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
      });
      setTimeout(function () {
        window.location.href = "/courses";
      }, 2500);
    }
  }

  componentDidUpdate() {}

  checkValidPlaylist = async (playlistId) => {
    await CourseService.checkValidPlaylist(
      playlistId,
      localStorage.getItem("token")
    )
      .then((res) => {
        this.setState({
          isCourseValid: res.data ? true : false,
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
  };

  editCourse = async (event, courseId) => {
    event.preventDefault();

    let newCourseId = this.state.courseDetails.courseLink
      .split("&")[1]
      .split("=")[1];
    console.log(this.state);
    if (
      this.state.price === this.state.courseDetails.price &&
      this.state.description === this.state.courseDetails.description &&
      parseInt(this.state.numberOfChapters) ===
        this.state.courseDetails.numberOfChapters &&
      this.state.courseName === this.state.courseDetails.courseName &&
      this.state.courseId === newCourseId
    ) {
      toast.success("Nothing has been changed.", {
        position: "top-right",
        autoClose: 5000,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
      });

      return;
    }

    var expression =
      /[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)?/gi;
    var regex = new RegExp(expression);
    if (
      this.state.courseDetails.courseLink.match(regex) &&
      this.state.courseDetails.courseName.length > 3 &&
      this.state.courseDetails.numberOfChapters > 0 &&
      this.state.courseDetails.description.length > 25 &&
      this.state.courseDetails.price >= 0
    ) {
      if (localStorage.getItem("token")) {
        await CourseService.editCourse(
          this.state.courseDetails,
          localStorage.getItem("token"),
          courseId
        )
          .then((res) => {
            toast.success("Course updated.", {
              position: "top-right",
              autoClose: 5000,
              hideProgressBar: false,
              closeOnClick: true,
              pauseOnHover: true,
              draggable: true,
              progress: undefined,
            });
            let loc = window.location.href.toString();
            loc = loc.replace(
              "chapters=" + this.state.numberOfChapters,
              "chapters=" + this.state.courseDetails.numberOfChapters
            );
            loc = loc.replace(
              loc.split("&")[2],
              "playlistName=" + this.state.courseDetails.courseName
            );
            loc = loc.replace(
              "playlistId=" + courseId,
              "playlistId=" +
                this.state.courseDetails.courseLink.split("&")[1].split("=")[1]
            );

            setTimeout(function () {
              window.location.href = loc;
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

  renderData = async (playlistId, playlistName, chapters) => {
    await fetch(
      `${YOUTUBE_PLAYLIST_ITEMS_API}?part=snippet&maxResults=${chapters}&playlistId=${playlistId}&key=${process.env.REACT_APP_API_YOUTUBE}`
    ).then(async (res) => {
      const data = await res.json();
      this.setState({
        body: (
          <main className="course-container">
            <h1 className="course-title" style={{ marginLeft: "10%" }}>
              {playlistName.replaceAll("%20", " ")}
            </h1>
            <button
              type="button"
              style={{
                display: "none",
              }}
              className=""
              id="set-availability"
              onClick={() => this.handleSetCourseAvailability(playlistId)}
            >
              {this.state.isCourseVisible
                ? "Make Unavailable"
                : "Make Available"}
            </button>
            <ul className="chapters">
              {data?.items?.map((item) => {
                const { id, snippet = {} } = item;
                const {
                  title,
                  thumbnails = {},
                  resourceId,
                  description,
                } = snippet;
                const { medium = {} } = thumbnails;

                const shortDescription = description.substring(0, 250);
                const dots = "...";
                const restOfDescription = description.substring(250);

                return (
                  <li key={resourceId.videoId} className="chapter-info">
                    <button
                      className="mark-complete"
                      id={"complete" + resourceId.videoId}
                      style={{
                        float: "right",
                        marginRight: "20%",
                        marginTop: "5%",
                      }}
                      onClick={() =>
                        this.handleCompleteChapter(
                          resourceId.videoId,
                          playlistId
                        )
                      }
                    >
                      Mark as Complete
                    </button>
                    <a
                      href={`https://www.youtube.com/watch?v=${resourceId.videoId}`}
                      target="_blank"
                    >
                      <h3>{title}</h3>
                      <p>
                        <img
                          width={medium.width}
                          height={medium.height}
                          src={medium.url}
                          alt=""
                        />
                      </p>
                    </a>
                    <p style={{ display: "inline-block" }}>
                      {shortDescription}
                      <span
                        id={"dots" + id}
                        onClick={() => this.handleReadMode(id)}
                      >
                        {dots}
                      </span>
                      <span
                        id={"more" + id}
                        style={{ display: "none" }}
                        onClick={() => this.handleReadMode(id)}
                      >
                        {restOfDescription}
                      </span>
                    </p>
                  </li>
                );
              })}
            </ul>
          </main>
        ),
      });
    });
  };

  async handleSetCourseAvailability(courseId) {
    await CourseService.setCourseAvailability(
      this.state.isCourseVisible,
      localStorage.getItem("token"),
      courseId
    )
      .then(() => {
        let message = this.state.isCourseVisible
          ? "Course is now unavailble"
          : "Course is now available";
        toast.success(message, {
          position: "top-right",
          autoClose: 5000,
          hideProgressBar: false,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
        });
        this.setState({
          isCourseVisible: !this.state.isCourseVisible,
        });
        setTimeout(function () {
          window.location.reload(false);
        }, 2500);
      })
      .catch();
  }

  async handleCompleteChapter(chapterUrl, courseYoutubeId) {
    if (
      document.getElementById("complete" + chapterUrl).textContent ===
      "Mark as Complete"
    ) {
      await CourseService.completeChapter(
        localStorage.getItem("user"),
        localStorage.getItem("token"),
        chapterUrl,
        courseYoutubeId
      )
        .then(() => {
          document.getElementById("complete" + chapterUrl).textContent =
            "Mark as Incomplete";
          this.state.numberOfCompletedChapters++;
          if (
            this.state.numberOfChapters == this.state.numberOfCompletedChapters
          ) {
            this.setState({
              isCourseCompleted: true,
            });
            if (this.state.isCertificationObtained === false) {
              document.getElementById("take-assessment").style.display =
                "block";
            }
          }
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
      await CourseService.incompleteChapter(
        localStorage.getItem("user"),
        localStorage.getItem("token"),
        chapterUrl,
        courseYoutubeId
      )
        .then(() => {
          document.getElementById("complete" + chapterUrl).textContent =
            "Mark as Complete";
          this.state.numberOfCompletedChapters--;
          if (
            document.getElementById("take-assessment").style.display === "block"
          ) {
            document.getElementById("take-assessment").style.display = "none";
          }
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
  }

  handleReadMode(id) {
    let moreText = document.getElementById("more" + id);
    let dots = document.getElementById("dots" + id);

    if (moreText.style.display === "none") {
      dots.style.display = "none";
      moreText.style.display = "inline";
    } else {
      moreText.style.display = "none";
      dots.style.display = "inline";
    }
  }

  async handleCompleteCourse() {
    if (
      this.state.existsAssessment === true &&
      this.state.isAssessmentTaken === false
    ) {
      this.setState({ showTakeAssessmentModal: true });
    } else {
      await CourseService.completeCourse(
        localStorage.getItem("user"),
        localStorage.getItem("token"),
        this.state.courseId
      )
        .then(() => {
          toast.success(
            "Congratulations! Certification obtained, check your profile page for details!",
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
            window.location.href = "/courses";
          }, 2500);
        })
        .catch();
    }
  }

  handleAddQuestion(e) {
    e.preventDefault();

    if (this.state.numberOfQuestions < 5) {
      this.state.numberOfQuestions++;
      this.setState((prevState) => ({
        modalQuestions: [
          ...prevState.modalQuestions,
          "Questions " + this.state.numberOfQuestions,
        ],
        modalQuestionsTexts: [...prevState.modalQuestionsTexts, ""],
      }));
      if (this.state.numberOfQuestions === 5) {
        document.getElementById("add-question").style.display = "none";
        document.getElementById("add-assessment").style.marginLeft = "20%";
      }
    }
  }

  handleDeleteQuestion() {
    if (this.state.numberOfQuestions > 1) {
      this.state.numberOfQuestions--;
      this.setState({
        modalQuestions: this.state.modalQuestions.filter(
          (_, i) => i !== this.state.numberOfQuestions
        ),
      });
    }
  }

  async handleSubmitCreateAssessment(e) {
    e.preventDefault();
    let questions = [];
    for (let i = 0; i <= this.state.numberOfQuestions - 1; i++) {
      if (
        this.state.modalQuestionsTexts[i] === "" ||
        this.state.modalQuestionsTexts[i].length < 15
      ) {
        toast.error(
          "Question " + (i + 1) + " is empty or has less than 15 characters!",
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
        return;
      } else {
        questions.push(
          this.state.modalQuestions[i] +
            "##" +
            this.state.modalQuestionsTexts[i]
        );
      }
    }

    await CourseService.addAssessment(
      localStorage.getItem("token"),
      this.state.courseId,
      questions
    )
      .then(() => {
        this.setState({
          showCreateAssessmentModal: false,
        });
        toast.success("Assessment created!", {
          position: "top-right",
          autoClose: 5000,
          hideProgressBar: false,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
        });
        setTimeout(function () {
          window.location.reload(false);
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
  }

  async handleSubmitAssessment(event) {
    event.preventDefault();
    let resultQuestions = [];
    for (let i = 0; i <= this.state.numberOfQuestions - 1; i++) {
      resultQuestions.push(
        this.state.questions[i] + "##" + this.state.questionsTexts[i]
      );
    }

    await CourseService.submitAssessment(
      localStorage.getItem("token"),
      this.state.courseId,
      resultQuestions,
      localStorage.getItem("user")
    )
      .then(() => {
        this.setState({
          showTakeAssessmentModal: false,
        });
        toast.success("Assessment submitted!", {
          position: "top-right",
          autoClose: 5000,
          hideProgressBar: false,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
        });
        setTimeout(function () {
          window.location.reload(false);
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
  }

  render() {
    return (
      <div className="common-body">
        <NavigationBar logout={this.props.logout} />
        {this.state.body}
        <button
          type="button"
          id="take-assessment"
          style={{
            display: "none",
          }}
          onClick={this.handleCompleteCourse.bind(this)}
        >
          {this.state.existsAssessment === true
            ? "Take assessment"
            : "Complete course"}
        </button>
        <button
          type="button"
          style={{
            display: "none",
          }}
          id="create-assessment"
          onClick={() => this.setState({ showCreateAssessmentModal: true })}
        >
          Create Assessment
        </button>
        <button
          type="button"
          style={{
            display:
              this.state.instrutorEmail === localStorage.getItem("user")
                ? "block"
                : "none",
          }}
          id="edit-course"
          onClick={() => {
            if (this.state.isCourseVisible) {
              toast.error(
                "You have to make the course unvailable in order to edit it.",
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
              return;
            }
            this.setState({ showEditCourseModal: true });
          }}
        >
          Edit Course
        </button>

        <Modal
          isOpen={this.state.showCreateAssessmentModal}
          onRequestClose={() =>
            this.setState({ showCreateAssessmentModal: false })
          }
          contentLabel="ADD ASSESSMENT"
          className="add-course"
          overlayClassName="myoverlay"
          closeTimeoutMS={500}
        >
          <h2 id="header-form">Add Assessment</h2>
          <form id="form-add-course">
            <div className="form-inner" id="form-body">
              {this.state.modalQuestions.map((elem, key) => (
                <div className="form-group">
                  <label htmlFor="course-description">
                    {elem}:{" "}
                    <FontAwesomeIcon
                      id="delete-question"
                      icon={faWindowClose}
                      title="Delete question."
                      onClick={this.handleDeleteQuestion.bind(this)}
                    />
                  </label>
                  <input
                    type="text"
                    id={"question-" + elem.split(" ")[1]}
                    name="course-description"
                    onChange={(e) => {
                      this.setState(
                        update(this.state, {
                          modalQuestionsTexts: {
                            [elem.split(" ")[1] - 1]: {
                              $set: e.target.value,
                            },
                          },
                        })
                      );
                    }}
                    value={
                      this.state.modalQuestionsTexts[elem.split(" ")[1] - 1]
                    }
                  />
                </div>
              ))}
              <input
                id="add-question"
                type="submit"
                onClick={this.handleAddQuestion.bind(this)}
                value="ADD QUESTION"
              />
              &nbsp;&nbsp;&nbsp;
              <input
                id="add-assessment"
                type="submit"
                onClick={this.handleSubmitCreateAssessment.bind(this)}
                value="ADD ASSESSMENT"
              />
            </div>
          </form>
        </Modal>

        <Modal
          isOpen={this.state.showTakeAssessmentModal}
          onRequestClose={() =>
            this.setState({ showTakeAssessmentModal: false })
          }
          contentLabel="TAKE ASSESSMENT"
          className="add-course"
          overlayClassName="myoverlay"
          closeTimeoutMS={500}
        >
          <h2 id="header-form">Take Assessment</h2>
          <form id="form-add-course">
            <div className="form-inner" id="form-body">
              {this.state.questions.map((elem) => (
                <div className="form-group">
                  <label htmlFor="course-description">
                    {<a style={{ color: "red" }}>{elem.split("##")[0]}:</a>}{" "}
                    {elem.split("##")[1]}
                  </label>
                  <input
                    type="text"
                    id={"question-" + elem.split("##")[0].split(" ")[1]}
                    name="course-description"
                    onChange={(e) => {
                      this.setState(
                        update(this.state, {
                          questionsTexts: {
                            [elem.split("##")[0].split(" ")[1] - 1]: {
                              $set: e.target.value,
                            },
                          },
                        })
                      );
                    }}
                    value={
                      this.state.questionsTexts[
                        elem.split("##")[0].split(" ")[1] - 1
                      ]
                    }
                  />
                </div>
              ))}
              <input
                id="submit-assessment"
                type="submit"
                style={{ marginLeft: "20%" }}
                onClick={this.handleSubmitAssessment.bind(this)}
                value="SUBMIT ASSESSMENT"
              />
            </div>
          </form>
        </Modal>

        <Modal
          isOpen={this.state.showEditCourseModal}
          onRequestClose={() => this.setState({ showEditCourseModal: false })}
          contentLabel="EDIT COURSE"
          className="edit-course"
          overlayClassName="myoverlay"
          closeTimeoutMS={500}
        >
          <h2 id="header-form">Edit Course</h2>
          <form id="form-edit-course">
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
                      courseDetails: {
                        ...prevState.courseDetails,
                        courseName: e.target.value,
                      },
                    }))
                  }
                  value={this.state.courseDetails.courseName}
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
                      courseDetails: {
                        ...prevState.courseDetails,
                        numberOfChapters: e.target.value,
                      },
                    }))
                  }
                  value={this.state.courseDetails.numberOfChapters}
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
                      courseDetails: {
                        ...prevState.courseDetails,
                        courseLink: e.target.value,
                      },
                    }))
                  }
                  value={this.state.courseDetails.courseLink}
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
                      courseDetails: {
                        ...prevState.courseDetails,
                        price: e.target.value,
                      },
                    }))
                  }
                  value={this.state.courseDetails.price}
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
                      courseDetails: {
                        ...prevState.courseDetails,
                        description: e.target.value,
                      },
                    }))
                  }
                  value={this.state.courseDetails.description}
                />
              </div>

              <input
                type="submit"
                onClick={(event) => this.editCourse(event, this.state.courseId)}
                value="EDIT COURSE"
              />
            </div>
          </form>
        </Modal>
      </div>
    );
  }
}

export default CoursePage;
