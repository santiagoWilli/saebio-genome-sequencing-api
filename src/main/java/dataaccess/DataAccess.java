package dataaccess;

import payloads.*;

import java.io.IOException;
import java.io.InputStream;

public interface DataAccess {
    String createSequence(Sequence sequence, String genomeToolToken);
    UploadCode uploadTrimmedFile(TrimRequestResult trimResult);
    boolean setSequenceTrimToFalse(String token);
    String getAllSequences();
    String getSequence(String id);
    String getTrimmedFileName(String id);
    InputStream getFileStream(String id) throws IOException;

    String uploadReference(Reference reference) throws IOException;
    String getAllReferences();
    String getReference(String id);

    String getAllStrains();
    boolean createStrain(Strain strain);
    boolean deleteStrain(String id) throws DocumentPointsToStrainException;
    boolean strainExists(String key);
    boolean updateStrainKeys(String id, StrainKeys keys);
}
