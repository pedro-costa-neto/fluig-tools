package br.com.softdigital.fluig.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * Connection Factory
 *
 * @author Pedro Costa
 *
 * @version 1.0.0
 */
public class ConnectionFactory {

    private final static Logger LOG = Logger.getLogger(Connection.class);

    public static Connection getConnection() {
        Connection connection = null;
        try {
            // db parameters
            String directory = System.getProperty("user.dir");
            String url = "jdbc:sqlite:" + directory + "\\aplicacao.db";

            // create a connection to the database
            connection = DriverManager.getConnection(url);

            LOG.info("A conexão com o SQLite foi estabelecida.");

        } catch (SQLException e) {
            LOG.error("(DB Aplicacao) Falha ao iniciar conexao com o banco de dados: " + e);
        }

        return connection;
    }

    public static boolean isTable(String name) {
        boolean result = false;

        try {
            Connection connection = ConnectionFactory.getConnection();

            String query = "";
            query += " SELECT ";
            query += "   name ";
            query += " FROM ";
            query += "   sqlite_master ";
            query += " WHERE ";
            query += "   type = ? AND";
            query += "   name = ? ";

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, "table");
            stmt.setString(2, name);

            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                result = true;
            }

            stmt.close();
            connection.close();

        } catch (SQLException e) {
            LOG.error("(DB Aplicacao) Falha consultar tabela: " + e);
        }
        return result;
    }
}
