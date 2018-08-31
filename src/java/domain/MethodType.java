package domain;

public enum MethodType {
    PUT("put"),
    GET("get");
    
    private String text;
    
    MethodType(String text) {
        this.text = text;
    }
    
    public static MethodType fromString(String text) {
        for (MethodType b : MethodType.values()) {
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
