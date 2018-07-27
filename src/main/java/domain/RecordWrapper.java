package domain;

import java.util.Date;

public class RecordWrapper {

    private String healthInsuredIdentification;
    private String agbCode;
    private String serviceCode;
    private Date serviceDate;
    private boolean isAcknowledged;

    public RecordWrapper(String agbCode, String serviceCode, boolean isAcknowledged) {
        this.agbCode = agbCode;
        this.serviceCode = serviceCode;
        this.isAcknowledged = isAcknowledged;
    }

    public RecordWrapper(String healthInsuredIdentification, String agbCode, String serviceCode, Date serviceDate, boolean isAcknowledged) {
        this.healthInsuredIdentification = healthInsuredIdentification;
        this.agbCode = agbCode;
        this.serviceCode = serviceCode;
        this.serviceDate = serviceDate;
        this.isAcknowledged = isAcknowledged;
    }


    @Override
    public String toString(){
        return "AGB code: " + this.agbCode + " service code: " + this.serviceCode + " is acknowledged: " + this.isAcknowledged;
    }
}
