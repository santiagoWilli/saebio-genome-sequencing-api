package handlers;

import payloads.Validable;
import utils.Answer;
import utils.RequestParams;

@FunctionalInterface
interface RequestHandler<V extends Validable> {
    Answer process(V payload, RequestParams requestParams);
}
