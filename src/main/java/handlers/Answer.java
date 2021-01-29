package handlers;

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

    public int getCode() {
        return code;
    }

    public String getBody() {
        return body;
    }
}
