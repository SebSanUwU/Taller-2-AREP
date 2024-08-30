package edu.escuelaing.arem.ASE.app.service;

import com.nimbusds.jose.shaded.gson.Gson;
import edu.escuelaing.arem.ASE.app.model.User;
import edu.escuelaing.arem.ASE.app.service.tools.RESTService;

import java.util.*;

public class UserService{
    public static final Map<String, User> userDatabase = new HashMap<>();
    public static Map<String, RESTService> services = new HashMap();

    public UserService() {
        createUsers();
        startServices();
    }
    void createUsers(){
        userDatabase.put("John", new User(UUID.randomUUID().hashCode(), "John", "john_wick@example.com"));
        userDatabase.put("Alice", new User(UUID.randomUUID().hashCode(), "Alice", "alice_smith@example.com"));
        userDatabase.put("Bob", new User(UUID.randomUUID().hashCode(), "Bob", "bob_marley@example.com"));
        userDatabase.put("Charlie", new User(UUID.randomUUID().hashCode(), "Charlie", "charlie_brown@example.com"));
        userDatabase.put("Diana", new User(UUID.randomUUID().hashCode(), "Diana", "diana_prince@example.com"));
        userDatabase.put("Eve", new User(UUID.randomUUID().hashCode(), "Eve", "eve_adams@example.com"));
    }
    public static void startServices(){
        get("/users",(req,res)-> {
            System.out.println("Finding all Users...");
            List<User> users = new ArrayList<>(userDatabase.values());
            // Convertir la lista de usuarios a JSON
            Gson gson = new Gson();
            res.setContentType("application/json");
            res.setCodeResponse("200");
            res.setStatusText("OK");
            return gson.toJson(users);
        });
        get("/user",(req,res)-> {
            String name = req.getValue("name");
            System.out.println("Finding User...");
            User user = userDatabase.get(name);
            if (user != null) {
                // Convert user object to JSON
                res.setContentType("application/json");
                res.setCodeResponse("200");
                res.setStatusText("OK");
                return "{\n" +
                        "  \"name\": \"" + user.getNombre() + "\",\n" +
                        "  \"id\": " + user.getId() + ",\n" +
                        "  \"email\": \"" + user.getEmail() + "\"\n" +
                        "}";
            }
            res.setStatusText("USER NOT FOUND");
            return null;
        });
    }

    private static void get(String path, RESTService action) {
        services.put(path, action);
    }

    public static Map<String, RESTService> getServices() {
        return services;
    }
}
