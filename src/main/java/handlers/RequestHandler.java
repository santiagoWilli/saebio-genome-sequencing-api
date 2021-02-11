package handlers;

import payloads.Validable;
import utils.Answer;

@FunctionalInterface
interface RequestHandler<V extends Validable> {
    Answer process(V payload);
}
