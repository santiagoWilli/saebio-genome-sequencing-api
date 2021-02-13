package genome;

import java.util.Objects;

public class GenomeToolAnswer {
    private final Status status;
    private final String message;

    public GenomeToolAnswer(Status status) {
        this.status = status;
        this.message = null;
    }

    public GenomeToolAnswer(Status status, String body){
        this.status = status;
        this.message = body;
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public enum Status {
        OK,
        API_DOWN,
        SERVER_ERROR,
        EXCEPTION_ENCOUNTERED;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        GenomeToolAnswer answer = (GenomeToolAnswer) object;
        if (status != answer.status) return false;
        return Objects.equals(message, answer.message);
    }

    @Override
    public String toString() {
        return "GenomeToolAnswer (status=" + status + ", message=" + message + ")";
    }
}
