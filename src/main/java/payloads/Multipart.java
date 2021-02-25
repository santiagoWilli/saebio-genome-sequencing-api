package payloads;

import javax.servlet.http.Part;
import java.util.Collection;

public abstract class Multipart {
    protected final Collection<Part> parts;

    protected Multipart(Collection<Part> parts) {
        this.parts = parts;
    }

    public Collection<Part> getParts() {
        return parts;
    }
}
