package unit.payloads;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import payloads.UserAuthentication;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class UserAuthentication_ {
    private Map<String, String[]> parameters;
    private UserAuthentication authentication;

    @Test
    public void valid_if_hasUsernameAndPassword() {
        parameters.put("username", new String[]{"name"});
        parameters.put("password", new String[]{"anyPass"});
        assertThat(authentication.isValid()).isTrue();
    }

    @Test
    public void invalid_if_hasNoPassword() {
        parameters.put("username", new String[]{"name"});
        assertThat(authentication.isValid()).isFalse();
    }

    @Test
    public void getUsername_should_returnTheUsername() {
        parameters.put("username", new String[]{"name"});
        assertThat(authentication.getUsername()).isEqualTo("name");
    }

    @Test
    public void getPassword_should_returnThePassword() {
        parameters.put("password", new String[]{"anyPass"});
        assertThat(authentication.getPassword()).isEqualTo("anyPass");
    }

    @BeforeEach
    public void setUp() {
        parameters = new HashMap<>();
        authentication = new UserAuthentication(parameters);
    }
}
