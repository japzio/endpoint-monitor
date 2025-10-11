package com.japzio.monitor.task;

import com.influxdb.client.InfluxDBClient;
import com.japzio.monitor.model.entity.Target;
import com.japzio.monitor.model.CheckResultsStatus;
import com.japzio.monitor.model.internal.CheckResultsDto;
import com.japzio.monitor.properties.MonitorProperties;
import com.roxstudio.utils.CUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class CurlTask extends  BaseTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(PingTask.class);

    private final Target target;
    private final MonitorProperties monitorProperties;

    public CurlTask(
           Target target,
           MonitorProperties monitorProperties,
           InfluxDBClient influxDBClient
    ) {
        super(influxDBClient);

        this.target = target;
        this.monitorProperties = monitorProperties;
    }

    @Override
    public void run() {
        String targetEndpoint = target.getEndpoint();
        var timeout = target.getTimeout() != null && target.getTimeout() > monitorProperties.getMaxTimeout()
                ? target.getTimeout() : monitorProperties.getMaxTimeout();
        log.info("runnable task - curl - start targetId={}", target.getId());
        CUrl curlRequest = new CUrl(targetEndpoint);
        curlRequest.timeout(timeout, timeout);
        curlRequest.exec();
        log.info("curl exec={}, timeout={}", targetEndpoint, timeout);
        log.info("curl result - {}", curlRequest.getHttpCode());
        var success = curlRequest.getHttpCode() == 200;
        saveCheckResult(
                CheckResultsDto.builder()
                        .success(success)
                        .targetId(target.getId())
                        .endpoint(target.getEndpoint())
                        .method(target.getMethod().toString())
                        .duration((int) curlRequest.getExecTime())
                        .createdAt(Instant.now())
                        .description(success ? String.valueOf(curlRequest.getHttpCode()) : "")
                        .build()
        );
        log.info("runnable task - curl - done");
    }
}
