package dataaccess;

import payloads.Sequence;
import payloads.TrimRequestResult;

import java.io.IOException;

public interface DataAccess {
    String createSequence(Sequence sequence, String genomeToolToken);
    UploadCode uploadTrimmedFile(TrimRequestResult trimResult) throws IOException;
    boolean setSequenceTrimToFalse(String token);
    String getAllSequences();
    String getSequence(String id);
}
