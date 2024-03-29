package domain;


import network.NetworkExposure;

public enum SituationType {
    
    DEFAULT(NetworkExposure.getSpecification().getChannelProperties()[1]),
    PMT(NetworkExposure.getSpecification().getChannelProperties()[2]);
    
    private String text;
    
    SituationType(String text) {
        this.text = text;
    }
    
    public static SituationType fromString(String text) {
        for (SituationType b : SituationType.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
    
    public String getText() {
        return this.text;
    }
}
