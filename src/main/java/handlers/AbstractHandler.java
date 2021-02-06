package handlers;

import payloads.Validable;
import spark.Request;
import spark.Response;
import spark.Route;

public abstract class AbstractHandler<V extends Validable> implements RequestHandler<V>, Route {

    @Override
    public final Answer process(V payload) {
        if (!payload.isValid()) {
            return Answer.badRequest("Cuerpo de la petición no válido");
        } else {
            return processRequest();
        }
    }

    protected abstract Answer processRequest();

    @Override
    public Object handle(Request request, Response response) {
        return null;
    }
}
