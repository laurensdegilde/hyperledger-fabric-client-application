package trie;

import util.CompactEncoder;

import java.util.Arrays;
import java.util.List;

public class Node {

    private Object value;

    public Node(Object obj) {

        if (obj == null) return;

        if (obj instanceof Node) {
            this.value = ((Node) obj).asObj();
        } else {
            this.value = obj;
        }
    }

    public Object asObj() {
        return value;
    }

    public List<Object> asList() {
        Object[] valueArray = (Object[]) value;
        return Arrays.asList(valueArray);
    }

    public String asString() {
        if (isBytes()) {
            return new String((byte[]) value);
        } else if (isString()) {
            return (String) value;
        }
        return "";
    }

    public byte[] asBytes() {
        if(isBytes()) {
            return (byte[]) value;
        } else if(isString()) {
            return asString().getBytes();
        }
        return CompactEncoder.EMPTY_BYTE_ARRAY;
    }

    public Node get(int index) {
        if(isList()) {
            if (asList().size() <= index) {
                return new Node(null);
            }
            if (index < 0) {
                throw new RuntimeException("Negative index not allowed");
            }
            return new Node(asList().get(index));
        }
        return new Node(null);
    }

    public boolean isList() {
        return value != null && value.getClass().isArray() && !value.getClass().getComponentType().isPrimitive();
    }

    public boolean isString() {
        return value instanceof String;
    }

    public boolean isBytes() {
        return value instanceof byte[];
    }

    public boolean isNull() {
        return value == null;
    }

    public int length() {
        if (isList()) {
            return asList().size();
        } else if (isBytes()) {
            return asBytes().length;
        } else if (isString()) {
            return asString().length();
        }
        return 0;
    }
}
