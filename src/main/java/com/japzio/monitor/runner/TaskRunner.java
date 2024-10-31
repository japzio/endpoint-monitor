package com.japzio.monitor.runner;

import com.japzio.monitor.config.MonitorConfig;
import com.japzio.monitor.model.EndpointStatus;
import com.japzio.monitor.service.MonitorService;
import com.japzio.monitor.task.CurlTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class TaskRunner {

    private static final Logger log = LoggerFactory.getLogger(TaskRunner.class);

    private final MonitorConfig monitorConfig;
    private final MonitorService monitorService;

    TaskRunner(@Autowired MonitorConfig monitorConfig,
               @Autowired MonitorService monitorService) {
        this.monitorConfig = monitorConfig;
        this.monitorService = monitorService;
    }

    @Scheduled(cron = "${monitor.cron-expression}")
    public void runCurl() {

        log.info("run curl - start");
        ExecutorService executorService = Executors.newFixedThreadPool(monitorConfig.getTargetEndpoints().size());

        for (URL targetEndpoint : monitorConfig.getTargetEndpoints()) {
            log.info("submit CurlTask({})", targetEndpoint.toString());
            executorService.submit(new CurlTask(targetEndpoint.toString(), monitorService));
        }

        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
        log.info("run curl - done");
    }
}
