import axios from 'axios';

const COURSE_API_URL = "http://localhost:8080/api/v1/course";

class CourseService {
    retrieveCourses(token) {
        return axios.get(COURSE_API_URL,
            {
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            });
    }

    uploadCourse(course, token) {
        return axios.post(COURSE_API_URL + "/new",
            {
                emailInstructor: course.email,
                numberOfChapters: course.numberOfChapters,
                courseYoutubeId: course.courseLink,
                courseName: course.courseName,
                price: course.price,
                description: course.description
            },
            {
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            });
    }

    checkEnrolled(courseId, user, token) {
        return axios.get(COURSE_API_URL + "/enrolled", {
            params: {
                courseId: courseId,
                user: user
            },
            headers: {
                'Authorization': 'Bearer ' + token
            }
        })
    }

    enroll(courseId, user, token) {
        return axios.post(COURSE_API_URL + "/enroll",
            {
                courseId: courseId,
                email: user
            },
            {
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            });
    }
}

export default new CourseService();