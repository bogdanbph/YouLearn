import axios from "axios";

const USER_SERVICE_API_URL = "http://localhost:8080/api/v1/user";

class UserService {
  retrieveUserProfile(email, token) {
    return axios.post(
      USER_SERVICE_API_URL,
      new URLSearchParams({
        email: email,
      }),
      {
        headers: {
          Authorization: "Bearer " + token,
        },
      }
    );
  }

  retrieveRoleForUser(email, token) {
    return axios.post(
      USER_SERVICE_API_URL + "/role",
      new URLSearchParams({
        email: email,
      }),
      {
        headers: {
          Authorization: "Bearer " + token,
        },
      }
    );
  }
}

export default new UserService();
