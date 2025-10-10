package com.japzio.monitor.task;

import com.influxdb.client.InfluxDBClient;
import com.japzio.monitor.model.entity.Target;
import com.japzio.monitor.model.CheckResultsStatus;
import com.japzio.monitor.model.internal.CheckResultsDto;
import com.japzio.monitor.properties.MonitorProperties;
import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;

public class TelnetTask  extends BaseTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(PingTask.class);

    private final Target target;
    private final MonitorProperties monitorProperties;

    public TelnetTask(
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
        TelnetClient telnet = new TelnetClient();
        telnet.setDefaultTimeout(Math.toIntExact(timeout));
        log.info("runnable task - telnet - start targetId={}", target.getId());
        log.info("curl task={}, timeout={}", targetEndpoint, timeout);
        var success = false;
        var description = "";
        var start = Instant.now();
        try {
            String[] targetEndpointSplit = targetEndpoint.split(":");
            if(targetEndpointSplit.length != 2) {
                throw new IllegalArgumentException("Invalid host:port format for Telnet task. actual=" + targetEndpoint);
            }

            telnet.connect(targetEndpointSplit[0], Integer.parseInt(targetEndpointSplit[1]));
            InputStream in = telnet.getInputStream();
            OutputStream out = telnet.getOutputStream();

            // Now you can read from 'in' and write to 'out' to interact with the Telnet server
            // Example: Sending a command and reading the response
            String command = "your_command\n"; // Don't forget the newline
            out.write(command.getBytes());
            out.flush();

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                System.out.print(new String(buffer, 0, bytesRead));
                // You might need more sophisticated logic to determine when the response is complete
            }
            success = true;
        } catch (IOException e) {
            description = "Error connecting or communicating";
            log.error("Error connecting or communicating: {}", e.getMessage());
        } finally {
            try {
                if (telnet.isConnected()) {
                    telnet.disconnect();
                }
            } catch (IOException e) {
                description = "Error disconnecting";
                log.error("Error disconnecting: {}", e.getMessage());
            }
        }

        log.info("telnet - {}", targetEndpoint);
        var duration = Duration.between(start, Instant.now()).getSeconds();
        log.info("telnet result success - {}, duration={}", success, duration);
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
        log.info("runnable task - telnet - done");
    }
}
