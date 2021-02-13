package genome;

import payloads.Sequence;
import utils.Answer;

public interface GenomeTool {
    Answer requestTrim(Sequence sequence);

    enum Response {
        EXCEPTION_ENCOUNTERED(5);

        private final int code;
        Response(int code) {
            this.code = code;
        }
        public int code() {
            return code;
        }
    }
}
