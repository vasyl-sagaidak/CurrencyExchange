package servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public final class ServletUtil {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private ServletUtil() {}

    public static void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Map<String, String> errorResponse = Collections.singletonMap("message", message);

        gson.toJson(errorResponse, resp.getWriter());
    }
}
