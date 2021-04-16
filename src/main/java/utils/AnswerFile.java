package utils;

import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

import java.io.InputStream;

public final class AnswerFile {
    private final InputStream inputStream;
    private final String mimeType;

    AnswerFile(InputStream inputStream, String mimeType) {
        this.inputStream = inputStream;
        this.mimeType = mimeType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String guessExtension() throws MimeTypeException {
        return MimeTypes.getDefaultMimeTypes().forName(mimeType).getExtension();
    }
}
