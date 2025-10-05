package com.japzio.monitor.task;

import com.japzio.monitor.model.EndpointStatus;
import com.japzio.monitor.model.MonitorJob;
import com.japzio.monitor.service.MonitorService;
import com.roxstudio.utils.CUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Map;

public class CurlTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(PingTask.class);

    private final MonitorJob monitorJob;
    private final MonitorService monitorService;

    public CurlTask(
            MonitorJob monitorJob,
            MonitorService monitorService
    ) {
        this.monitorJob = monitorJob;
        this.monitorService = monitorService;
    }

    @Override
    public void run() {
        String targetEndpoint = monitorJob.getEndpoint();
        log.info("runnable task - curl - start");
        CUrl curlRequest = new CUrl(targetEndpoint);
        curlRequest.exec();
        log.info("curl exec - {}", targetEndpoint);
        log.info("curl result - {}", curlRequest.getHttpCode());
        monitorService.saveResults(
                Map.of(
                        targetEndpoint,
                        new EndpointStatus(
                                monitorJob,
                                String.valueOf(curlRequest.getHttpCode()),
                                Instant.now().toString()
                        )
                )
        );
        log.info("runnable task - curl - done");
    }
}
