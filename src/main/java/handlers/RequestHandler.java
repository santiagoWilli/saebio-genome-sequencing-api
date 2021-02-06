package handlers;

import payloads.Validable;

@FunctionalInterface
interface RequestHandler<V extends Validable> {
    Answer process(V payload);
}
