import axios from 'axios';

const USER_REGISTER_API_URL = "http://localhost:8080/api/v1/register";

class RegisterService {
    submitRegisterForm(user) {
        return axios.post(USER_REGISTER_API_URL, 
            {
                firstName: user.firstName,
                lastName: user.lastName,
                email: user.email,
                password: user.password,
                gender: user.gender,
                role: user.role
            }
        );
    }
}

export default new RegisterService();