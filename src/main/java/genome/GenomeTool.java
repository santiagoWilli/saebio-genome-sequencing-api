package genome;

import payloads.ReportRequest;
import payloads.Sequence;

public interface GenomeTool {
    GenomeToolAnswer requestTrim(Sequence sequence);
    GenomeToolAnswer requestAnalysis(ReportRequest reportRequest);
}
