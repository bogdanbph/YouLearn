import axios from 'axios';

const USER_LOGIN_API_URL = "http://localhost:8080/api/v1/login";

class LoginService {
    submitLoginForm(username, password) {
        return axios.post(USER_LOGIN_API_URL, 
            new URLSearchParams({
                username: username,
                password: password
            }));
    }
}

export default new LoginService();