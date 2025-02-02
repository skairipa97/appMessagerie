package User;

import org.json.JSONObject;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet("/convo")
public class Servlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Set CORS headers
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Validate required parameters
        String userIdParam = request.getParameter("userId");
        String conversationIdParam = request.getParameter("conversation");

        if (userIdParam == null || conversationIdParam == null) {
            sendError(response, "User ID or Conversation ID missing", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int currentUserId = Integer.parseInt(userIdParam);
            int conversationId = Integer.parseInt(conversationIdParam);

            // Since conversation ID is now the user ID, use it to get the correct conversation
            Conversation conversation = Database.getConversationByUserId(currentUserId);
            if (conversation == null) {
                sendError(response, "Conversation not found", HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Verify user access
            if (!Database.isUserInConversation(conversationId, currentUserId)) {
                sendError(response, "Accès non autorisé", HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // Update user connection status
            Database.updateUserLastConnection(currentUserId);

            // Get users
            User user1 = Database.getUserById(conversation.getUser1());
            User user2 = Database.getUserById(conversation.getUser2());

            // Get and mark messages as read
            List<Message> messages = Database.getMessagesByConversation(conversationId);
            Database.markMessagesAsRead(conversationId, currentUserId);

            // Build response
            JSONObject jsonResponse = buildConversationResponse(
                    conversation, messages, user1, user2, currentUserId
            );

            // Send response
            try (PrintWriter out = response.getWriter()) {
                out.print(jsonResponse.toString());
            }

        } catch (NumberFormatException e) {
            sendError(response, "ID invalide", HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            sendError(response, "Erreur interne du serveur", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private JSONObject buildConversationResponse(
            Conversation conversation,
            List<Message> messages,
            User user1,
            User user2,
            int currentUserId
    ) {
        JSONObject jsonResponse = new JSONObject();
        JSONObject conversationJson = new JSONObject();

        // Add messages
        JSONObject messagesJson = new JSONObject();
        for (Message msg : messages) {
            messagesJson.put(String.valueOf(msg.getId()), new JSONObject()
                    .put("fromuser", msg.getFromuser())
                    .put("touser", msg.getTouser())
                    .put("contenu", msg.getContenu())
                    .put("date_envoi", msg.getDate_envoi())
                    .put("lu", msg.getLu())
                    .put("is_sender", msg.getFromuser() == currentUserId)
            );
        }

        // Add users
        JSONObject usersJson = new JSONObject();
        if (user1 != null) {
            usersJson.put(String.valueOf(user1.getId()), createUserJson(user1));
        }
        if (user2 != null) {
            usersJson.put(String.valueOf(user2.getId()), createUserJson(user2));
        }

        // Build final structure
        conversationJson.put("messages", messagesJson);
        conversationJson.put("users", usersJson);
        jsonResponse.put("conversation_" + conversation.getId(), conversationJson);

        return jsonResponse;
    }

    private JSONObject createUserJson(User user) {
        return new JSONObject()
                .put("name", user.getUsername())
                .put("email", user.getEmail())
                .put("photo_profil", user.getPhotoProfil())
                .put("en_ligne", user.isEnLigne())
                .put("derniere_connexion", user.getDerniereConnexion());
    }

    private void sendError(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        JSONObject error = new JSONObject();
        error.put("error", message);
        response.getWriter().write(error.toString());
    }
}