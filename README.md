# thrift-client-pooling
Thrift service client pooling using stormpot

## Example
#### Configuration 
I am using YAML configuraiton to load thrift service client pooling 

```yaml
!!com.github.thrift.client.ThriftClientConfiguration
baseUrl: "http://localhost:8080"
services:
 TestService:
    poolSize: 5
    connectionExpireInMills: 1000
    connectionFetchTimeoutInMills: 1000
 FakeService1:
    .....
```

#### Thrift service client pooling sample code
```java
public static final int POOL_SIZE = 5;
private static final String SERVICE_NAME = "TestService";
private ThriftClientConfiguration thriftClientConfigurationYaml() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("thriftClientConfig.yaml");
        return yaml.load(inputStream);
}

public void testGetConnection() throws Exception {
    // Initlize Thrift Connection Factory for TestService
    configuration = thriftClientConfigurationYaml();
    thriftClientFactory = new ThriftClientFactory(protocolFactory, configuration.getBaseUrl());

    ThriftClientConfiguration.ServiceProperties serviceProperties = configuration.getServices().get(SERVICE_NAME);
    Config<ThriftConnection<TTestService.Client>> config = new Config<>().setAllocator(
                                new ThriftConnectionAllocator<>(thriftClientFactory, SERVICE_NAME, TTestService.Client.class));
    config.setSize(serviceProperties.getPoolSize());
    config.setExpiration(new TimeExpiration<>(serviceProperties.getConnectionExpireInMills(),
            TimeUnit.MILLISECONDS));
    thriftConnectionFactory = new ThriftConnectionFactory<>(config,
                    new Timeout(serviceProperties.getConnectionFetchTimeoutInMills(), TimeUnit.MILLISECONDS));
            
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
```

