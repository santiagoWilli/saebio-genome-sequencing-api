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

    public boolean isTrimmed() {
        return files.keySet().iterator().next().contains("_trimmed");
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
        FileNameFields[] pair = new FileNameFields[2];
        for (String fileName : files.keySet()) pair[i++] = new FileNameFields(fileName);
        if (pair[0].trimmed && !pair[1].trimmed || !pair[0].trimmed && pair[1].trimmed) return false;
        if (!pair[0].strain.equals(pair[1].strain) || !pair[0].date.equals(pair[1].date)) return false;
        return !pair[0].number.equals(pair[1].number);
    }

    private static String fileNameRegex() {
        return "[a-zA-Z]+[0-9]{1,6}_((0[1-9])|([1-2][0-9])|(3[0-1]))((0[1-9])|(1[0-2]))[0-9]{2}_R(1|2)(_trimmed)?.(fq|fastq).gz";
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

    private final class FileNameFields {
        final String strain, date, number;
        final boolean trimmed;

        FileNameFields(String fileName) {
            String[] fields = fileName.split("_");
            strain = fields[0];
            date = fields[1];
            if (fields.length == 3) number = fields[2].substring(0, fields[2].indexOf("."));
            else number = fields[2];
            trimmed = fields.length > 3;
        }
    }
}

