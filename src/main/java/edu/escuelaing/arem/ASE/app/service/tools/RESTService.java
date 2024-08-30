package edu.escuelaing.arem.ASE.app.service.tools;

import edu.escuelaing.arem.ASE.app.service.tools.Request;
import edu.escuelaing.arem.ASE.app.service.tools.Response;

import java.io.IOException;

public interface RESTService {
    String response(Request request, Response response) throws IOException;
}
