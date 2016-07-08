package com.netflix.vms.transformer.logger;

import static org.slf4j.helpers.MessageFormatter.arrayFormat;

import java.util.Collection;
import java.util.function.Function;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.netflix.vms.logging.TaggingLogger;
import com.netflix.vms.transformer.common.config.TransformerConfig;
import com.netflix.vms.transformer.elasticsearch.ElasticSearchClient;

public class TransformerServerLogger implements TaggingLogger {
    private static final String FQCN = TaggingLogger.class.getName();

    private final ElasticSearchClient esClient;
    private final TransformerConfig config;
    private final LocationAwareLogger consoleLogger;
    private final long currentCycleId;

    private final String elasticSearchIndexName;
    private final ObjectMapper objectMapper;

    public TransformerServerLogger(TransformerConfig config, ElasticSearchClient esClient) {
        this(
                config,
                esClient,
                LoggerFactory.getLogger("Transformer"),
                -1
        );
    }

    private TransformerServerLogger(TransformerConfig config, ElasticSearchClient esClient, Logger consoleLogger, long cycleId) {
        this.consoleLogger = (LocationAwareLogger) consoleLogger;
        this.esClient = esClient;
        this.config = config;
        this.currentCycleId = cycleId;
        this.elasticSearchIndexName = buildIndexName();
        this.objectMapper = new ObjectMapper();
    }

    public TransformerServerLogger withCurrentCycleId(long cycleId) {
        return new TransformerServerLogger(config, esClient, consoleLogger, cycleId);
    }

    private String toJsonString(Severity severity, LogTag logTag, String message, Throwable th) throws JsonProcessingException {
        if(th != null)
            message += "; Exception: " + ExceptionUtils.getStackTrace(th);

        TransformerLogMessage msg = new TransformerLogMessage(severity, logTag, message, String.valueOf(currentCycleId), System.currentTimeMillis(), config.getAwsInstanceId());

        ObjectWriter writer = objectMapper.writer();
        return writer.writeValueAsString(msg);
    }


    private String buildIndexName() {
        final StringBuilder builder = new StringBuilder("vms-");
        builder.append(config.getTransformerVip()).append("-cyc_").append(currentCycleId / 1000000000);
        return builder.toString().toLowerCase();
    }

    @Override
    public void log(Severity severity, Collection<LogTag> tags, String message, Object... args) {
        // TODO: timt: partially duplicated from TaggingLoggers
        String formattedMessage = message;
        Throwable cause = null;
        if (args.length > 0) {
            formattedMessage = arrayFormat(message, args).getMessage();
            Object o = args[args.length-1];
            cause = o != null && Throwable.class.isAssignableFrom(o.getClass()) ? (Throwable)o : null;
        }

        for (LogTag tag : tags) {
            consoleLogger.log(null, FQCN, severity.intValue, tag.with(formattedMessage), null, cause);
            if(config.isElasticSearchLoggingEnabled()) {
                try {
                    esClient.addData(elasticSearchIndexName, "vmsserver", toJsonString(severity, tag, formattedMessage, cause));
                } catch (JsonProcessingException e) {
                    consoleLogger.log(null, FQCN, Severity.ERROR.intValue, "Unable to create json for ES log message", null, e);
                }
            }
        }
    }
}
