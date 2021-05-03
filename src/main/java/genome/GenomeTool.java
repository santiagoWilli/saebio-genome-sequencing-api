package genome;

import payloads.Sequence;
import java.io.InputStream;

public interface GenomeTool {
    GenomeToolAnswer requestTrim(Sequence sequence);

    GenomeToolAnswer requestToSendAnalysisFiles();
    GenomeToolAnswer sendAnalysisFile(String token, InputStream stream, String fileName);
    GenomeToolAnswer requestToStartAnalysis(String token);
}
