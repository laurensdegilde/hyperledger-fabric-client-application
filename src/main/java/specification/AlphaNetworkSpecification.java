package specification;


import java.io.File;

public class AlphaNetworkSpecification implements NetworkSpecification {

    public String getORG1_MSP() {
        return "Org1MSP";
    }

    public String getORG1() {
        return "org1";
    }

    public String getAdmin() {
        return "admin";
    }

    public String getAdminPassword() {
        return "adminpw";
    }

    public String getBasePath() {
        return "C:\\Users\\laure";
    }

    public String getNetworkPath() {
        return "fabric-networks";
    }

    public String getNetworkFolderName() {
        return "practice-network-v2";
    }

    public String getChannelConfigurationFolderName() {
        return "channel-artifacts";
    }

    public String getCryptoConfigurationFolderName(){
        return "crypto-config";
    }

    public String getChannelConfigurationPath() {
        return this.getBasePath() + File.separator + this.getNetworkPath() + File.separator + this.getChannelConfigurationFolderName();
    }
    public String getOrg1UserAdminBasePath(){
        return this.getBasePath() + File.separator + this.getNetworkPath() + File.separator
                + this.getNetworkFolderName() + File.separator + this.getCryptoConfigurationFolderName() + File.separator +
                "peerOrganizations" + File.separator + this.getOrg1Name() + File.separator + "users\\Admin@org1.ldegilde.com\\msp";

    }
    public String getOrdererBasePath(){
        return this.getBasePath() + File.separator + this.getNetworkPath() + File.separator
                + this.getCryptoConfigurationFolderName() + File.separator +
                "ordererOrganizations\\ldegilde.com\\orderers\\orderer.ldegilde.com\\tls";

    }
    public String getOrg1UsrAdminKeyStorePath() {
        return getOrg1UserAdminBasePath() + File.separator + "keystore";
    }

    public String getOrg1UsrAdminCertificatePath() {
        return getOrg1UserAdminBasePath() + File.separator + "signcerts";
    }

    public String getOrdererUrl() {
        return "grpc://192.168.99.100:7050";
    }

    public String getOrdererName() {
        return "orderer.ldegilde.com";
    }

    public String getOrg1Name(){
        return "org1.ldegilde.com";
    }

    public String getCAOrg1URL(){ return "http://192.168.99.100:7054";}
}
