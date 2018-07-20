package specification;

public interface NetworkSpecification {
	
	String getORG1_MSP();
	String getORG1();
	String getAdmin();
	String getAdminPassword();

	String getBasePath();
	String getNetworkPath();
	String getNetworkFolderName();
	String getChannelConfigurationFolderName();

	String getChannelConfigurationPath();
	String getOrg1UsrAdminKeyStorePath();
	String getOrg1UsrAdminCertificatePath();

	String getOrdererBasePath();
	String getOrdererUrl();
	String getOrdererName();

	String getCAOrg1URL();

    String getOrg1Name();
}
