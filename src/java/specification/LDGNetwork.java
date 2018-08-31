package specification;

public class LDGNetwork implements NetworkSpecification {

    public String[] getChannelProperties() {
        return new String[]{
            "ldegilde-channel",
            "default-chaincodev4",
            "pmt-chaincodev15"
        };
    }


    public String[] getChannelMethodProperties() {
        return new String[]{
                "get",
                "put"
        };
    }

    public String[] getOrdererProperties(){
        return new String[]{
            "orderer.ldegilde.com",
            "grpc://10.89.65.66:7050"
        };
    }
    public String[] getOrg1Properties(){
        return new String[]{
            "Org1MSP",
            "org1.ldegilde.com",
            "http://10.250.105.53:7054",
            "peer0.org1.ldegilde.com",
            "grpc://10.250.105.53:7051",
            "peer1.org1.ldegilde.com",
            "grpc://10.250.105.53:8051"
        };
    }
    public String[] getOrg2Properties(){
        return new String[]{
            "Org2MPS",
            "org2.ldegilde.com",
            "http://10.233.22.50:7054",
            "peer0.org2.ldegilde.com",
            "grpc://10.233.22.50:7051",
            "peer1.org2.ldegildde.com",
            "grpc://10.233.22.50:8051"
        };
    }
}

