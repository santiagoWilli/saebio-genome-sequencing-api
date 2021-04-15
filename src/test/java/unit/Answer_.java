package unit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import utils.Answer;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class Answer_ {
    @Test
    public void badRequest_returns_code400() {
        String message = "The request is invalid";
        assertThat(Answer.badRequest(message).getCode()).isEqualTo(400);
        assertThat(Answer.badRequest(message).getBody()).isEqualTo("{\"message\":\"" + message + "\"}");
        assertThat(Answer.badRequest(message).hasFile()).isFalse();

    }

    @Test
    public void serviceUnavailable_returns_code503() {
        String message = "Genome reporter tool is down";
        assertThat(Answer.serviceUnavailable(message).getCode()).isEqualTo(503);
        assertThat(Answer.serviceUnavailable(message).getBody()).isEqualTo("{\"message\":\"" + message + "\"}");
        assertThat(Answer.serviceUnavailable(message).hasFile()).isFalse();
    }

    @Test
    public void badGateway_returns_code502() {
        String message = "Genome reporter tool encountered an internal error";
        assertThat(Answer.badGateway(message).getCode()).isEqualTo(502);
        assertThat(Answer.badGateway(message).getBody()).isEqualTo("{\"message\":\"" + message + "\"}");
        assertThat(Answer.badGateway(message).hasFile()).isFalse();
    }

    @Test
    public void notFount_returns_code404() {
        assertThat(Answer.notFound().getCode()).isEqualTo(404);
        assertThat(Answer.notFound().hasFile()).isFalse();
    }

    @Test
    public void withFile_returns_code200_and_hasAnswerFileWithProvidedFileAndContentType() throws IOException {
        File file = new File("test/resources/answerWithFile.txt");
        InputStream stream = FileUtils.openInputStream(file);
        Answer answer = Answer.withFile(stream, "text/plain");
        assertThat(answer.hasFile()).isTrue();
        assertThat(answer.getCode()).isEqualTo(200);
        assertThat(answer.getFile().getMimeType()).isEqualTo("text/plain");
        assertThat(IOUtils.contentEquals(answer.getFile().getInputStream(), stream)).isTrue();
    }
}
