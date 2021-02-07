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
        return new Answer(400, json(message));
    }

    public static Answer serviceUnavailable() {
        return new Answer(503, json("Genome reporter tool is down"));
    }

    public static Answer badGateway() {
        return new Answer(502, json("Genome reporter tool encountered an internal error"));
    }

    public int getCode() {
        return code;
    }

    public String getBody() {
        return body;
    }

    private static String json(String message) {
        return "{\"message\":\"" + message + "\"}";
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
