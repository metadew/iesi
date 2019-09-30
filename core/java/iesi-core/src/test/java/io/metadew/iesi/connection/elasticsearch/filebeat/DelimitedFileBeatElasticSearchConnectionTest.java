package io.metadew.iesi.connection.elasticsearch.filebeat;

import io.metadew.iesi.connection.elasticsearch.NestedTestObject;
import io.metadew.iesi.connection.elasticsearch.TestObject;
import io.metadew.iesi.connection.elasticsearch.filebeat.DelimitedFileBeatElasticSearchConnection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DelimitedFileBeatElasticSearchConnectionTest {

    @Test
    void toStringString() {
        DelimitedFileBeatElasticSearchConnection delimitedFileBeatElasticSearchConnection = new DelimitedFileBeatElasticSearchConnection();
        assertEquals("\"a\"", delimitedFileBeatElasticSearchConnection.toString("a"));
    }

    @Test
    void toStringObject() {
        DelimitedFileBeatElasticSearchConnection delimitedFileBeatElasticSearchConnection = new DelimitedFileBeatElasticSearchConnection(" ");
        TestObject testObject = new TestObject("a", "b", "c", "d");
        assertEquals("\"a\" \"b\" \"c\" \"d\"", delimitedFileBeatElasticSearchConnection.toString(testObject));
    }

    @Test
    void toStringObjectCustomQuote() {
        DelimitedFileBeatElasticSearchConnection delimitedFileBeatElasticSearchConnection = new DelimitedFileBeatElasticSearchConnection(" ", "'");
        TestObject testObject = new TestObject("a", "b", "c", "d");
        assertEquals("'a' 'b' 'c' 'd'", delimitedFileBeatElasticSearchConnection.toString(testObject));
    }

    @Test
    void toStringObjectCustomDelimiter() {
        DelimitedFileBeatElasticSearchConnection delimitedFileBeatElasticSearchConnection = new DelimitedFileBeatElasticSearchConnection(";");
        TestObject testObject = new TestObject("a", "b", "c", "d");
        assertEquals("\"a\";\"b\";\"c\";\"d\"", delimitedFileBeatElasticSearchConnection.toString(testObject));
    }

    @Test
    void toStringNestedObject() {
        DelimitedFileBeatElasticSearchConnection delimitedFileBeatElasticSearchConnection = new DelimitedFileBeatElasticSearchConnection(" ", "'");
        NestedTestObject nestedTestObject = new NestedTestObject(new TestObject("a", "b", "c", "d"), "a", "b", "c");
        assertEquals("'a' 'b' 'c' 'd' 'a' 'b' 'c'", delimitedFileBeatElasticSearchConnection.toString(nestedTestObject));
    }
    
}
