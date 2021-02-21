package handlers;

import payloads.Validable;
import spark.Request;
import spark.Response;
import spark.Route;
import utils.Answer;

import javax.servlet.MultipartConfigElement;
import java.util.Collection;

public abstract class AbstractHandler<V extends Validable> implements RequestHandler<V>, Route {
    private final Class<V> payloadClass;

    protected AbstractHandler(Class<V> payloadClass) {
        this.payloadClass = payloadClass;
    }

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
    public Object handle(Request request, Response response) throws Exception {
        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
        V payload = payloadClass.getConstructor(Collection.class).newInstance(request.raw().getParts());

        Answer answer = process(payload);
        response.status(answer.getCode());
        response.type("application/json");
        return answer.getBody();
    }
}
