package edu.escuelaing.arem.ASE.app;


import edu.escuelaing.arem.ASE.app.service.StaticService;
import edu.escuelaing.arem.ASE.app.service.tools.RESTService;
import edu.escuelaing.arem.ASE.app.service.tools.Request;
import edu.escuelaing.arem.ASE.app.service.tools.Response;
import edu.escuelaing.arem.ASE.app.service.UserService;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.*;



public class SimpleWebServer {
    private static final int PORT = 8080; //corre por este puerto
    public static UserService userService = new UserService();
    public static StaticService staticService = new StaticService();
    public static Map<String, RESTService> userServiceTable = UserService.getServices();
    public static Map<String, RESTService> staticServiceTable = StaticService.getServices();

    public static void main(String[] args) throws IOException {
        //PATH DEV
        StaticService.staticfiles("webroot");

        ExecutorService threadPool = Executors.newFixedThreadPool(10); // Crea un "Pool de hilos" de hilos a procesor
        ServerSocket serverSocket = new ServerSocket(PORT); // crea un SererSocket con el puerto
        while (true) {
            Socket clientSocket = serverSocket.accept(); // Espera el llamado de alguna peticion cliente
            threadPool.submit(new ClientHandler(clientSocket)); // AL cliente le asigna el socketCLiente y lo pone a correr
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); // Saca caracteres
             BufferedOutputStream dataOut = new BufferedOutputStream(clientSocket.getOutputStream())) {

            String requestLine = in.readLine(); // Lee la primera línea de la solicitud.
            if (requestLine == null) return;

            String[] tokens = requestLine.split(" ");
            if (tokens.length < 3) return;

            String method = tokens[0];
            String requestedResource = tokens[1]; // Obtiene el recurso solicitado.
            String basePath = requestedResource.split("\\?")[0]; // Extrae el camino base.

            printRequestHeader(requestLine,in);

            System.out.println(basePath);

            Request req = new Request(requestedResource);
            Response res = new Response();

            if (method.equals("GET")) {
                if (basePath.startsWith("/api")){
                    basePath = basePath.replaceAll("/api","");
                    handleAPIGet(basePath,req,res,out,dataOut);
                }else {
                    if (requestedResource.endsWith("/")) {
                        req.setResource("index.html");
                        requestedResource+="index.html";
                    }
                    System.out.println(requestedResource);
                    handleGetRequest(requestedResource,req,res,out,dataOut);
                }
            }  else {
                res.setCodeResponse("501");
                res.setStatusText("Not Implemented");
                sendErrorResponse(out, res);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printRequestHeader(String requestLine, BufferedReader in) throws IOException {
        System.out.println("Request Line: " + requestLine);
        String inputLine = "";
        while ((inputLine = in.readLine()) != null) {
            if( !in.ready()) {
                break;
            }
            System.out.println("Header: " + inputLine);
        }
    }

    private void handleGetRequest(String fileRequested, Request req, Response res, PrintWriter out, BufferedOutputStream dataOut) throws IOException {
        String fileLength = SimpleWebServer.staticServiceTable.get("").response(req,res);
        if (fileLength != null) {
            sendOkResponse(out,res, Integer.parseInt(fileLength));
            byte[] fileData = res.getFileData();
            dataOut.write(fileData, 0, Integer.parseInt(fileLength));
            dataOut.flush();
        } else {
            sendErrorResponse(out,res);
        }
    }

    private void handleAPIGet(String fileRequest, Request req, Response res, PrintWriter out, BufferedOutputStream dataOut) throws IOException {
        String response = SimpleWebServer.userServiceTable.get(fileRequest).response(req,res);
        if (response != null){
            sendOkResponse(out,res,response.length());
            dataOut.write(response.getBytes());
            dataOut.flush();
        }else {
            sendErrorResponse(out,res);
        }
    }

    private void sendOkResponse(PrintWriter out,Response response,int fileLength){
        out.println("HTTP/1.1 "+response.getCodeResponse()+" "+response.getStatusText());
        out.println("Content-type: " + response.getContentType());
        out.println("Content-length: " + fileLength);
        out.println();
        out.flush();
    }

    private void sendErrorResponse(PrintWriter out,Response response){
        out.println("HTTP/1.1 "+response.getCodeResponse()+" "+response.getStatusText());
        out.println("Content-type: " + response.getContentType());
        out.println();
        out.flush();
        out.println("<html><body><h1>"+response.getCodeResponse()+" "+response.getStatusText()+"</h1></body></html>");
        out.flush();
    }
}