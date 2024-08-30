package edu.escuelaing.arem.ASE.app.service;

import edu.escuelaing.arem.ASE.app.service.tools.RESTService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class StaticService {
    private static String WEB_ROOT;
    public static Map<String, RESTService> services = new HashMap();
    public static void staticfiles(String directory) {
        WEB_ROOT = "src/main/" + directory;

        Path path = Paths.get(WEB_ROOT);
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                System.out.println("Directorio creado: " + WEB_ROOT);
            }
        } catch (IOException e) {
            System.err.println("Error al crear el directorio: " + e.getMessage());
        }
    }
    public StaticService() {
        startServices();
    }

    public static void startServices(){
        get("",(req,res)->{
            System.out.println("Finding Resource...");
            String fileRequest = req.getResource();
            File file = new File(WEB_ROOT,fileRequest);
            int fileLength = (int) file.length();
            if(file.exists()){
                res.setContentType(getContentType(fileRequest));
                res.setFileData(readFileData(file,fileLength));
                res.setStatusText("OK");
                res.setCodeResponse("200");
                return String.valueOf(fileLength);
            }
            res.setStatusText("Not Found");
            res.setCodeResponse("404");
            return null;
        });
    }

    private static void get(String path, RESTService action) {
        services.put(path, action);
    }

    public static Map<String, RESTService> getServices() {
        return services;
    }

    private static String getContentType(String fileRequested) {
        if (fileRequested.endsWith(".html")) return "text/html";
        else if (fileRequested.endsWith(".css")) return "text/css";
        else if (fileRequested.endsWith(".js")) return "application/javascript";
        else if (fileRequested.endsWith(".png")) return "image/png";
        else if (fileRequested.endsWith(".jpg")) return "image/jpeg";
        return "text/plain";
    }

    private static byte[] readFileData(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];
        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } finally {
            if (fileIn != null) fileIn.close();
        }
        return fileData;
    }
}
