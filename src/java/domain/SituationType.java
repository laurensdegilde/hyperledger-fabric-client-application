package domain;

public enum SituationType {
    DEFAULT("default-chaincodev5"),
    PMT("pmt-chaincodev15");
    
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
