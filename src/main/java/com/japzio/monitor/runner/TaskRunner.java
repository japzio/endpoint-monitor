package com.japzio.monitor.runner;

import com.japzio.monitor.model.MonitorJob;
import com.japzio.monitor.model.SupportedMethods;
import com.japzio.monitor.service.MonitorService;
import com.japzio.monitor.task.CurlTask;
import com.japzio.monitor.task.PingTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class TaskRunner {

    private static final Logger log = LoggerFactory.getLogger(TaskRunner.class);

    private final MonitorService monitorService;
    private final Set<MonitorJob> targetsStorage;

    TaskRunner(@Autowired MonitorService monitorService,
               @Autowired @Qualifier("targetsStorage") Set<MonitorJob> targetsStorage) {
        this.monitorService = monitorService;
        this.targetsStorage = targetsStorage;
    }

    @Scheduled(cron = "${monitor.cron-expression}")
    public void runJob() {

        if(targetsStorage.isEmpty()) {
            log.warn("exiting... no targets found");
            return;
        }

        log.info("run job - start");
        ExecutorService executorService = Executors.newFixedThreadPool(targetsStorage.size());

        for (MonitorJob monitorJob: targetsStorage) {
            log.info("submit Task({})", monitorJob.getEndpoint());
            switch(monitorJob.getMethod()) {
                case CURL:
                    log.info("action=submitTask, info=CurlTask");
                    executorService.submit(new CurlTask(monitorJob, monitorService));
                    break;
                case PING:
                    log.info("action=submitTask, info=PingTask");
                    executorService.submit(new PingTask(monitorJob, monitorService));
                    break;
                default:
                    log.warn("unsupported method");

            }
        }

        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
        log.info("run job - done");
    }
}
