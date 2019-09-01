package io.grpc.testing.junit;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GrpcServerExtension implements BeforeEachCallback, AfterEachCallback {
    private final static Logger LOGGER = Logger.getLogger(GrpcServerExtension.class.getName());

    private final BindableService bindableSevice;

    private int port;
    private Server server;

    private GrpcServerExtension() {
        throw new IllegalStateException("GrpcServerExtension can only be registered dynamically.");
    }

    private GrpcServerExtension(Builder builder) {
        this.port = builder.port > 0 ? builder.port : retrieveFreePort();
        this.bindableSevice = builder.service;
    }

    private void start() throws IOException {
        server = ServerBuilder
                .forPort(port)
                .addService(bindableSevice)
                .build()
                .start();
        if (server != null) {
            LOGGER.log(Level.INFO, "Server started using port " + port);
        }
    }

    private void shutdown() {
        if (server != null) {
            server.shutdownNow();
        }
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        shutdown();
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        start();
    }

    public int getPort() {
        return port;
    }

    private int retrieveFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving a free port", e);
        }
        return 0;
    }

    public static final class Builder {
        private int port;
        private BindableService service;

        public Builder withPort(int serverPort) {
            this.port = serverPort;
            return this;
        }

        public Builder withBindableService(BindableService service) {
            this.service = service;
            return this;
        }

        public GrpcServerExtension build() {
            return new GrpcServerExtension(this);
        }

    }
}
