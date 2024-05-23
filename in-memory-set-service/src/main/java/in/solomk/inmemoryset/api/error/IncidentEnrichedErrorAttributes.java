package in.solomk.inmemoryset.api.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class IncidentEnrichedErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request,
                                                  ErrorAttributeOptions options) {
        var errorAttributes = super.getErrorAttributes(request, options.including(ErrorAttributeOptions.Include.MESSAGE));
        var error = getError(request);

        var incidentId = UUID.randomUUID().toString();

        errorAttributes.put("incidentId", incidentId);
        log.error("Error occurred during request processing [request={}, incidentId={}, errorAttributes={}]", request, incidentId, errorAttributes, error);
        return errorAttributes;
    }
}
