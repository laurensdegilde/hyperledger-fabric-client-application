package specification;

public interface NetworkSpecification {
    String[] getAdminOrg1Properties();
    
    String[] getOrdererProperties();
    
    String[] getOrgProperties();
    
    String[] getChannelProperties();
    
    String[] getChannelMethodProperties();
}
