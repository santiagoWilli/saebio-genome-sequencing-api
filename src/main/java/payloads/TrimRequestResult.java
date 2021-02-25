package payloads;

import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class TrimRequestResult extends Multipart implements Validable {
    public TrimRequestResult(Collection<Part> fileParts) {
        super(fileParts);
    }

    public String getSequenceToken() throws IOException {
        return new BufferedReader(new InputStreamReader(partWithName("token").getInputStream(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    private Part partWithName(String name) {
        return fileParts
                .stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean isValid() {
        if (partWithName("status") == null || partWithName("token") == null) return false;
        try {
            if (getStatusCode() == 2) {
                Part filePartOne = partWithName("file1");
                Part filePartTwo = partWithName("file2");
                if (filePartOne == null || filePartTwo == null) return false;
                return fileIsFastq(filePartOne) && fileIsFastq(filePartTwo);
            }
            return true;
        } catch (Exception e) {
            e.getStackTrace();
            return false;
        }
    }

    public int getStatusCode() throws IOException {
        return Integer.parseInt(new BufferedReader(new InputStreamReader(partWithName("status").getInputStream(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n")));
    }

    private boolean fileIsFastq(Part part) {
        String[] extensions = new String[] {".fq.gz", ".fastq.gz", ".fq", ".fastq"};
        return Arrays.stream(extensions)
                .anyMatch(e -> part.getSubmittedFileName().endsWith(e));
    }
}
