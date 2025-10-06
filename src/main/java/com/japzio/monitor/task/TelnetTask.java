package com.japzio.monitor.task;

import com.japzio.monitor.entity.CheckResult;
import com.japzio.monitor.entity.Target;
import com.japzio.monitor.properties.MonitorProperties;
import com.japzio.monitor.repository.CheckResultRepository;
import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.time.Instant;

public class TelnetTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(PingTask.class);

    private final Target target;
    private final CheckResultRepository checkResultRepository;
    private final MonitorProperties monitorProperties;

    public TelnetTask(
           Target target,
           CheckResultRepository checkResultRepository,
           MonitorProperties monitorProperties
    ) {
        this.target = target;
        this.checkResultRepository = checkResultRepository;
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
        String status = "OK";
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
            status = "Error Communicating";
            System.err.println("Error connecting or communicating: " + e.getMessage());
        } finally {
            try {
                if (telnet.isConnected()) {
                    telnet.disconnect();
                }
            } catch (IOException e) {
                status = "Error Disconnecting";
                System.err.println("Error disconnecting: " + e.getMessage());
            }
        }

        log.info("telnet - {}", targetEndpoint);
        log.info("telnet result - {}", status);
        checkResultRepository.save(
                CheckResult.builder()
                        .status(String.valueOf(status))
                        .targetId(target.getId())
                        .createdAt(Timestamp.from(Instant.now()))
                        .build()
        );
        log.info("runnable task - telnet - done");
    }
}
