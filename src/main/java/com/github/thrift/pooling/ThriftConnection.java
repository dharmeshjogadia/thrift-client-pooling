package com.github.thrift.pooling;

import com.github.thrift.client.ThriftClientException;
import com.github.thrift.client.ThriftClientFactory;
import org.apache.thrift.TServiceClient;
import stormpot.Poolable;
import stormpot.Slot;

/**
 * This class is Poolable implementation of Thrift Service Client of Type T
 * @see stormpot.Poolable
 * @see stormpot.Slot
 * @see ThriftConnectionFactory
 * @param <T> Thrift Service Client Type
 */
public class ThriftConnection<T> implements Poolable {
    private final T serviceClient;
    private final Slot slot;

    public ThriftConnection(Slot slot, ThriftClientFactory thriftClientFactory, Class<T> clazz, String service)
            throws ThriftClientException {
        this.slot = slot;
        this.serviceClient = thriftClientFactory.constructClient(clazz, service);
    }

    public T getClient() {
        return serviceClient;
    }

    public void close() {
        if (serviceClient instanceof TServiceClient) {
            (((TServiceClient) serviceClient).getInputProtocol()).getTransport().close();
        }
    }

    @Override
    public void release() {
        if (slot != null) {
            slot.release(this);
        }
    }
}
