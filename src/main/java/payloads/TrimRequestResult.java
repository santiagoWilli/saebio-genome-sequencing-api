package payloads;

import javax.servlet.http.Part;
import java.util.Collection;

public class TrimRequestResult extends Multipart implements Validable {
    public TrimRequestResult(Collection<Part> fileParts) {
        super(fileParts);
    }

    public String getSequenceToken() {
        return "";
    }

    @Override
    public boolean isValid() {
        return false;
    }
}
