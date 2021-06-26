package handlers;

import dataaccess.DataAccess;
import dataaccess.exceptions.UserNotFoundException;
import payloads.UserAuthentication;
import utils.Answer;
import utils.JWT;
import utils.Json;
import utils.RequestParams;

import java.util.AbstractMap;
import java.util.Map;

public class LoginHandler extends AbstractHandler<UserAuthentication> {
    private final DataAccess dataAccess;

    public LoginHandler(DataAccess dataAccess) {
        super(UserAuthentication.class);
        this.dataAccess = dataAccess;
    }

    @Override
    protected Answer processRequest(UserAuthentication authentication, RequestParams requestParams) {
        try {
            return dataAccess.login(authentication) ?
                    new Answer(200, Json.custom(Map.ofEntries(
                            new AbstractMap.SimpleEntry<>("token", JWT.generate()),
                            new AbstractMap.SimpleEntry<>("expiresAfter", String.valueOf(JWT.LEEWAY))))) :
                    Answer.withMessage(480, "Password not valid");
        } catch (UserNotFoundException e) {
            return Answer.withMessage(470, "User not found");
        }
    }
}
