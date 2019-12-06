package com.github.thrift.client;

import com.github.thrift.TTestService;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ThriftClientFactoryTest {
    private static final String SERVICE_NAME = "TestService";
    @Mock
    TProtocolFactory protocolFactory;
    @Mock
    TProtocol tTransport;
    private ThriftClientFactory thriftClientFactory;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(protocolFactory.getProtocol(Mockito.any())).thenReturn(tTransport);
    }
    @Test
    public void testConstructClient() throws Exception {
        // Setup
        thriftClientFactory = new ThriftClientFactory(protocolFactory, "http://localhost:8080");

        // Run the test
        final TTestService.Client result = thriftClientFactory.constructClient(TTestService.Client.class, SERVICE_NAME);

        // Verify the results
        Assert.assertNotNull(result);
    }

    @Test
    public void testConstructClient_ThrowsThriftClientException() throws Exception {
        // Setup
        thriftClientFactory = new ThriftClientFactory(protocolFactory, "http://localhost:8080");
        String expectedMsg = "The clazz " + Object.class.getName() + " is not subtype of org.apache.thrift.TServiceClient.class";
        // Run the test
        try {
            thriftClientFactory.constructClient(Object.class, SERVICE_NAME);
        } catch (ThriftClientException e) {
            Assert.assertEquals(expectedMsg, e.getMessage());
        }
    }

    @Test(expected = ThriftClientException.class)
    public void testConstructClient_ThrowsThriftClientException1() throws Exception {
        // Setup
        thriftClientFactory = new ThriftClientFactory(protocolFactory, "testHostUrl");

        // Run the test
        thriftClientFactory.constructClient(TTestService.Client.class, SERVICE_NAME);
    }



    @Test(expected = ThriftClientException.class)
    public void testConstructClient_ThrowsThriftClientException2() throws Exception {
        class TestClass extends TServiceClient {
            public TestClass() {
                super(null);
            }
        }
        // Setup
        thriftClientFactory = new ThriftClientFactory(protocolFactory, "testHostUrl");

        // Run the test
        thriftClientFactory.constructClient(TestClass.class, SERVICE_NAME);
    }
}