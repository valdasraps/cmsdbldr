package org.cern.cms.dbloader.rest;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

@Log4j
public class LoaderApplicationExecutor {

    private final static int PORT_FROM = 8080;
    private final static String CLASS_NAME = LoaderApplication.class.getCanonicalName();
    private final static String JAVA_HOME = System.getProperty("java.home");
    private final static String JAVA_BIN = JAVA_HOME + File.separator + "bin" + File.separator + "java";
    private final static String CLASS_PATH = System.getProperty("java.class.path");

    @Getter
    private final String propertiesFile;
    @Getter
    private int port = 0;
    private Process proc;

    public LoaderApplicationExecutor(String propertiesFile) {
        this.propertiesFile = propertiesFile;
        log.info(String.format("Loader descriptor loaded from: %s", propertiesFile));
    }

    public void start() throws Exception {
        this.port = nextFreePort();
        this.proc = new ProcessBuilder(JAVA_BIN, "-cp", CLASS_PATH, CLASS_NAME, String.valueOf(port), propertiesFile).start();
    }

    public void stop() {
        if (this.proc != null) {
            this.proc.destroy();
            this.proc = null;
        }
    }

    public boolean isRunning() {
        return proc != null && proc.isAlive();
    }

    private static int nextFreePort() {
        int port = PORT_FROM;
        while (true) {
            try {
                new ServerSocket(port).close();
                return port;
            } catch (IOException e) {
                port += 1;
            }
        }
    }

}