package com.github.thrift;

import org.apache.thrift.protocol.TProtocol;

public class TTestService {
        public static class Client extends org.apache.thrift.TServiceClient {

            public Client(TProtocol prot) {
                super(prot);
            }
        }
    }