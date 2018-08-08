package util;

import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;
import static org.bouncycastle.pqc.math.linearalgebra.ByteUtils.concatenate;
import static org.bouncycastle.util.encoders.Hex.encode;


import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NibbleEncoder {

    private final static byte TERMINATOR = 16;
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private final static Map<Character, Byte> hexMap = new HashMap<>();
    static {
        hexMap.put('0', (byte)0x0);
        hexMap.put('1', (byte)0x1);
        hexMap.put('2', (byte)0x2);
        hexMap.put('3', (byte)0x3);
        hexMap.put('4', (byte)0x4);
        hexMap.put('5', (byte)0x5);
        hexMap.put('6', (byte)0x6);
        hexMap.put('7', (byte)0x7);
        hexMap.put('8', (byte)0x8);
        hexMap.put('9', (byte)0x9);
        hexMap.put('a', (byte)0xa);
        hexMap.put('b', (byte)0xb);
        hexMap.put('c', (byte)0xc);
        hexMap.put('d', (byte)0xd);
        hexMap.put('e', (byte)0xe);
        hexMap.put('f', (byte)0xf);
    }

    /**
     * Pack nibbles to binary
     *
     * @param nibbles sequence. may have a terminator
     * @return hex-encoded byte array
     */
    public static byte[] packNibbles(byte[] nibbles) {
        int terminator = 0;

        if (nibbles[nibbles.length-1] == TERMINATOR) {
            terminator = 1;
            nibbles = copyOf(nibbles, nibbles.length-1);
        }
        int oddlen = nibbles.length % 2;
        int flag = 2*terminator + oddlen;
        if (oddlen != 0) {
            byte[] flags = new byte[] { (byte) flag};
            nibbles = concatenate(flags, nibbles);
        } else {
            byte[] flags = new byte[] { (byte) flag, 0};
            nibbles = concatenate(flags, nibbles);
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (int i = 0; i < nibbles.length; i += 2) {
            buffer.write(16*nibbles[i] + nibbles[i+1]);
        }
        return buffer.toByteArray();
    }

    /**
     * Unpack a binary string to its nibbles equivalent
     *
     * @param str of binary data
     * @return array of nibbles in byte-format
     */
    public static byte[] unpackToNibbles(byte[] str) {
        byte[] base = binToNibbles(str);
        base = copyOf(base, base.length - 1);
        if (base[0] >= 2) {
            base = appendByte(base, TERMINATOR);
        }
        if (base[0] % 2 == 1) {
            base = copyOfRange(base, 1, base.length);
        } else {
            base = copyOfRange(base, 2, base.length);
        }
        return base;
    }

    /**
     * Transforms a binary array to hexadecimal format + terminator
     *
     * @param str byte[]
     * @return array with each individual nibble adding a terminator at the end
     */
    public static byte[] binToNibbles(byte[] str) {
        byte[] hexEncoded = encode(str);
        ByteBuffer slice = ByteBuffer.allocate(hexEncoded.length + 1);
        for (byte b : hexEncoded) {
            slice.put(hexMap.get((char)b));
        }
        slice.put(TERMINATOR);
        return slice.array();
    }

    public static byte[] binToNibblesNoTerminator(byte[] str) {
        byte[] hexEncoded = encode(str);
        ByteBuffer slice = ByteBuffer.allocate(hexEncoded.length);
        for (byte b : hexEncoded) {
            slice.put(hexMap.get((char)b));
        }
        return slice.array();
    }

    public static byte[] appendByte(byte[] bytes, byte b) {
        byte[] result = Arrays.copyOf(bytes, bytes.length + 1);
        result[result.length - 1] = b;
        return result;
    }
    public static String nibblesToPrettyString(byte[] nibbles){
        StringBuffer buffer = new StringBuffer();
        for (byte nibble : nibbles) {
            String nibleString = oneByteToHexString(nibble);
            buffer.append("\\x" + nibleString);
        }
        return buffer.toString();
    }
    public static String oneByteToHexString(byte value) {
        String retVal = Integer.toString(value & 0xFF, 16);
        if (retVal.length() == 1) retVal = "0" + retVal;
        return retVal;
    }

}
