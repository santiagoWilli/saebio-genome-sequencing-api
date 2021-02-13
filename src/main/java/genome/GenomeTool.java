package genome;

import payloads.Sequence;
import utils.Answer;

public interface GenomeTool {
    Answer requestTrim(Sequence sequence);

    enum Response {
        API_DOWN(4),
        SERVER_ERROR(5),
        EXCEPTION_ENCOUNTERED(6);

        private final int code;
        Response(int code) {
            this.code = code;
        }
        public int code() {
            return code;
        }
    }
}
