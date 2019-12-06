package com.github.thrift.client;

public class ThriftClientException extends Exception {
    public ThriftClientException(Throwable t) {
        super(t);
    }

    public ThriftClientException(String errMsg) {
        super(errMsg);
    }
}
