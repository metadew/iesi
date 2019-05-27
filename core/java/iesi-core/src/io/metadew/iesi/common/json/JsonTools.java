package io.metadew.iesi.common.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

public class JsonTools {

    private static final String formatError = "Input does not contain a valid JSON structure.";

    private boolean consoleOutput = false;

    private static final String delimiter = "|";

    private static String pathSeparator = ".";
    private static int genericId = 0;
    private static int messageId = 0;

    private static LinkedList<Integer> parentIds = new LinkedList<Integer>();
    private static LinkedList<String> parentPaths = new LinkedList<String>();
    private static LinkedList<Integer> inGroupIds = new LinkedList<Integer>();
    private static LinkedList<JsonToken> jsonTokens = new LinkedList<JsonToken>();

    private JsonParsed jsonParsed;
    private JsonParsedItem jsonParsedItem;

    public JsonTools() {
    }

    public JsonParsed parseJson(String context, String scope) {
        JsonParsed jsonParsed = null;
        try {
            JsonFactory f = new MappingJsonFactory();
            JsonParser jp = null;

            if (context.equalsIgnoreCase("file")) {
                File inputFile = new File(scope);
                jp = f.createParser(inputFile);
            } else if (context.equalsIgnoreCase("string")) {
                jp = f.createParser(scope);
            }

            jsonParsed = this.parseJson(jp);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonParsed;

    }

    private JsonParsed parseJson(JsonParser jp) {
        this.setJsonParsed(new JsonParsed());
        try {
            JsonToken current;
            current = jp.nextToken();
            parentIds.add(0);
            parentPaths.add("");
            inGroupIds.add(0);

            // Start process
            if (current == JsonToken.START_ARRAY) {
                processJSONarray(jp);
            } else {
                this.processJSONobject(jp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.getJsonParsed();

    }

    /**
     * Function to process a JSON Array.
     */
    private void processJSONarray(JsonParser jp) {
        try {
            JsonToken current = jp.nextToken();
            while (current != JsonToken.END_ARRAY) {

                if (current == JsonToken.START_OBJECT) {
                    jsonTokens.add(current);
                    int lastInGroupId = inGroupIds.getLast();
                    lastInGroupId++;
                    inGroupIds.removeLast();
                    inGroupIds.add(lastInGroupId);
                    processJSONobject(jp);
                    genericId++;
                    jsonTokens.removeLast();
                } else {
                    throw new RuntimeException(formatError);
                }
                current = jp.nextToken();
            }
            inGroupIds.removeLast();
            if (!jsonTokens.isEmpty()) {
                jsonTokens.removeLast();
            }

        } catch (IOException e) {
            throw new RuntimeException(formatError, e);
        }
    }

    /**
     * Function to process a JSON Object.
     */
    private void processJSONobject(JsonParser jp1) {

        try {
            JsonNode node = jp1.readValueAsTree();
            org.json.JSONObject o = new org.json.JSONObject(node.toString().replace("@", ""));
            Iterator<String> keys = o.keys();
            while (keys.hasNext()) {

                String key = (String) keys.next();

                if (this.getJsonParsedItem() == null)
                    this.setJsonParsedItem(new JsonParsedItem());
                this.getJsonParsedItem().setItem(genericId);
                this.write(genericId);
                this.write(delimiter);
                this.write(++messageId);
                this.getJsonParsedItem().setIdentifier(messageId);
                this.write(delimiter);
                JsonFactory f = new MappingJsonFactory();
                JsonParser jp2 = f.createParser(node.get(key).toString());
                JsonParser jp3 = f.createParser(node.get(key).toString());

                JsonToken current2 = jp2.nextToken();
                jp3.nextToken();
                JsonToken current3 = jp3.nextToken();
                if (parentPaths.getLast().equalsIgnoreCase("")) {
                    parentPaths.add(key);
                } else {
                    parentPaths.add(pathSeparator + key);
                }
                if (current2 == JsonToken.START_ARRAY && current3 == JsonToken.START_OBJECT) {

                    int lastGroupId = inGroupIds.getLast();

                    this.getJsonParsedItem().setParent(parentIds.getLast());
                    this.write(parentIds.getLast());
                    this.write(delimiter);
                    if (jsonTokens.contains(JsonToken.START_ARRAY)) {
                        this.getJsonParsedItem().setIteration(lastGroupId);
                        this.write(lastGroupId);
                    } else {
                        this.getJsonParsedItem().setIteration(1);
                        this.write(1);
                    }
                    // lastGroupId++;
                    inGroupIds.removeLast();
                    inGroupIds.add(lastGroupId);
                    inGroupIds.add(0);
                    jsonTokens.add(current2);
                    this.write(delimiter);
                    this.getJsonParsedItem().setKey(key);
                    this.write(key);
                    this.write(delimiter);
                    this.getJsonParsedItem().setValue("");
                    this.write("");
                    this.write(delimiter);

                    String parentPath = "";
                    for (String string : parentPaths) {
                        this.write(string);
                        parentPath += string;

                    }
                    this.getJsonParsedItem().setPath(parentPath);
                    this.getJsonParsed().addItem(this.getJsonParsedItem());
                    this.setJsonParsedItem(null);
                    this.writeln("");

                    parentIds.add(messageId);
                    processJSONarray(jp2);
                    parentPaths.removeLast();
                    parentIds.removeLast();

                } else if (current2 == JsonToken.START_OBJECT && current3 == JsonToken.FIELD_NAME) {
                    this.processJsonObjectWithField(node, key);
                } else {
                    String value = "";
                    if (node.get(key).isArray()) {
                        value = StringUtils.join(node.get(key), ',').replaceAll("\"", "");
                    } else {
                        value = node.get(key).asText();
                    }

                    if (parentPaths.size() > 1) {
                        this.getJsonParsedItem().setParent(parentIds.getLast());
                        this.write(parentIds.getLast());
                        this.write(delimiter);
                        if (inGroupIds.size() == 1) {
                            this.getJsonParsedItem().setIteration(1);
                            this.write(1);
                        } else {
                            this.getJsonParsedItem().setIteration(inGroupIds.getLast());
                            this.write(inGroupIds.getLast());
                        }
                        this.write(delimiter);
                        this.getJsonParsedItem().setKey(key);
                        this.write(key);
                        this.write(delimiter);
                        this.getJsonParsedItem().setValue(value);
                        this.write(value);
                        this.write(delimiter);
                        String parentPath = "";
                        for (String string : parentPaths) {
                            this.write(string);
                            parentPath += string;
                        }
                        this.getJsonParsedItem().setPath(parentPath);
                        parentPaths.removeLast();
                        this.writeln("");
                        this.getJsonParsed().addItem(this.getJsonParsedItem());
                        this.setJsonParsedItem(null);

                    } else {
                        this.getJsonParsedItem().setParent(parentIds.getLast());
                        this.write(parentIds.getLast());
                        this.write(delimiter);
                        this.getJsonParsedItem().setIteration(1);
                        this.write(1);
                        this.write(delimiter);
                        this.getJsonParsedItem().setKey(key);
                        this.write(key);
                        this.write(delimiter);

                        this.getJsonParsedItem().setValue(value);
                        this.write(value);
                        this.write(delimiter);
                        String parentPath = "";
                        for (String string : parentPaths) {
                            this.write(string);
                            parentPath += string;

                        }
                        this.getJsonParsedItem().setPath(parentPath);
                        parentPaths.removeLast();
                        this.writeln("");
                        this.getJsonParsed().addItem(this.getJsonParsedItem());
                        this.setJsonParsedItem(null);

                    }
                }

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

    }

    private void processJsonObjectWithField(JsonNode node, String key) {
        // continuing with the previous node key
        String value = "";
        if (node.get(key).isArray()) {
            value = StringUtils.join(node.get(key), ',').replaceAll("\"", "");
        } else {
            value = node.get(key).asText();
        }
        this.getJsonParsedItem().setParent(parentIds.getLast());
        this.write(parentIds.getLast());
        this.write(delimiter);
        if (inGroupIds.size() == 1) {
            this.getJsonParsedItem().setIteration(1);
            this.write(1);
        } else {
            this.getJsonParsedItem().setIteration(inGroupIds.getLast());
            this.write(inGroupIds.getLast());
        }
        this.write(delimiter);
        this.getJsonParsedItem().setKey(key);
        this.write(key);
        this.write(delimiter);
        this.getJsonParsedItem().setValue(value);
        this.write(value);
        this.write(delimiter);
        String parentPath = "";
        for (String string : parentPaths) {
            this.write(string);
            parentPath += string;
        }
        this.getJsonParsedItem().setPath(parentPath);
        parentPaths.removeLast();
        this.getJsonParsed().addItem(this.getJsonParsedItem());
        this.setJsonParsedItem(null);

        this.writeln("");
        ///// implementation ends

        Iterator<Entry<String, JsonNode>> internalElements = node.get(key).fields();
        int lastMessageId = messageId;
        parentIds.add(lastMessageId);
        int lastGroupId = inGroupIds.getLast();
        inGroupIds.removeLast();
        inGroupIds.add(lastGroupId);
        // all the elements will have same group id below
        inGroupIds.add(0);
        while (internalElements.hasNext()) {
            if (parentPaths.getLast().equalsIgnoreCase("")) {
                parentPaths.add(key);
            } else {
                parentPaths.add(pathSeparator + key);
            }
            Entry<String, JsonNode> element = internalElements.next();

            if (this.getJsonParsedItem() == null)
                this.setJsonParsedItem(new JsonParsedItem());
            this.getJsonParsedItem().setItem(genericId);
            this.write(genericId);
            this.write(delimiter);
            this.write(++messageId);
            this.getJsonParsedItem().setIdentifier(messageId);
            this.write(delimiter);
            this.write(parentIds.getLast());
            this.getJsonParsedItem().setParent(parentIds.getLast());
            this.write(delimiter);

            int lastGroupIdNew = inGroupIds.getLast();
            this.getJsonParsedItem().setIteration(inGroupIds.getLast());
            this.write(lastGroupIdNew);
            inGroupIds.removeLast();
            // not incrementing the elements as all the element will have same level
            inGroupIds.add(lastGroupIdNew);
            this.write(delimiter);
            this.write(element.getKey());
            this.getJsonParsedItem().setKey(element.getKey());
            this.write(delimiter);
            if (element.getValue().isArray()) {
                value = "";
            } else {
                value = element.getValue().asText();
            }
            this.getJsonParsedItem().setValue(value);
            this.write(value);
            this.write(delimiter);
            if (parentPaths.getLast().equalsIgnoreCase("")) {
                parentPaths.add(element.getKey());
            } else {
                parentPaths.add(pathSeparator + element.getKey());
            }
            parentPath = "";
            for (String string : parentPaths) {
                this.write(string);
                parentPath += string;

            }
            this.getJsonParsedItem().setPath(parentPath);
            this.writeln("");
            this.getJsonParsed().addItem(this.getJsonParsedItem());
            this.setJsonParsedItem(null);

            parentPaths.removeLast();

            JsonFactory f = new MappingJsonFactory();
            JsonParser jp2;
            try {
                jp2 = f.createParser(element.getValue().toString());

                JsonParser jp3 = f.createParser(element.getValue().toString());
                JsonToken current2 = jp2.nextToken();
                jp3.nextToken();
                JsonToken current3 = jp3.nextToken();
                if (current2 == JsonToken.START_ARRAY && current3 == JsonToken.START_OBJECT) {
                    int lastGroupIdInner = inGroupIds.getLast();
                    inGroupIds.removeLast();
                    inGroupIds.add(lastGroupIdInner);
                    int ingroupId = 0;
                    jsonTokens.add(current2);
                    inGroupIds.add(ingroupId);
                    parentIds.add(messageId);
                    if (parentPaths.getLast().equalsIgnoreCase("")) {
                        parentPaths.add(element.getKey());
                    } else {
                        parentPaths.add(pathSeparator + element.getKey());
                    }
                    this.processJSONarray(jp2);
                    parentPaths.removeLast();
                    parentIds.removeLast();
                } else if (current2 == JsonToken.START_OBJECT && current3 == JsonToken.FIELD_NAME) {
                    int lastGroupIdInner = inGroupIds.getLast();
                    inGroupIds.removeLast();
                    inGroupIds.add(lastGroupIdInner);
                    int ingroupId = 0;
                    jsonTokens.add(current2);
                    inGroupIds.add(ingroupId);
                    parentIds.add(messageId);
                    if (parentPaths.getLast().equalsIgnoreCase("")) {
                        parentPaths.add(element.getKey());
                    } else {
                        parentPaths.add(pathSeparator + element.getKey());
                    }
                    this.processJSONobject(jp2);
                    parentPaths.removeLast();
                    parentIds.removeLast();
                }
            } catch (JsonParseException e) {
                throw new RuntimeException(formatError, e);
            } catch (IOException e) {
                throw new RuntimeException(formatError, e);
            }
            parentPaths.removeLast();

        }
        inGroupIds.removeLast();
        parentIds.removeLast();
    }

    private void write(String input) {
        if (this.isConsoleOutput())
            System.out.print(input);
    }

    private void write(int input) {
        if (this.isConsoleOutput())
            System.out.print(input);
    }

    private void writeln(String input) {
        if (this.isConsoleOutput())
            System.out.println(input);
    }

    // Getters and Setters
    public JsonParsed getJsonParsed() {
        return jsonParsed;
    }

    public void setJsonParsed(JsonParsed jsonParsed) {
        this.jsonParsed = jsonParsed;
    }

    public JsonParsedItem getJsonParsedItem() {
        return jsonParsedItem;
    }

    public void setJsonParsedItem(JsonParsedItem jsonParsedItem) {
        this.jsonParsedItem = jsonParsedItem;
    }

    public boolean isConsoleOutput() {
        return consoleOutput;
    }

    public void setConsoleOutput(boolean consoleOutput) {
        this.consoleOutput = consoleOutput;
    }
}