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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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

        if (enabledTargets.isEmpty()) {
            log.warn("exiting... no \"enabled\" targets found");
            return;
        }

        log.info("run job - start");
        // Cap thread pool size to prevent resource exhaustion
        int MAX_THREADS = 10; // Adjust based on system capacity
        try (ExecutorService executorService = Executors.newFixedThreadPool(Math.min(enabledTargets.size(), MAX_THREADS))) {
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executorService;
            List<Future<?>> futures = new ArrayList<>();

            // Submit tasks and track their Futures
            for (Target target : enabledTargets) {
                log.info("submit Task({})", target.getEndpoint());
                try {
                    switch (target.getMethod()) {
                        case CURL:
                            log.info("action=submitTask, info=CurlTask");
                            futures.add(executorService.submit(new CurlTask(target, checkResultRepository, monitorProperties)));
                            break;
                        case PING:
                            log.info("action=submitTask, info=PingTask");
                            futures.add(executorService.submit(new PingTask(target, checkResultRepository, monitorProperties)));
                            break;
                        case TELNET:
                            log.info("action=submitTask, info=TelnetTask");
                            futures.add(executorService.submit(new TelnetTask(target, checkResultRepository, monitorProperties)));
                            break;
                        default:
                            log.warn("unsupported method: {}", target.getMethod());
                    }
                } catch (Exception e) {
                    log.error("Failed to submit task for target: {}", target.getEndpoint(), e);
                }
            }

            // Log thread pool stats after submitting tasks
            log.info("executorService tasks submitted. coreSize={}, active={}, completedTask={}",
                    threadPoolExecutor.getCorePoolSize(), threadPoolExecutor.getActiveCount(), threadPoolExecutor.getCompletedTaskCount());

            // Wait for tasks to complete with a timeout aligned to max task duration
            executorService.shutdown();
            try {
                // Allow up to 32 seconds to account for task timeout (30s) + scheduling overhead
                if (!executorService.awaitTermination(monitorProperties.getMaxTimeout(), TimeUnit.SECONDS)) {
                    log.warn("Tasks did not complete within timeout, forcing shutdown");
                    executorService.shutdownNow();
                    // Log tasks that were interrupted
                    for (Future<?> future : futures) {
                        if (!future.isDone()) {
                            log.warn("Task interrupted: {}", future);
                        }
                    }
                }
            } catch (InterruptedException e) {
                log.warn("Interrupted while waiting for tasks to complete");
                executorService.shutdownNow();
                Thread.currentThread().interrupt(); // Restore interrupted status
            }

            // Check for task failures
            for (Future<?> future : futures) {
                try {
                    future.get(0, TimeUnit.SECONDS); // Check if task completed successfully
                } catch (ExecutionException e) {
                    log.error("Task failed with exception", e.getCause());
                } catch (Exception e) {
                    log.error("Error checking task completion", e);
                }
            }

        } catch (Exception e) {
            log.error("Unexpected error during job execution", e);
        }

        log.info("run job - done");
    }
}
