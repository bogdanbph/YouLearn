package com.youlearn.youlearn.service;

import com.youlearn.youlearn.dto.RegistrationRequest;
import com.youlearn.youlearn.exception.BadRequestException;
import com.youlearn.youlearn.model.Token;
import com.youlearn.youlearn.model.User;
import com.youlearn.youlearn.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final Logger logger = LogManager.getLogger(RegistrationService.class);

    private final EmailService emailService;
    private final UserService userService;
    private final TokenService tokenService;

    @Value("${server.port}")
    private String port;

    public String registerUser(RegistrationRequest request) {
        boolean isValid = emailService.test(request.getEmail());
        boolean isValidPassword = request.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$");

        if (!isValid) {
            throw new BadRequestException("Email format is not correct.");
        }

        if (!isValidPassword) {
            logger.error("Password format is not correct.");
            throw new BadRequestException("Password format is not correct.");
        }

        User user = new User(request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                UserRole.valueOf(request.getRole()),
                request.getGender());
        String tokenString = userService.signUp(user);

        String link = String.format("http://localhost:%s/api/v1/register/confirm?token=%s", port, tokenString);

        try {
            emailService.send(request.getEmail(), buildEmail(request.getFirstName(), link), "Confirm Email");
        }
        catch (Exception ex) {
            userService.deleteUser(user);
            throw new BadRequestException(ex.getMessage());
        }
        return tokenString;
    }

    @Transactional
    public void confirmToken(String tokenString) {
        Token token = tokenService.getToken(tokenString)
                .orElseThrow(() -> new BadRequestException("Token not found in database."));
        if (token.getConfirmedAt() != null) {
            throw new BadRequestException("The email is already confirmed.");
        }

        LocalDateTime expiredAt = token.getExpiredAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("The token is expired!");
        }

        tokenService.setConfrimedAt(tokenString);
        userService.enableUser(token.getUser().getEmail());

    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" style=\"background-image:linear-gradient(to bottom right, #ffce00, #FE4880)\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td align=\"center\" style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#000000;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#000000\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td align=\"center\" style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi <b>" + name + "</b>,</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p>" +
                "        \n" +
                "           <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\" style=\"font-size: 20px; font-family: Helvetica, Arial, sans-serif; border-radius: 25px; background-color: #FFA73B; text-decoration: none; color: #000000; text-decoration: none; padding: 15px 25px; border-radius: 2px; border: 1px solid #FFA73B; display: inline-block;\">Activate Now</a> </p>" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Link will expire in 30 minutes.</p> <p>See you soon, <br/><b>Team YouLearn</b></p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
}
