package com.github.thrift.client;


import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for Thrift Service Client and Pooling
 */
public class ThriftClientConfiguration {

    private String baseUrl;

    public void setServices(Map<String, ServiceProperties> services) {
        this.services = services;
    }

    private Map<String, ServiceProperties> services = new HashMap<>();

    public Map<String, ServiceProperties> getServices() {
        return services;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Configuration for Thrift Service Client Pooling
     */
    public static class ServiceProperties {
        private int poolSize = 10;
        private int connectionExpireInMills = 60000;
        private int connectionFetchTimeoutInMills = 60000;

        public int getPoolSize() {
            return poolSize;
        }

        public void setPoolSize(int poolSize) {
            this.poolSize = poolSize;
        }

        public int getConnectionExpireInMills() {
            return connectionExpireInMills;
        }

        public void setConnectionExpireInMills(int connectionExpireInMills) {
            this.connectionExpireInMills = connectionExpireInMills;
        }

        public int getConnectionFetchTimeoutInMills() {
            return connectionFetchTimeoutInMills;
        }

        public void setConnectionFetchTimeoutInMills(int connectionFetchTimeoutInMills) {
            this.connectionFetchTimeoutInMills = connectionFetchTimeoutInMills;
        }
    }
}
