package com.fastcode.example.addons.audittrail;

import com.fastcode.example.addons.audittrail.domain.irepository.IApiHistoryRepository;
import com.fastcode.example.addons.audittrail.domain.model.ApiHistoryEntity;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Precorrelation;
import org.zalando.logbook.Sink;

public final class DBSink implements Sink {

    @Autowired
    IApiHistoryRepository apiHistoryRepository;

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void write(final Precorrelation precorrelation, final HttpRequest request) throws IOException {
        if (!"/audit/apis".equalsIgnoreCase(request.getPath())) {
            ApiHistoryEntity entity = new ApiHistoryEntity();
            entity.setCorrelation(precorrelation.getId());
            entity.setPath(request.getPath());
            String username = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : null;
            entity.setUserName(username);
            entity.setHeader(request.getHeaders().toString());
            entity.setBody(request.getBodyAsString());
            entity.setContentType(request.getContentType());
            entity.setMethod(request.getMethod());
            entity.setQuery(request.getQuery());
            entity.setClientAddress(request.getRemote());
            entity.setScheme(request.getScheme());
            entity.setRequestTime(LocalDateTime.now());
            entity.setBrowser(request.getHeaders().get("user-agent").toString());
            apiHistoryRepository.save(entity);
        }
    }

    @Override
    public void write(final Correlation correlation, final HttpRequest request, final HttpResponse response)
        throws IOException {
        if (!"/audit/apis".equalsIgnoreCase(request.getPath())) {
            LocalDateTime now = LocalDateTime.now();
            ApiHistoryEntity entityReturned = apiHistoryRepository.getByCorrelation(correlation.getId());
            entityReturned.setResponseStatus(String.valueOf(response.getStatus()));
            if (response.getStatus() > 299 || response.getStatus() < 200) entityReturned.setResponse(
                response.getBodyAsString()
            );
            entityReturned.setResponseTime(now);
            entityReturned.setProcessTime(Duration.between(entityReturned.getRequestTime(), now).toMillis());
            apiHistoryRepository.save(entityReturned);
        }
    }
}
