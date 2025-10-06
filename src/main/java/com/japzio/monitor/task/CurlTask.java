package com.japzio.monitor.task;

import com.japzio.monitor.entity.CheckResult;
import com.japzio.monitor.entity.Target;
import com.japzio.monitor.properties.MonitorProperties;
import com.japzio.monitor.repository.CheckResultRepository;
import com.roxstudio.utils.CUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.Instant;

public class CurlTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(PingTask.class);

    private final Target target;
    private final CheckResultRepository checkResultRepository;
    private final MonitorProperties monitorProperties;

    public CurlTask(
           Target target,
           CheckResultRepository checkResultRepository,
           MonitorProperties monitorProperties
    ) {
        this.target = target;
        this.checkResultRepository = checkResultRepository;
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
        checkResultRepository.save(
                CheckResult.builder()
                        .status(String.valueOf(curlRequest.getHttpCode()))
                        .targetId(target.getId())
                        .createdAt(Timestamp.from(Instant.now()))
                        .build()
        );
        log.info("runnable task - curl - done");
    }
}
