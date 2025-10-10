package com.japzio.monitor.task;

import com.influxdb.client.InfluxDBClient;
import com.japzio.monitor.model.entity.Target;
import com.japzio.monitor.model.CheckResultsStatus;
import com.japzio.monitor.model.internal.CheckResultsDto;
import com.japzio.monitor.properties.MonitorProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;

public class PingTask extends BaseTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(PingTask.class);

    private final Target target;
    private final MonitorProperties monitorProperties;

    public PingTask(
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
        log.info("runnable task - ping - start targetId={}", target.getId());
        log.info("ping task={}, timeout={}", targetEndpoint, timeout);

        var success = false;
        var description = "";
        System.out.println("Pinging " + targetEndpoint + "...");
        var start = Instant.now();
        var duration = 0L;
        try {
            InetAddress inet = InetAddress.getByName(targetEndpoint);
            boolean reachable = inet.isReachable(Math.toIntExact(timeout)); // timeout in ms
            duration = Duration.between(start, Instant.now()).getSeconds();
            if (reachable) {
                success = true;
                log.info("ping exec - {} is reachable.", targetEndpoint);
            } else {
                description = "NOT reachable.";
                log.info("ping exec - {} is NOT reachable.", targetEndpoint);
            }

            log.info("ping exec - {}", targetEndpoint);
            log.info("ping result success - {}", success);

        } catch (Exception e) {
            description = e.getMessage();
            log.error("ping exec - {} exception occurred!", targetEndpoint);
            e.printStackTrace();
        }

        saveCheckResult(
                CheckResultsDto.builder()
                        .success(success)
                        .targetId(target.getId())
                        .endpoint(target.getEndpoint())
                        .method(target.getMethod().toString())
                        .duration(success ? (int) duration : 0 )
                        .createdAt(Instant.now())
                        .description(!success ? description : "")
                        .build()
        );

        log.info("runnable task - ping - done");
    }
}
