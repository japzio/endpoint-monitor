package com.japzio.monitor.task;

import com.japzio.monitor.entity.CheckResult;
import com.japzio.monitor.repository.CheckResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTask {

    private static final Logger log = LoggerFactory.getLogger(BaseTask.class);

    private final CheckResultRepository checkResultRepository;

    public BaseTask(CheckResultRepository checkResultRepository) {
        this.checkResultRepository = checkResultRepository;
    }

    protected void saveCheckResult(CheckResult checkResult) {
        log.info("action=saveCheckResult, info=start, targetId={}, resultId{}",
                checkResult.getTargetId(),  checkResult.getId());
        checkResultRepository.save(checkResult);
        log.info("action=saveCheckResult, info=done");
    }

}
