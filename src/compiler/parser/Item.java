package compiler.parser;


public class Item {

    private Object obj;
    private int intValue;
    private float realValue;
    private String charValue;
    private boolean boolValue;
    private int type;

    public Item(Object obj) {
        this.intValue = 0;
        this.realValue = (float) 0.0;
        this.charValue = "";
        this.boolValue = false;
        this.obj = obj;

        if (obj instanceof Integer) {
            this.intValue = (int) obj;
            type = TipoDeDado.TYPE_INT;
        } else if (obj instanceof Float) {
            this.realValue = (float) obj;
            type = TipoDeDado.TYPE_REAL;
        } else if (obj instanceof String) {
            this.charValue = (String) obj;
            type = TipoDeDado.TYPE_STRING;
        } else if (obj instanceof Boolean) {
            type = TipoDeDado.TYPE_BOOLEAN;
            this.boolValue = (boolean) obj;
        }
    }

    public Object getObj() {

        switch (this.type) {
            case TipoDeDado.TYPE_INT:
                return intValue;
            case TipoDeDado.TYPE_REAL:
                return realValue;
            case TipoDeDado.TYPE_BOOLEAN:
                return boolValue;
            default:
                return charValue;
        }
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Item{" +
                "obj=" + this.getObj() +
//                ", intValue=" + intValue +
//                ", realValue=" + realValue +
//                ", charValue='" + charValue + '\'' +
//                ", boolValue=" + boolValue +
                ", type=" + type +
                '}';
    }

    static class TipoDeDado {
        public static final int TYPE_INT = 1;
        public static final int TYPE_REAL = 2;
        public static final int TYPE_STRING = 3;
        public static final int TYPE_BOOLEAN = 4;
    }
}
