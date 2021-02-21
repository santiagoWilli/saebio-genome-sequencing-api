package payloads;

import javax.servlet.http.Part;
import java.util.Collection;

public abstract class Multipart {
    protected final Collection<Part> fileParts;

    protected Multipart(Collection<Part> fileParts) {
        this.fileParts = fileParts;
    }

    public Collection<Part> getFileParts() {
        return fileParts;
    }
}
