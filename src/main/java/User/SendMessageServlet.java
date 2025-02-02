package User;

import org.json.JSONObject;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.stream.Collectors;

@WebServlet("/sendMessage")
public class SendMessageServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Read and log the incoming JSON
            String jsonString = request.getReader().lines().collect(Collectors.joining());
            System.out.println("Received message payload: " + jsonString);

            JSONObject jsonObj = new JSONObject(jsonString);

            // Extract and validate values
            int fromUser = jsonObj.getInt("fromuser");
            int toUser = jsonObj.getInt("touser");
            String content = jsonObj.getString("contenu");
            int conversation = jsonObj.getInt("conversation");

            // Validate users exist
            if (Database.getUserById(fromUser) == null || Database.getUserById(toUser) == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Invalid user IDs\"}");
                return;
            }

            // Create and save message
            Message message = new Message();
            message.setFromuser(fromUser);
            message.setTouser(toUser);
            message.setContenu(content);
            message.setDate_envoi(new Timestamp(System.currentTimeMillis()));
            message.setLu(false);
            message.setConversation(conversation);

            Message savedMessage = Database.saveMessage(message);
            System.out.println("Attempting to save message: " + message.toString());

            if (savedMessage != null) {
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("success", true);
                jsonResponse.put("messageId", savedMessage.getId());
                response.getWriter().write(jsonResponse.toString());
            } else {
                throw new ServletException("Failed to save message");
            }

        } catch (Exception e) {
            System.err.println("Error in SendMessage servlet: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}