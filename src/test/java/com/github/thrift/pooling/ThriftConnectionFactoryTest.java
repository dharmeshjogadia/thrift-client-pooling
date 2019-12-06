package com.github.thrift.pooling;

import com.github.thrift.TTestService;
import com.github.thrift.client.ThriftClientConfiguration;
import com.github.thrift.client.ThriftClientFactory;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TTransport;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.yaml.snakeyaml.Yaml;
import stormpot.Config;
import stormpot.TimeExpiration;
import stormpot.Timeout;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ThriftConnectionFactoryTest {
    public static final int POOL_SIZE = 5;
    private static final String SERVICE_NAME = "TestService";
    @Mock
    TProtocolFactory protocolFactory;
    @Mock
    TProtocol tProtocol;
    @Mock
    TTransport tTransport;
    ThriftClientFactory thriftClientFactory;
    ThriftConnectionFactory<TTestService.Client> thriftConnectionFactory;
    ThriftClientConfiguration configuration;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        Mockito.doNothing().when(tTransport).close();
        Mockito.when(tProtocol.getTransport()).thenReturn(tTransport);
        Mockito.when(protocolFactory.getProtocol(Mockito.any())).thenReturn(tProtocol);

        configuration = thriftClientConfigurationYaml();
        thriftClientFactory = new ThriftClientFactory(protocolFactory, configuration.getBaseUrl());

        ThriftClientConfiguration.ServiceProperties serviceProperties = configuration.getServices().get(SERVICE_NAME);
        Config<ThriftConnection<TTestService.Client>> config = new Config<>()
                .setAllocator(
                        new ThriftConnectionAllocator<>(thriftClientFactory, SERVICE_NAME, TTestService.Client.class));
        config.setSize(serviceProperties.getPoolSize());
        config.setExpiration(new TimeExpiration<>(serviceProperties.getConnectionExpireInMills(),
                TimeUnit.MILLISECONDS));
        thriftConnectionFactory = new ThriftConnectionFactory<>(config,
                new Timeout(serviceProperties.getConnectionFetchTimeoutInMills(), TimeUnit.MILLISECONDS));
    }

    @Test
    public void testGetConnection() throws Exception {
        // Run the test
        ThriftConnection<TTestService.Client> result = thriftConnectionFactory.getConnection();
        // Verify the results
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getClient());
        Assert.assertTrue(thriftConnectionFactory.getPool().getAllocationCount() > 0);
        Assert.assertEquals(POOL_SIZE, thriftConnectionFactory.getPool().getTargetSize());
        // close
        result.release();
        result.close();
    }

    private ThriftClientConfiguration thriftClientConfiguration() {
        ThriftClientConfiguration configuration = new ThriftClientConfiguration();
        configuration.setBaseUrl("http://localhost:8080");
        Map<String, ThriftClientConfiguration.ServiceProperties>
                servicePropertiesMap = new HashMap<>();
        ThriftClientConfiguration.ServiceProperties serviceProperties =
                new ThriftClientConfiguration.ServiceProperties();
        serviceProperties.setPoolSize(POOL_SIZE);
        serviceProperties.setConnectionExpireInMills(1000);
        serviceProperties.setConnectionFetchTimeoutInMills(1000);
        servicePropertiesMap.put(SERVICE_NAME, serviceProperties);
        configuration.setServices(servicePropertiesMap);
        return configuration;
    }

    private ThriftClientConfiguration thriftClientConfigurationYaml() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("thriftClientConfig.yaml");
        return yaml.load(inputStream);
    }

    @After
    public void after() throws IOException {
        thriftConnectionFactory.close();
    }
}