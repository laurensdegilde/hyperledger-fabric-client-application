package specification;

public interface NetworkSpecification {
    String[] getAdminOrg1Properties();
    
    String[] getOrdererProperties();
    
    String[] getOrg1Properties();
    
    String[] getOrg2Properties();
    
    String[] getChannelProperties();
    
    String[] getChannelMethodProperties();
}
