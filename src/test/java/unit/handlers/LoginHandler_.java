package unit.handlers;

import dataaccess.DataAccess;
import dataaccess.exceptions.*;
import handlers.LoginHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.UserAuthentication;
import utils.Answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoginHandler_ {
    private LoginHandler handler;
    private DataAccess dataAccess;
    private UserAuthentication authentication;

    @BeforeEach
    public void setUp() {
        dataAccess = mock(DataAccess.class);
        authentication = mock(UserAuthentication.class);
        when(authentication.isValid()).thenReturn(true);
        handler = new LoginHandler(dataAccess);
    }

    @Test
    public void if_userNotFound_return_470() throws UserNotFoundException, InvalidPasswordException {
        when(dataAccess.login(authentication)).thenThrow(UserNotFoundException.class);
        assertThat(handler.process(authentication, null).getCode()).isEqualTo(470);
    }

    @Test
    public void if_passwordNotValid_return_480() throws InvalidPasswordException, UserNotFoundException {
        when(dataAccess.login(authentication)).thenThrow(InvalidPasswordException.class);
        assertThat(handler.process(authentication, null).getCode()).isEqualTo(480);
    }

    @Test
    public void if_credentialsAreValid_return_http200_and_token() throws InvalidPasswordException, UserNotFoundException {
        when(dataAccess.login(authentication)).thenReturn("hash");
        Answer answer = handler.process(authentication, null);
        assertThat(answer.getCode()).isEqualTo(200);
        assertThat(answer.getBody()).contains("\"token\":");
    }
}
