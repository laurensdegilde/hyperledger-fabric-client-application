package domain;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

import java.io.Serializable;
import java.util.Set;


public class UserWrapper implements User, Serializable {
    
    private static final long serialVersionUID = 1L;
    protected String name;
    protected Set<String> roles;
    protected String account;
    protected String affiliation;
    protected Enrollment enrollment;
    protected String mspId;
    
    @Override
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public Set<String> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
    
    @Override
    public String getAccount() {
        return account;
    }
    
    public void setAccount(String account) {
        this.account = account;
    }
    
    @Override
    public String getAffiliation() {
        return affiliation;
    }
    
    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }
    
    @Override
    public Enrollment getEnrollment() {
        return enrollment;
    }
    
    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }
    
    @Override
    public String getMspId() {
        return mspId;
    }
    
    public void setMspId(String mspId) {
        this.mspId = mspId;
    }
    
}
