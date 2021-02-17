package dataaccess;

import payloads.Sequence;

public interface DataAccess {
    String createSequence(Sequence sequence, String genomeToolToken);
}
