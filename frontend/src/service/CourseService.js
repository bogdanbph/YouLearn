import axios from "axios";

const COURSE_API_URL = "http://localhost:8080/api/v1/course";
const ASSESSMENT_API_URL = "http://localhost:8080/api/v1/assessment";

class CourseService {
  retrieveCourses(token) {
    return axios.get(COURSE_API_URL, {
      headers: {
        Authorization: "Bearer " + token,
      },
    });
  }

  uploadCourse(course, token) {
    return axios.post(
      COURSE_API_URL + "/new",
      {
        emailInstructor: course.email,
        numberOfChapters: course.numberOfChapters,
        courseYoutubeId: course.courseLink,
        courseName: course.courseName,
        price: course.price,
        description: course.description,
      },
      {
        headers: {
          Authorization: "Bearer " + token,
        },
      }
    );
  }

  checkEnrolled(courseId, user, token) {
    return axios.get(COURSE_API_URL + "/enrolled", {
      params: {
        courseId: courseId,
        user: user,
      },
      headers: {
        Authorization: "Bearer " + token,
      },
    });
  }

  enroll(courseId, user, token) {
    return axios.post(
      COURSE_API_URL + "/enroll",
      {
        courseId: courseId,
        email: user,
      },
      {
        headers: {
          Authorization: "Bearer " + token,
        },
      }
    );
  }

  checkValidPlaylist(playlistId, token) {
    return axios.get(COURSE_API_URL + "/" + playlistId, {
      headers: {
        Authorization: "Bearer " + token,
      },
    });
  }

  completeChapter(email, token, chapterUrl, courseId) {
    return axios.post(
      COURSE_API_URL + "/chapter/complete",
      {
        email: email,
        chapterUrl: chapterUrl,
        courseYoutubeId: courseId,
      },
      {
        headers: {
          Authorization: "Bearer " + token,
        },
      }
    );
  }

  incompleteChapter(email, token, chapterUrl, courseId) {
    return axios.delete(COURSE_API_URL + "/chapter/incomplete", {
      headers: {
        Authorization: "Bearer " + token,
      },
      params: {
        email: email,
        chapterUrl: chapterUrl,
        courseYoutubeId: courseId,
      },
    });
  }

  checkCompletedChapters(email, token, chapterUrl, courseId) {
    return axios.post(
      COURSE_API_URL + "/chapter/completed",
      {
        email: email,
        chapterUrl: chapterUrl,
        courseYoutubeId: courseId,
      },
      {
        headers: {
          Authorization: "Bearer " + token,
        },
      }
    );
  }

  checkIfAssessmentExistsForCourse(token, courseId) {
    return axios.get(ASSESSMENT_API_URL + "?courseId=" + courseId, {
      headers: {
        Authorization: "Bearer " + token,
      },
    });
  }

  retrieveInstructorEmail(token, courseId) {
    return axios.get(COURSE_API_URL + "/instructor?courseId=" + courseId, {
      headers: {
        Authorization: "Bearer " + token,
      },
    });
  }

  completeCourse(email, token, courseId) {
    return axios.post(
      COURSE_API_URL + "/complete?courseId=" + courseId,
      {
        email: email,
      },
      {
        headers: {
          Authorization: "Bearer " + token,
        },
      }
    );
  }

  checkIfCertificationIsObtained(email, token, courseId) {
    return axios.post(
      COURSE_API_URL + "/certification?courseId=" + courseId,
      {
        email: email,
      },
      {
        headers: {
          Authorization: "Bearer " + token,
        },
      }
    );
  }

  addAssessment(token, courseId, questions) {
    return axios.post(
      ASSESSMENT_API_URL + "/new",
      {
        courseId: courseId,
        questions: questions,
      },
      {
        headers: {
          Authorization: "Bearer " + token,
        },
      }
    );
  }

  retrieveQuestions(token, courseId) {
    return axios.get(ASSESSMENT_API_URL + "/questions?courseId=" + courseId, {
      headers: {
        Authorization: "Bearer " + token,
      },
    });
  }

  submitAssessment(token, courseId, questions, email) {
    return axios.post(
      ASSESSMENT_API_URL + "/submit",
      {
        courseId: courseId,
        questions: questions,
        email: email,
      },
      {
        headers: {
          Authorization: "Bearer " + token,
        },
      }
    );
  }

  isAssessmentTaken(token, email, courseId) {
    return axios.post(
      COURSE_API_URL + "/assessment?courseId=" + courseId,
      {
        email: email,
      },
      {
        headers: {
          Authorization: "Bearer " + token,
        },
      }
    );
  }

  setCourseAvailability(isAvailable, token, courseId) {
    return axios.post(
      COURSE_API_URL + "/availability?courseId=" + courseId + "&isAvailable=" + isAvailable,
      {},
      {
        headers: {
          Authorization: "Bearer " + token,
        },
      }
    );
  }
  
  getCourseAvailability(token, courseId) {
    return axios.get(COURSE_API_URL + "/availability?courseId=" + courseId, {
      headers: {
        Authorization: "Bearer " + token,
      },
    });
  }
}

export default new CourseService();
