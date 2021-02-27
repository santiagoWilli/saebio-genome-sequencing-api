package dataaccess;

import payloads.Sequence;
import payloads.TrimRequestResult;

public interface DataAccess {
    String createSequence(Sequence sequence, String genomeToolToken);
    UploadCode uploadTrimmedFile(TrimRequestResult trimResult);
    boolean setSequenceTrimToFalse(String token);
}
