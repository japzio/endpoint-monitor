package com.japzio.monitor.task;

import com.japzio.monitor.entity.CheckResult;
import com.japzio.monitor.entity.Target;
import com.japzio.monitor.model.CheckResultsStatus;
import com.japzio.monitor.properties.MonitorProperties;
import com.japzio.monitor.repository.CheckResultRepository;
import com.roxstudio.utils.CUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.Instant;

public class CurlTask extends  BaseTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(PingTask.class);

    private final Target target;
    private final MonitorProperties monitorProperties;

    public CurlTask(
           Target target,
           CheckResultRepository checkResultRepository,
           MonitorProperties monitorProperties
    ) {
        super(checkResultRepository);

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
        var status = curlRequest.getHttpCode() == 200 ? CheckResultsStatus.OK.name() : CheckResultsStatus.NOT_OK.name();
        saveCheckResult(
                CheckResult.builder()
                        .status(status)
                        .targetId(target.getId())
                        .duration(
                                curlRequest.getHttpCode() == 200 ? (int) curlRequest.getExecTime() : null)
                        .createdAt(Timestamp.from(Instant.now()))
                        .description(status.equals(CheckResultsStatus.NOT_OK.name()) ? String.valueOf(curlRequest.getHttpCode()) : "")
                        .build()
        );
        log.info("runnable task - curl - done");
    }
}
