package com.japzio.monitor.task;

import com.japzio.monitor.entity.CheckResult;
import com.japzio.monitor.entity.Target;
import com.japzio.monitor.model.CheckResultsStatus;
import com.japzio.monitor.properties.MonitorProperties;
import com.japzio.monitor.repository.CheckResultRepository;
import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

public class TelnetTask  extends BaseTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(PingTask.class);

    private final Target target;
    private final MonitorProperties monitorProperties;

    public TelnetTask(
           Target target,
           CheckResultRepository checkResultRepository,
           MonitorProperties monitorProperties
    ) {
        super(checkResultRepository);
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
        var status = CheckResultsStatus.OK.name();
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

        } catch (IOException e) {
            status = CheckResultsStatus.NOT_OK.name();
            description = "Error connecting or communicating";
            log.error("Error connecting or communicating: {}", e.getMessage());
        } finally {
            try {
                if (telnet.isConnected()) {
                    telnet.disconnect();
                }
            } catch (IOException e) {
                status = CheckResultsStatus.NOT_OK.name();
                description = "Error disconnecting";
                log.error("Error disconnecting: {}", e.getMessage());
            }
        }

        log.info("telnet - {}", targetEndpoint);
        log.info("telnet result - {}", status);
        var duration = Duration.between(start, Instant.now()).getSeconds();
        saveCheckResult(
                CheckResult.builder()
                        .status(status)
                        .targetId(target.getId())
                        .duration(status.equals(CheckResultsStatus.OK.name()) ? (int) duration : null)
                        .createdAt(Timestamp.from(Instant.now()))
                        .description(status.equalsIgnoreCase(CheckResultsStatus.NOT_OK.name())? description : "")
                        .build()
        );
        log.info("runnable task - telnet - done");
    }
}
