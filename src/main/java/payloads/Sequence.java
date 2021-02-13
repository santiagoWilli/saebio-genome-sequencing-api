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
        return partFilesFormASequence();
    }

    public Collection<Part> getFileParts() {
        return fileParts;
    }

    private boolean partFilesFormASequence() {
        int i = 0;
        String[][] pair = new String[2][];
        for (Part part : fileParts) pair[i++] = getFilenameFieldsOf(part);
        if (!pair[0][0].equals(pair[1][0]) || !pair[0][1].equals(pair[1][1])) return false;
        return !pair[0][2].equals(pair[1][2]);
    }

    private static String[] getFilenameFieldsOf(Part part) {
        String[] nameFields = part.getSubmittedFileName().split("_");
        nameFields[2] = nameFields[2].substring(0, nameFields[2].indexOf("."));
        return nameFields;
    }

    private static String filenameRegex() {
        return "[a-zA-Z]+[0-9]{0,4}_((0[1-9])|([1-2][1-9])|(3[0-1]))((0[1-9])|(1[0-2]))[0-9]{2}_R(1|2).(fq|fastq).gz";
    }
}

