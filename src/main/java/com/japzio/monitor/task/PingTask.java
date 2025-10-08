package com.japzio.monitor.task;

import com.japzio.monitor.entity.CheckResult;
import com.japzio.monitor.entity.Target;
import com.japzio.monitor.model.CheckResultsStatus;
import com.japzio.monitor.properties.MonitorProperties;
import com.japzio.monitor.repository.CheckResultRepository;
import com.sun.jdi.IncompatibleThreadStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoField;

public class PingTask extends BaseTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(PingTask.class);

    private final Target target;
    private final MonitorProperties monitorProperties;

    public PingTask(
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
        log.info("runnable task - ping - start targetId={}", target.getId());
        log.info("ping task={}, timeout={}", targetEndpoint, timeout);

        var status = CheckResultsStatus.OK.name();
        var description = "";
        System.out.println("Pinging " + targetEndpoint + "...");
        var start = Instant.now();
        var duration = 0L;
        try {
            InetAddress inet = InetAddress.getByName(targetEndpoint);
            boolean reachable = inet.isReachable(Math.toIntExact(timeout)); // timeout in ms
            duration = Duration.between(start, Instant.now()).getSeconds();
            if (reachable) {
                status = CheckResultsStatus.OK.name();
                log.info("ping exec - {} is reachable.", targetEndpoint);
            } else {
                status = CheckResultsStatus.NOT_OK.name();
                description = "NOT reachable.";
                log.info("ping exec - {} is NOT reachable.", targetEndpoint);
            }

            log.info("ping exec - {}", targetEndpoint);
            log.info("ping result - {}", status);

        } catch (Exception e) {
            status = CheckResultsStatus.NOT_OK.name();
            description = e.getMessage();
            log.error("ping exec - {} exception occurred!", targetEndpoint);
            e.printStackTrace();
        }

        saveCheckResult(
                CheckResult.builder()
                        .status(status)
                        .targetId(target.getId())
                        .duration(status.equals(CheckResultsStatus.OK.name()) ? (int) duration : null)
                        .createdAt(Timestamp.from(Instant.now()))
                        .description(status.equals(CheckResultsStatus.NOT_OK.name()) ? description : "")
                        .build()
        );

        log.info("runnable task - ping - done");
    }
}
