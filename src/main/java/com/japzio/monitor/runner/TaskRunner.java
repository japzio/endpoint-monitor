package com.japzio.monitor.runner;

import com.japzio.monitor.config.MonitorConfig;
import com.japzio.monitor.model.EndpointStatus;
import com.japzio.monitor.task.CurlTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    public MonitorConfig monitorConfig;
    public Map<String, EndpointStatus> endpointStatusStore;

    TaskRunner(@Autowired MonitorConfig monitorConfig,
               @Autowired Map<String, EndpointStatus> endpointStatusStore) {
        this.monitorConfig = monitorConfig;
        this.endpointStatusStore = endpointStatusStore;
    }

    @Scheduled(fixedRate = 5000)
    public void runCurl() {

        log.info("run curl - start");
        ExecutorService executorService = Executors.newFixedThreadPool(monitorConfig.getTargetEndpoints().size());

        for (URL targetEndpoint : monitorConfig.getTargetEndpoints()) {
            log.info("submit CurlTask({})", targetEndpoint.toString());
            executorService.submit(new CurlTask(targetEndpoint.toString(), endpointStatusStore));
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