package handlers;

import payloads.Validable;
import utils.Answer;

import java.util.Map;

@FunctionalInterface
interface RequestHandler<V extends Validable> {
    Answer process(V payload, Map<String, String> requestParams);
}
