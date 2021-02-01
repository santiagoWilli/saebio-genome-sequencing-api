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
        if (fileParts.size() != 2) return false;
        for (Part part : fileParts) {
            if (!part.getSubmittedFileName().matches(filenameRegex())) return false;
        }
        return true;
    }

    private static String filenameRegex() {
        return "[a-zA-Z]+[0-9]{0,4}_((0[1-9])|([1-2][1-9])|(3[0-1]))((0[1-9])|(1[0-2]))[0-9]{2}_R(1|2).(fq|fastq).gz";
    }
}

