package genome;

import payloads.Sequence;
import utils.Answer;

public class NullarborClient implements GenomeTool {
    @Override
    public Answer requestTrim(Sequence sequence) {
        return new Answer(Response.EXCEPTION_ENCOUNTERED.code());
    }
}
