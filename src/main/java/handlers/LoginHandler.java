package handlers;

import dataaccess.DataAccess;
import dataaccess.exceptions.InvalidPasswordException;
import dataaccess.exceptions.UserNotFoundException;
import payloads.UserAuthentication;
import utils.Answer;
import utils.Json;

import java.util.Map;

public class LoginHandler extends AbstractHandler<UserAuthentication> {
    private final DataAccess dataAccess;

    public LoginHandler(DataAccess dataAccess) {
        super(UserAuthentication.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(UserAuthentication authentication, Map<String, String> requestParams) {
        try {
            dataAccess.login(authentication);
            return new Answer(200, Json.custom("token", "hash"));
        } catch (UserNotFoundException e) {
            return Answer.withMessage(470, "User not found");
        } catch (InvalidPasswordException e) {
            return Answer.withMessage(480, "Password not valid");
        }
    }
}
