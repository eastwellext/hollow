package com.netflix.vms.transformer.input;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.InputDataVersionIds;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import com.netflix.vms.logging.TaggingLogger;

public class VMSInputDataVersionLogger {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");


    public static void logInputVersions(Map<String, String> inputBlobHeaders, TaggingLogger logger) {
        for(Map.Entry<String, String> entry : inputBlobHeaders.entrySet()) {
            if(entry.getKey().endsWith("_coldstart")) {
                String mutationGroup = entry.getKey().substring(0, entry.getKey().indexOf("_coldstart"));
                String latestColdstartVersion = entry.getValue();
                String latestEventId = inputBlobHeaders.get(mutationGroup + "_events");
                String coldstartKeybase = inputBlobHeaders.get(mutationGroup + "_coldstartKeybase");

                // coldstart filename
                String coldstartFilename = inputBlobHeaders.get(mutationGroup + "_coldstartFile") != null ? inputBlobHeaders.get(mutationGroup + "_coldstartFile") : "null";

                // publish time in filestore
                String formattedDate = "null";
                if (inputBlobHeaders.get(mutationGroup + "_coldstartFilePublishTime") != null) {
                    long publishTime = Long.valueOf(inputBlobHeaders.get(mutationGroup + "_coldstartFilePublishTime"));
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date publishDate = new Date(publishTime);
                    formattedDate = dateFormat.format(publishDate);
                }

                logger.info(InputDataVersionIds,
                        "mutationGroup=" + mutationGroup +
                        " latestEventId=" + latestEventId +
                        " coldstartVersionId=" + latestColdstartVersion +
                        " coldstartKeybase=" + coldstartKeybase +
                        " coldstartS3Filename=" + coldstartFilename +
                        " isColdstartPinned=false" +
                        " coldstartFilePublishDate=" + formattedDate);
            }
        }
    }
}
