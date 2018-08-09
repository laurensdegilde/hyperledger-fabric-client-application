package util.rlp;

import trie.Value;

public class RLPHelper {


    public static Value decode(byte[] data) {
        if (data != null && data.length != 0) {
            return new Value(RLP.decode(data, 0).getDecoded());
        } return null;
    }

    public static byte[] encode(Value value) {
        return RLP.encode(value);
    }
}
