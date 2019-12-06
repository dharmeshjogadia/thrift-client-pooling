package com.github.thrift.pooling;

import stormpot.BlazePool;
import stormpot.Config;
import stormpot.Timeout;

import java.io.Closeable;
import java.io.IOException;

/**
 * Thrift connection factory of type T.
 * Create connection factory of any type using {@link stormpot.Config}
 * Sample code
 * <code>
 *     Config<ThriftConnection<T>> config = new Config<>() ...
 *     ....
 *     ThriftConnectionFactory<T> thriftConnectionFactory = new ThriftConnectionFactory<>(config,
 *                     new Timeout(200, TimeUnit.MILLISECONDS));
 * </code>
 * @see stormpot.BlazePool
 * @see stormpot.Config
 * @see ThriftConnection
 * @param <T> Thrift Service Client Type
 */
public class ThriftConnectionFactory<T> implements Closeable {
    private final BlazePool<ThriftConnection<T>> pool;
    private final Config<ThriftConnection<T>> config;
    private final Timeout connectTimeout;

    public ThriftConnectionFactory(Config<ThriftConnection<T>> config, Timeout connectTimeout) {
        this.config = config;
        this.connectTimeout = connectTimeout;
        this.pool = new BlazePool<>(this.config);
    }

    /**
     * Claim connection from pool with connectTimeout
     * @return {@link ThriftConnection} of type T
     * @throws InterruptedException
     */
    public ThriftConnection<T> getConnection() throws InterruptedException {
        return this.pool.claim(connectTimeout);
    }

    /**
     * Helper function to expose pool for metrics or debugging
     * @return {@link BlazePool} of {@link ThriftConnection} of type T
     */
    public BlazePool<ThriftConnection<T>> getPool() {
        return pool;
    }

    @Override
    public void close() throws IOException {
        this.pool.shutdown();
    }
}
