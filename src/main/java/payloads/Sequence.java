package payloads;

import javax.servlet.http.Part;
import java.util.Collection;

public class Sequence implements Validable {
    Collection<Part> fileParts;

    public Sequence(Collection<Part> fileParts) {
        this.fileParts = fileParts;
    }

    @Override
    public boolean isValid() {
        return false;
    }
}

