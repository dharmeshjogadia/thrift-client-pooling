package com.github.thrift.client;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.lang.reflect.InvocationTargetException;

/**
 * Factory class to construct any type of Thrift Service Client based on {@link TProtocolFactory}
 * <code>
 * thriftClientFactory = new ThriftClientFactory(protocolFactory, "http://localhost:8080");
 * final TTestService.Client result = thriftClientFactory.constructClient(TTestService.Client.class, SERVICE_NAME);
 * </code>
 *
 * @see org.apache.thrift.protocol.TProtocolFactory
 */
public class ThriftClientFactory {
    private static final String THRIFT_URL_PATH = "/thrift";
    private final TProtocolFactory protocolFactory;
    private final String hostUrl;

    public ThriftClientFactory(TProtocolFactory protocolFactory, String hostUrl) {
        this.protocolFactory = protocolFactory;
        this.hostUrl = hostUrl;
    }

    private TMultiplexedProtocol getClientProtocol(String serviceName) throws TTransportException {
        TTransport transport = new THttpClient(hostUrl + THRIFT_URL_PATH);
        TProtocol protocol = protocolFactory.getProtocol(transport);
        return new TMultiplexedProtocol(protocol, serviceName);
    }

    /**
     * Construct Thrift Service Client of any type based on params
     *
     * @param clazz   Thrift Service Client Class
     * @param service Thrift Service Name
     * @param <T>     Type of Thrift Service Client
     * @return newly constructed Thrift Service Client of type T
     * @throws ThriftClientException
     */
    public <T> T constructClient(Class<T> clazz, String service) throws ThriftClientException {
        if (!TServiceClient.class.isAssignableFrom(clazz)) {
            throw new ThriftClientException(
                    "The clazz " + clazz.getName() + " is not subtype of org.apache.thrift.TServiceClient.class");
        }
        try {
            return clazz.getDeclaredConstructor(TProtocol.class).newInstance(getClientProtocol(service));
        } catch (TTransportException e) {
            throw new ThriftClientException(e.getCause());
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException | NoSuchMethodException e) {
            throw new ThriftClientException(e);
        }
    }
}
