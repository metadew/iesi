package io.metadew.iesi.connection.elasticsearch;

public class NestedTestObject {

    private final TestObject a;
    private final String b;
    private final String c;
    private final String d;

    public NestedTestObject(TestObject a, String b, String c, String d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public String getD() {
        return d;
    }

    public String getC() {
        return c;
    }

    public TestObject getA() {
        return a;
    }

    public String getB() {
        return b;
    }
}
