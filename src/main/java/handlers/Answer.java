package handlers;

import java.util.Objects;

public class Answer {
    private final int code;
    private final String body;

    public Answer(int code, String body){
        this.code = code;
        this.body = body;
    }

    public static Answer badRequest(String message) {
        return new Answer(400, "{\"message\":\"" + message + "\"}");
    }

    public static Answer serviceUnavailable() {
        return new Answer(503, null);
    }

    public static Answer badGateway() {
        return new Answer(1, null);
    }

    public int getCode() {
        return code;
    }

    public String getBody() {
        return body;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Answer answer = (Answer) object;
        if (code != answer.code) return false;
        return Objects.equals(body, answer.body);
    }
}
