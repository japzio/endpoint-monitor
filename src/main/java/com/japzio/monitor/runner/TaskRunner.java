package com.japzio.monitor.runner;

import com.japzio.monitor.entity.Target;
import com.japzio.monitor.properties.MonitorProperties;
import com.japzio.monitor.repository.CheckResultRepository;
import com.japzio.monitor.repository.TargetRepository;
import com.japzio.monitor.task.CurlTask;
import com.japzio.monitor.task.PingTask;
import com.japzio.monitor.task.TelnetTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class TaskRunner {

    private static final Logger log = LoggerFactory.getLogger(TaskRunner.class);

    private final TargetRepository targetRepository;
    private final CheckResultRepository checkResultRepository;
    private final MonitorProperties monitorProperties;

    TaskRunner(
            @Autowired TargetRepository targetRepository,
            @Autowired CheckResultRepository checkResultRepository,
            @Autowired MonitorProperties monitorProperties

    ) {
        this.targetRepository = targetRepository;
        this.checkResultRepository = checkResultRepository;
        this.monitorProperties = monitorProperties;
    }

    @Scheduled(cron = "${monitor.cron-expression}")
    public void runJob() {

        var enabledTargets = targetRepository.findAllByEnabledTrue();

        if(enabledTargets.isEmpty()) {
            log.warn("exiting... no \"enabled\" targets found");
            return;
        }

        log.info("run job - start");
        ExecutorService executorService = Executors.newFixedThreadPool(enabledTargets.size());

        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executorService;

        log.info("executorService started. coreSize={}, active={}, completedTask={}",
                threadPoolExecutor.getCorePoolSize(), threadPoolExecutor.getActiveCount(), threadPoolExecutor.getCompletedTaskCount());

        for (Target target: enabledTargets) {
            log.info("submit Task({})", target.getEndpoint());
            switch(target.getMethod()) {
                case CURL:
                    log.info("action=submitTask, info=CurlTask");
                    executorService.submit(new CurlTask(target, checkResultRepository, monitorProperties));
                    break;
                case PING:
                    log.info("action=submitTask, info=PingTask");
                    executorService.submit(new PingTask(target, checkResultRepository, monitorProperties));
                    break;
                case TELNET:
                    log.info("action=submitTask, info=TelnetTask");
                    executorService.submit(new TelnetTask(target, checkResultRepository, monitorProperties));
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
