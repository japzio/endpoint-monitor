package com.japzio.monitor.task;

import com.japzio.monitor.model.EndpointStatus;
import com.japzio.monitor.model.MonitorJob;
import com.japzio.monitor.service.MonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.time.Instant;
import java.util.Map;

public class PingTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(PingTask.class);

    private final MonitorJob monitorJob;
    private final MonitorService monitorService;

    public PingTask(
            MonitorJob monitorJob,
            MonitorService monitorService
    ) {
        this.monitorJob = monitorJob;
        this.monitorService = monitorService;
    }

    @Override
    public void run() {
        String targetEndpoint = monitorJob.getEndpoint();
        log.info("runnable task - ping - start");
        try {
            var status = "unset";
            String host = "google.com";
            InetAddress inet = InetAddress.getByName(targetEndpoint);

            System.out.println("Pinging " + host + "...");
            boolean reachable = inet.isReachable(5000); // timeout in ms

            if (reachable) {
                status = "Ok";
                log.info("ping exec - {} is reachable.", targetEndpoint);
            } else {
                status = "Unreachable";
                log.info("ping exec - {} is NOT reachable.", targetEndpoint);
            }

            log.info("ping exec - {}", targetEndpoint);
            log.info("ping result - {}", status);

            monitorService.saveResults(
                    Map.of(
                            targetEndpoint,
                            new EndpointStatus(
                                    monitorJob,
                                    status,
                                    Instant.now().toString()
                            )
                    )
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("runnable task - ping - done");
    }
}
