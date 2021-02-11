package handlers;

import payloads.Validable;
import spark.Request;
import spark.Response;
import spark.Route;
import utils.Answer;

public abstract class AbstractHandler<V extends Validable> implements RequestHandler<V>, Route {

    @Override
    public final Answer process(V payload) {
        if (!payload.isValid()) {
            return Answer.badRequest("Cuerpo de la petición no válido");
        } else {
            return processRequest(payload);
        }
    }

    protected abstract Answer processRequest(V payload);

    @Override
    public Object handle(Request request, Response response) {
        return null;
    }
}
