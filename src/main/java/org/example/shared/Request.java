package org.example.shared;


import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type;
    private Object data;

    public Request(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Request{" +
                "type='" + type + '\'' +
                ", data=" + data +
                '}';
    }
}