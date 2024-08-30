package edu.escuelaing.arem.ASE.app.service.tools;

public class Request {
    private String resource;

    public Request(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getValue(String key) {
        String[] parts = resource.split("\\?");
        if (parts.length > 1) {
            String[] params = parts[1].split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length > 1 && keyValue[0].equals(key)) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }
}
