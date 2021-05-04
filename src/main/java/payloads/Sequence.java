package payloads;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Sequence extends Isolate implements Validable {
    public Sequence(Map<String, String> fields, Map<String, File> files) {
        super(fields, files);
    }

    public LocalDate getDate() {
        FileNameDate fileNameDate = new FileNameDate();
        return LocalDate.of(
                Integer.parseInt("20" + fileNameDate.year),
                fileNameDate.month,
                fileNameDate.day
        );
    }

    public List<String> getOriginalFileNames() {
        return files.keySet().stream().sorted().collect(Collectors.toList());
    }

    @Override
    public boolean isValid() {
        if (files.size() != 2) return false;
        for (String fileName : files.keySet()) {
            if (!fileName.matches(fileNameRegex())) return false;
        }
        return filesFormASequence();
    }

    private boolean filesFormASequence() {
        int i = 0;
        String[][] pair = new String[2][];
        for (String fileName : files.keySet()) pair[i++] = getFileNameFieldsOf(fileName);
        if (!pair[0][0].equals(pair[1][0]) || !pair[0][1].equals(pair[1][1])) return false;
        return !pair[0][2].equals(pair[1][2]);
    }

    private static String[] getFileNameFieldsOf(String name) {
        String[] nameFields = name.split("_");
        nameFields[2] = nameFields[2].substring(0, nameFields[2].indexOf("."));
        return nameFields;
    }

    private static String fileNameRegex() {
        return "[a-zA-Z]+[0-9]{0,4}_((0[1-9])|([1-2][1-9])|(3[0-1]))((0[1-9])|(1[0-2]))[0-9]{2}_R(1|2).(fq|fastq).gz";
    }

    private final class FileNameDate {
        final int day, month, year;

        FileNameDate() {
            String fileName = files.keySet().iterator().next();
            String date = fileName.split("_")[1];
            day = Integer.parseInt(date.substring(0, 2));
            month = Integer.parseInt(date.substring(2, 4));
            year = Integer.parseInt(date.substring(4, 6));
        }
    }
}

