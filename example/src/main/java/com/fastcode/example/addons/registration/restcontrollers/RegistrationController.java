package com.fastcode.example.addons.registration.restcontrollers;

import com.fastcode.example.addons.email.application.mail.IEmailService;
import com.fastcode.example.application.core.authorization.tokenverification.ITokenVerificationAppService;
import com.fastcode.example.application.core.authorization.users.IUsersAppService;
import com.fastcode.example.application.core.authorization.users.dto.*;
import com.fastcode.example.commons.logging.LoggingHelper;
import com.fastcode.example.domain.core.authorization.tokenverification.Tokenverification;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
public class RegistrationController {

    @Autowired
    private PasswordEncoder pEncoder;

    @Autowired
    private ITokenVerificationAppService _tokenAppService;

    @Autowired
    @Qualifier("usersAppService")
    private IUsersAppService _usersAppService;

    @Autowired
    private LoggingHelper logHelper;

    @Autowired
    private IEmailService _emailService;

    public static final long ACCOUNT_VERIFICATION_TOKEN_EXPIRATION_TIME = 86_400_000;

    @RequestMapping(method = RequestMethod.POST, consumes = { "application/json" }, produces = { "application/json" })
    public ResponseEntity<HashMap<String, String>> registerUserAccount(
        @RequestBody CreateUsersInput users,
        @RequestParam("clientUrl") final String clientUrl
    ) {
        FindUsersByUsernameOutput foundUsers = _usersAppService.findByUsername(users.getUsername());

        if (foundUsers != null) {
            logHelper.getLogger().error("There already exists a users with a Username=%s", users.getUsername());
            throw new EntityExistsException(
                String.format("There already exists a users with Username =%s", users.getUsername())
            );
        }
        foundUsers = _usersAppService.findByEmailaddress(users.getEmailaddress());

        if (foundUsers != null) {
            logHelper.getLogger().error("There already exists a users with a email =%s", users.getEmailaddress());
            throw new EntityExistsException(
                String.format("There already exists a users with email =%s", users.getEmailaddress())
            );
        }

        users.setPassword(pEncoder.encode(users.getPassword()));
        users.setIsactive(true);
        users.setIsemailconfirmed(false);

        CreateUsersOutput output = _usersAppService.create(users);
        Optional.ofNullable(output).orElseThrow(() -> new EntityNotFoundException(String.format("No record found")));

        sendVerificationEmail(clientUrl, output.getEmailaddress(), output.getId());
        String msg = "Account verfication link has been sent to " + users.getEmailaddress();
        HashMap resultMap = new HashMap<String, String>();
        resultMap.put("message", msg);

        return new ResponseEntity(resultMap, HttpStatus.OK);
    }

    @RequestMapping(
        value = "/verifyEmail",
        method = RequestMethod.POST,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<HashMap<String, String>> verifyEmail(@RequestParam("token") final String token) {
        Tokenverification tokenEntity = _tokenAppService.findByTokenAndType(token, "registration");

        Optional
            .ofNullable(tokenEntity)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Invalid verification link.")));

        FindUsersWithAllFieldsByIdOutput output = _usersAppService.findWithAllFieldsById(tokenEntity.getUsersId());
        Optional
            .ofNullable(output)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Invalid verification link.")));

        if (new Date().after(tokenEntity.getExpirationTime())) {
            _tokenAppService.deleteToken(tokenEntity);
            _usersAppService.delete(tokenEntity.getUsersId());

            logHelper.getLogger().error("Token has expired, please register again");
            throw new EntityNotFoundException(String.format("Token has expired, please register again"));
        }

        output.setIsemailconfirmed(true);
        _usersAppService.updateUsersData(output);
        _tokenAppService.deleteToken(tokenEntity);

        String msg = "Users Verified!";
        HashMap resultMap = new HashMap<String, String>();
        resultMap.put("message", msg);
        return new ResponseEntity(resultMap, HttpStatus.OK);
    }

    @RequestMapping(
        value = "/resendVerificationEmail/{username}",
        method = RequestMethod.POST,
        consumes = { "application/json" },
        produces = { "application/json" }
    )
    public ResponseEntity<HashMap<String, String>> resendVerificationEmail(
        @PathVariable final String username,
        @RequestParam("clientUrl") final String clientUrl
    ) {
        FindUsersByUsernameOutput foundUsers = _usersAppService.findByUsername(username);

        if (foundUsers == null) {
            logHelper.getLogger().error("There does not exist a users with Username=%s", foundUsers.getUsername());
            throw new EntityExistsException(
                String.format("There does not exist a users with Username =%s", foundUsers.getUsername())
            );
        }

        if (foundUsers != null && Boolean.TRUE.equals(foundUsers.getIsemailconfirmed())) {
            logHelper.getLogger().error("Users with Username=%s is already verified.", username);
            throw new EntityExistsException(String.format("Users with Username=%s is already verified.", username));
        }

        sendVerificationEmail(clientUrl, foundUsers.getEmailaddress(), foundUsers.getId());

        String msg = "Account verfication link has been resent to " + foundUsers.getEmailaddress();

        HashMap<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("message", msg);
        return new ResponseEntity(resultMap, HttpStatus.OK);
    }

    private void sendVerificationEmail(String clientUrl, String emailAddress, Long usersId) {
        Tokenverification tokenEntity = _tokenAppService.generateToken("registration", usersId);

        String subject = "Account Verfication";
        String emailText =
            "To verify your account, click the link below:\n" +
            clientUrl +
            "/verify-email?token=" +
            tokenEntity.getToken();

        _emailService.sendEmail(_emailService.buildEmail(emailAddress, subject, emailText));
    }
}
