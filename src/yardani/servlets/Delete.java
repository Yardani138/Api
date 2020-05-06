package yardani.servlets;

import com.google.gson.Gson;
import yardani.Config;
import yardani.controllers.NetworkController;
import yardani.templates.ErrorMessageEntity;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet("/delete")
public class Delete extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        Gson gson = new Gson();
        if(id != null) {
            if(deleteUser(id)) {
                resp.sendRedirect("/api");
                return;
            } else {
                ErrorMessageEntity errorMessage = new ErrorMessageEntity("User wasn't deleted.", 6);
                String jsonMessage = gson.toJson(errorMessage);
                resp.getWriter().write(jsonMessage);
            }
        } else {
            ErrorMessageEntity errorMessage = new ErrorMessageEntity("Id not specified.", 1);
            String jsonMessage = gson.toJson(errorMessage);
            resp.getWriter().write(jsonMessage);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private boolean deleteUser(String id) {
        NetworkController networkController = new NetworkController();
        Statement statement = null;
        ResultSet rs = null;
        boolean isDeleted = false;
        networkController.connect(Config.DB_URL, Config.DB_USER, Config.DB_PASSWORD);
        String sql = "DELETE FROM api_table WHERE id = " + id;
        try {
            statement = networkController.getConnection().createStatement();
            statement.executeUpdate(sql);
            System.out.println("User deleted!");
            isDeleted = true;
        } catch (SQLException e) {
            System.out.println("Can't delete user...\n" + e);
        } finally {
            networkController.disconnect(statement, rs);
        }
        return isDeleted;
    }
}
