package edu.escuelaing.arem.ASE.app.service.tools;

import java.io.OutputStream;
import java.io.PrintWriter;

public class Response {
    private String contentType;
    private String codeResponse;
    private byte[] fileData;
    private String statusText;


    public Response() {
        this.codeResponse = "404";
        this.contentType = "text/plain";
        this.fileData = null;
        this.statusText = "NONE";
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public String getContentType() {
        return contentType;
    }


    public void setCodeResponse(String codeResponse) {
        this.codeResponse = codeResponse;
    }

    public String getCodeResponse() {
        return codeResponse;
    }
}
