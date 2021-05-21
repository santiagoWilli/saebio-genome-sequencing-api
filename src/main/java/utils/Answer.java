package utils;

import java.io.InputStream;
import java.util.Objects;

public class Answer {
    private final int code;
    private final String body;
    private final AnswerFile file;

    public Answer(int code, String body){
        this.code = code;
        this.body = body;
        this.file = null;
    }

    private Answer(AnswerFile file) {
        this.code = 200;
        this.body = null;
        this.file = file;
    }

    public static Answer badRequest(String message) {
        return new Answer(400, Json.message(message));
    }

    public static Answer serviceUnavailable(String message) {
        return new Answer(503, Json.message(message));
    }

    public static Answer badGateway(String message) {
        return new Answer(502, Json.message(message));
    }

    public static Answer serverError(String message) {
        return new Answer(500, Json.message(message));
    }

    public static Answer notFound() {
        return new Answer(404, "The specified resource could not be found");
    }

    public static Answer withFile(InputStream file, String mimeType) {
        return new Answer(new AnswerFile(file, mimeType));
    }

    public static Answer withMessage(int code, String message) {
        return new Answer(code, Json.message(message));
    }

    public int getCode() {
        return code;
    }

    public String getBody() {
        return body;
    }

    public AnswerFile getFile() {
        return file;
    }

    public boolean hasFile() {
        return file != null;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Answer answer = (Answer) object;
        if (code != answer.code) return false;
        return Objects.equals(body, answer.body);
    }

    @Override
    public String toString() {
        return "Answer (code=" + code + ", body=" + body + ")";
    }
}
