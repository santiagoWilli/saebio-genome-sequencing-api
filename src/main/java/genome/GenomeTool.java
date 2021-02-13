package genome;

import payloads.Sequence;

public interface GenomeTool {
    GenomeToolAnswer requestTrim(Sequence sequence);
}
