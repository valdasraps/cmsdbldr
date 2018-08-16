package org.cern.cms.dbloader.rest;

import java.io.IOException;
import org.apache.log4j.LogManager;
import org.eclipse.jetty.server.AbstractNCSARequestLog;
import org.eclipse.jetty.util.log.AbstractLogger;

/**
 * Jetty logger bridge
 * @author valdo
 */
public class LogFactory {

    public static RequestLogger createRequestLogger() {
        return new RequestLogger(Application.class);
    }
    
    public static class RequestLogger extends AbstractNCSARequestLog {
    
        private final org.apache.log4j.Logger logger;
        
        public RequestLogger(Class clazz) {
            this.logger = LogManager.getLogger(clazz.getCanonicalName());
        }
        
        @Override
        protected boolean isEnabled() {
            return true;
        }

        @Override
        public void write(String s) throws IOException {
            logger.info(s);
        }
        
    }

    public static Logger createLogger() {
        return new Logger(Application.class.getCanonicalName());
    }
    
    public static class Logger extends AbstractLogger {

        private final org.apache.log4j.Logger logger;
        private final String name;

        public Logger(String name) {
            this.name = name;
            this.logger = LogManager.getLogger(name);
        }

        @Override
        protected org.eclipse.jetty.util.log.Logger newLogger(String name) {
            return new Logger(name);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void warn(String msg, Object... ps) {
            logger.warn(String.format(msg, ps));
        }

        @Override
        public void warn(Throwable ex) {
            logger.warn(ex.getMessage(), ex);
        }

        @Override
        public void warn(String msg, Throwable ex) {
            logger.warn(msg, ex);
        }

        @Override
        public void info(String msg, Object... ps) {
            logger.info(String.format(msg, ps));
        }

        @Override
        public void info(Throwable ex) {
            logger.info(ex.getMessage(), ex);
        }

        @Override
        public void info(String msg, Throwable ex) {
            logger.warn(msg, ex);
        }

        @Override
        public boolean isDebugEnabled() {
            return logger.isDebugEnabled();
        }

        @Override
        public void setDebugEnabled(boolean bln) {
            warn("setDebugEnabled not implemented");
        }

        @Override
        public void debug(String msg, Object... ps) {
            logger.debug(String.format(msg, ps));
        }

        @Override
        public void debug(Throwable ex) {
            logger.debug(ex.getMessage(), ex);
        }

        @Override
        public void debug(String msg, Throwable ex) {
            logger.debug(msg, ex);
        }

        @Override
        public void ignore(Throwable ex) {
            logger.trace(ex.getMessage(), ex);
        }

    }
        
}
