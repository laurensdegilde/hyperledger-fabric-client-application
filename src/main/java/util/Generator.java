package util;

public class Generator {

    public final int AMOUNT_OF_DECLARATIONS = 320000;
    public final int AMOUNT_OF_INVALID_OPTICAL_DECLARATIONS = 2000;
    public final int AMOUNT_OF_VALID_OPTICAL_DECLARATIONS = 1200;

    public Generator(){

    }

    public String [] getNewRandomDataRecord(){
        return new String [] { "test1", "test2", "test3", "test4", "test5"};
    }

}
