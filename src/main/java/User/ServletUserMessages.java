package User;

import org.json.JSONArray;
import org.json.JSONObject;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/user-messages")
public class ServletUserMessages extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String userIdParam = request.getParameter("id");
        String conversationIdParam = request.getParameter("conversation");

        if (userIdParam == null || conversationIdParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"ID utilisateur ou conversation manquant\"}");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdParam);
            int conversationId = Integer.parseInt(conversationIdParam);

            // Récupérer l'utilisateur
            User user = Database.getUserById(userId);
            if (user == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Utilisateur non trouvé\"}");
                return;
            }

            // Récupérer les messages de la conversation
            List<Message> messages = Database.getMessagesByConversation(conversationId);

            // Construire la réponse JSON
            JSONObject jsonResponse = new JSONObject();

            // Ajouter l'utilisateur
            JSONObject jsonUser = new JSONObject();
            jsonUser.put("id", user.getId());
            jsonUser.put("name", user.getUsername());
            jsonUser.put("email", user.getEmail());
            jsonUser.put("photo_profil", user.getPhotoProfil());
            jsonUser.put("en_ligne", user.isEnLigne());
            jsonUser.put("derniere_connexion", user.getDerniereConnexion());

            jsonResponse.put("user", jsonUser);

            // Ajouter les messages
            JSONArray jsonMessages = new JSONArray();
            for (Message message : messages) {
                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("id", message.getId());
                jsonMessage.put("fromuser", message.getFromuser());
                jsonMessage.put("touser", message.getTouser());
                jsonMessage.put("contenu", message.getContenu());
                jsonMessage.put("date_envoi", message.getDate_envoi().toString());
                jsonMessage.put("lu", message.getLu());

                jsonMessages.put(jsonMessage);
            }

            jsonResponse.put("messages", jsonMessages);

            // Envoyer la réponse
            try (PrintWriter out = response.getWriter()) {
                out.print(jsonResponse.toString());
                out.flush();
            }

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"ID utilisateur ou conversation invalide\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Erreur interne du serveur\"}");
        }
    }
}
