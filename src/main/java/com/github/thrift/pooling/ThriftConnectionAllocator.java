package com.github.thrift.pooling;

import com.github.thrift.client.ThriftClientFactory;
import stormpot.Allocator;
import stormpot.Slot;

/**
 * This class is allocator of ThriftConnection of Type T
 * <code>
 *     thriftClientFactory = new ThriftClientFactory(...)
 *     Config<ThriftConnection<T>> config = new Config<>()
 *                       .setAllocator(
 *                       new ThriftConnectionAllocator<>(thriftClientFactory, serviceName,
 *                       TTestService.Client.class))
 *
 * </code>
 * @see stormpot.Allocator
 * @see stormpot.Config#setAllocator(Allocator)
 * @see ThriftConnection
 * @see com.github.thrift.client.ThriftClientFactory
 * @param <T> Thrift Service Client Type
 */
public class ThriftConnectionAllocator<T> implements Allocator<ThriftConnection<T>> {

    private final ThriftClientFactory thriftClientFactory;
    private final String service;
    private final Class<T> clazz;

    public ThriftConnectionAllocator(ThriftClientFactory thriftClientFactory, String service, Class<T> clazz) {
        this.thriftClientFactory = thriftClientFactory;
        this.service = service;
        this.clazz = clazz;
    }

    @Override
    public ThriftConnection<T> allocate(Slot slot) throws Exception {
        return new ThriftConnection<>(slot, thriftClientFactory, clazz, service);
    }

    @Override
    public void deallocate(ThriftConnection<T> poolable) throws Exception {
        poolable.close();
    }
}
