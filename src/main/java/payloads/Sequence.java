package payloads;

import utils.StrainMap;

import javax.servlet.http.Part;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Sequence extends Multipart implements Validable {
    public Sequence(Collection<Part> fileParts) {
        super(fileParts);
    }

    public LocalDate getDate() {
        FilenameDate filenameDate = new FilenameDate();
        return LocalDate.of(
                Integer.parseInt("20" + filenameDate.year),
                filenameDate.month,
                filenameDate.day
        );
    }

    public String getStrain() {
        return StrainMap.get(strainKey());
    }

    public List<String> getOriginalFilenames() {
        return fileParts.stream().map(Part::getSubmittedFileName).collect(Collectors.toList());
    }

    @Override
    public boolean isValid() {
        if (fileParts.size() != 2) return false;
        for (Part part : fileParts) {
            if (!part.getSubmittedFileName().matches(filenameRegex())) return false;
        }
        return partFilesFormASequence();
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

    private final class FilenameDate {
        final int day, month, year;

        FilenameDate() {
            String filename = fileParts.iterator().next().getSubmittedFileName();
            String date = filename.split("_")[1];
            day = Integer.parseInt(date.substring(0, 2));
            month = Integer.parseInt(date.substring(2, 4));
            year = Integer.parseInt(date.substring(4, 6));
        }
    }

    private String strainKey() {
        return fileParts.iterator().next()
                .getSubmittedFileName()
                .split("_")[0]
                .split("[0-9]")[0]
                .toLowerCase();
    }
}

