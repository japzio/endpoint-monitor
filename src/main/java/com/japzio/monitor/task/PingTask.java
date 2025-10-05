package com.japzio.monitor.task;

import com.japzio.monitor.entity.CheckResult;
import com.japzio.monitor.entity.Target;
import com.japzio.monitor.repository.CheckResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.time.Instant;

public class PingTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(PingTask.class);

    private final Target target;
    private final CheckResultRepository checkResultRepository;

    public PingTask(
            Target target,
            CheckResultRepository checkResultRepository
    ) {
        this.target = target;
        this.checkResultRepository = checkResultRepository;
    }

    @Override
    public void run() {
        String targetEndpoint = target.getEndpoint();
        log.info("runnable task - ping - start targetId={}", target.getId());
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

            checkResultRepository.save(
                    CheckResult.builder()
                            .status(status)
                            .targetId(target.getId())
                            .createdAt(Timestamp.from(Instant.now()))
                            .build()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("runnable task - ping - done");
    }
}
