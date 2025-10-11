package com.japzio.monitor.task;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.japzio.monitor.model.internal.CheckResultsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseTask {

    private static final Logger log = LoggerFactory.getLogger(BaseTask.class);
    private final InfluxDBClient influxDBClient;

    public BaseTask(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    protected void saveCheckResult(CheckResultsDto checkResultsDto) {
        log.info("action=saveCheckResult, info=start, targetId={}", checkResultsDto.getTargetId());

        try (WriteApi writeApi = influxDBClient.getWriteApi()) {

            // Create a data point
            Point point = Point.measurement("check_results")
                    .addTag("endpoint", checkResultsDto.getEndpoint())
                    .addTag("method", checkResultsDto.getMethod())
                    .addTag("description", checkResultsDto.getDescription())
                    .addTag("success", checkResultsDto.getSuccess().toString())
                    .addField("response_time", checkResultsDto.getDuration())
                    .time(System.currentTimeMillis(), WritePrecision.MS);

            writeApi.writePoint(point);

            log.info("action=saveCheckResult, info=done, dataPoint={}", point.toLineProtocol());

        } catch (Exception e) {
            // Log error (e.g., using SLF4J)
            log.error("action=saveCheckResult, error=writeException, message={}", e.getMessage());
        }
    }

}
