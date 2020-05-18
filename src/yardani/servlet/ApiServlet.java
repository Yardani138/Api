package yardani.servlet;

import com.google.gson.Gson;
import yardani.config.Config;
import yardani.controller.NetworkController;
import yardani.domain.ErrorMessage;
import yardani.domain.Message;
import yardani.security.Crypto;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet("/api")
public class ApiServlet extends HttpServlet {

    private String id;
    private String firstName;
    private String lastName;
    private String country;
    private String city;
    private String street;
    private String houseNum;
    private String email;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        Crypto crypto = new Crypto();
        String id = req.getParameter("id");
        if(id != null) {
            if(getInfo(id)) {
                Message address = new Message(new String(crypto.decrypt(street.getBytes())), new String(crypto.decrypt(houseNum.getBytes())), new String(crypto.decrypt(city.getBytes())));
                Message message = new Message(this.id, new String(crypto.decrypt(firstName.getBytes())), new String(crypto.decrypt(lastName.getBytes())), new String(crypto.decrypt(country.getBytes())), new String(crypto.decrypt(email.getBytes())), address);
                String jsonMessage = gson.toJson(message);
                resp.getWriter().write(jsonMessage);
            } else {
                ErrorMessage errorMessage = new ErrorMessage("User doesn't exist.", 4);
                String jsonMessage = gson.toJson(errorMessage);
                resp.getWriter().write(jsonMessage);
            }
        }
    }

    private boolean getInfo(String id) {
        NetworkController networkController = new NetworkController();
        Statement statement = null;
        ResultSet rs = null;
        boolean isGotten = false;
        networkController.connect(Config.DB_URL, Config.DB_USER, Config.DB_PASSWORD);
        String query = "SELECT * FROM api_table WHERE id = " + id;
        try {
            statement = networkController.getConnection().createStatement();
            rs = statement.executeQuery(query);
            while(rs.next()){
                this.id = rs.getString("id");
                if(this.id != null) {
                    firstName = rs.getString("firstname");
                    lastName = rs.getString("lastname");
                    country = rs.getString("country");
                    city = rs.getString("city");
                    street = rs.getString("street");
                    houseNum = rs.getString("housenum");
                    email = rs.getString("email");
                    isGotten = true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Can't get info\n" + e);
        } finally {
            networkController.disconnect(statement, rs);
        }
        return isGotten;
    }
}
