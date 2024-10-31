package com.japzio.monitor.task;

import com.japzio.monitor.model.EndpointStatus;
import com.japzio.monitor.service.MonitorService;
import com.roxstudio.utils.CUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Map;

public class CurlTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(CurlTask.class);

    private final String targetEndpoint;
    private final MonitorService monitorService;

    public CurlTask(
            String targetEndpoint,
            MonitorService monitorService
    ) {
        this.targetEndpoint = targetEndpoint;
        this.monitorService = monitorService;
    }

    @Override
    public void run() {
        log.info("runnable task - curl - start");
        CUrl curlRequest = new CUrl(targetEndpoint);
        curlRequest.exec();

        log.info("curl result - {}", curlRequest.getHttpCode());
        monitorService.saveResults(
                Map.of(
                    targetEndpoint,
                    new EndpointStatus(
                            String.valueOf(curlRequest.getHttpCode()),
                            Instant.now().toString()
                    )
                )
        );
        log.info("runnable task - curl - done");
    }
}
