package payloads;

import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TrimRequestResult extends Multipart implements Validable {
    public TrimRequestResult(Collection<Part> fileParts) {
        super(fileParts);
    }

    public String getSequenceToken() {
        return new BufferedReader(streamReaderFromPartWithName("token"))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    public List<Part> getFileParts() {
        return Arrays.asList(partWithName("file1"), partWithName("file2"));
    }

    public int getStatusCode() {
        return Integer.parseInt(new BufferedReader(streamReaderFromPartWithName("status"))
                .lines()
                .collect(Collectors.joining("\n")));
    }

    @Override
    public boolean isValid() {
        if (partWithName("status") == null || partWithName("token") == null) return false;
        if (getStatusCode() == 2) {
            Part filePartOne = partWithName("file1");
            Part filePartTwo = partWithName("file2");
            if (filePartOne == null || filePartTwo == null) return false;
            return fileIsFastq(filePartOne) && fileIsFastq(filePartTwo)
                    && !filePartOne.getSubmittedFileName().equals(filePartTwo.getSubmittedFileName());
        }
        return true;
    }

    private Part partWithName(String name) {
        return parts
                .stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private boolean fileIsFastq(Part part) {
        String[] extensions = new String[] {".fq.gz", ".fastq.gz", ".fq", ".fastq"};
        return Arrays.stream(extensions)
                .anyMatch(e -> part.getSubmittedFileName().endsWith(e));
    }

    private InputStreamReader streamReaderFromPartWithName(String name) {
        try {
            return new InputStreamReader(partWithName(name).getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.getStackTrace();
            throw new RuntimeException();
        }
    }
}
