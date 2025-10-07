package com.japzio.monitor.task;

import com.japzio.monitor.entity.CheckResult;
import com.japzio.monitor.entity.Target;
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
        try {
            var status = "unset";

            InetAddress inet = InetAddress.getByName(targetEndpoint);

            System.out.println("Pinging " + targetEndpoint + "...");
            var start = Instant.now();
            boolean reachable = inet.isReachable(Math.toIntExact(timeout)); // timeout in ms
            var duration = Duration.between(start, Instant.now()).getSeconds();
            if (reachable) {
                status = "Ok";
                log.info("ping exec - {} is reachable.", targetEndpoint);
            } else {
                status = "Unreachable";
                log.info("ping exec - {} is NOT reachable.", targetEndpoint);
            }

            log.info("ping exec - {}", targetEndpoint);
            log.info("ping result - {}", status);

            saveCheckResult(
                    CheckResult.builder()
                            .status(status)
                            .targetId(target.getId())
                            .duration(reachable ? (int) duration : null)
                            .createdAt(Timestamp.from(Instant.now()))
                            .build()
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("runnable task - ping - done");
    }
}
