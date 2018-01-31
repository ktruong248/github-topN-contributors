package io.wiser.service.handlers;

import com.google.common.base.Strings;
import io.wiser.service.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Configuration;
import play.Environment;
import play.api.OptionalSourceMapper;
import play.api.routing.Router;
import play.http.DefaultHttpErrorHandler;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.concurrent.CompletionStage;

import static io.wiser.jackson.JsonHelper.toJson;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static play.mvc.Results.internalServerError;
import static play.mvc.Results.notFound;

@Singleton
public class ErrorHandler extends DefaultHttpErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    @Inject
    public ErrorHandler(Configuration configuration, Environment environment,
                        OptionalSourceMapper sourceMapper, Provider<Router> routes) {
        super(configuration, environment, sourceMapper, routes);
    }

    @Override
    public CompletionStage<Result> onClientError(Http.RequestHeader request, int statusCode, String message) {
        if (statusCode == 404) {
            message = Strings.isNullOrEmpty(message) ? "Invalid resource" : message;
            return completedFuture(notFound(toJson(new ErrorResponse(message))));
        } else {
            return completedFuture(Results.status(statusCode, toJson(new ErrorResponse(message))));
        }
    }

    @Override
    public CompletionStage<Result> onServerError(Http.RequestHeader request, Throwable exception) {
        logger.error("unhandled exception", exception);
        return completedFuture(internalServerError(toJson(new ErrorResponse(exception))));
    }
}