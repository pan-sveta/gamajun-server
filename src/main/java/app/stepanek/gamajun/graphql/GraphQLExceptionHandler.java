package app.stepanek.gamajun.graphql;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {
    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
            if (ex instanceof GraphQLError)
                return (GraphQLError) ex;
            if (ex instanceof DataIntegrityViolationException)
                return handleDataIntegrityViolationException((DataIntegrityViolationException) ex);
            else
                return super.resolveToSingleError(ex, env);
    }

    private GraphQLError handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return GraphqlErrorBuilder
                .newError()
                .message("Objekt s tímto klíčem již existuje")
                .extensions(Map.of("code", "DUPLICATE_KEY","exception", ex.getClass().getName(), "exceptionMessage", ex.getMessage()))
                .errorType(ErrorType.ValidationError).build();
    }
}
