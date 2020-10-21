package br.com.softdigital.fluig.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.softdigital.fluig.domains.Credential;
import br.com.softdigital.fluig.util.ConnectionFactory;

/**
 * Credentials Repository
 *
 * @author Pedro Costa
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class CredentialsRepository {

    private final static Logger LOG = Logger.getLogger(CredentialsRepository.class);

    public CredentialsRepository() {
        if (!ConnectionFactory.isTable("credentials")) {
            createDataBase();
        }
    }

    /**
     * Salvar ou atualizar credencial
     *
     * @param obj
     * @return
     * @throws SQLException
     */
    public Credential saveOrUpdate(Credential obj) throws SQLException {
        String queryInsert = "";
        queryInsert += " INSERT INTO credentials ";
        queryInsert += "   (url, user_code, user_name, password, company, company_name, environment) ";
        queryInsert += " VALUES ";
        queryInsert += "   (?, ?, ?, ?, ?, ?, ?)";

        String queryUpdate = "";
        queryUpdate += " UPDATE ";
        queryUpdate += "   credentials ";
        queryUpdate += " SET ";
        queryUpdate += "   url = ?,";
        queryUpdate += "   user_code = ?,";
        queryUpdate += "   user_name = ?, ";
        queryUpdate += "   password = ?, ";
        queryUpdate += "   company = ?, ";
        queryUpdate += "   company_name = ?, ";
        queryUpdate += "   environment = ? ";
        queryUpdate += " WHERE ";
        queryUpdate += "   id = ? ";

        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = null;

        if (obj.getId() != null) {
            stmt = connection.prepareStatement(queryUpdate);
            stmt.setInt(8, obj.getId());
        } else {
            stmt = connection.prepareStatement(queryInsert, Statement.RETURN_GENERATED_KEYS);
        }

        stmt.setString(1, obj.getUrl());
        stmt.setString(2, obj.getUserCode());
        stmt.setString(3, obj.getUserName());
        stmt.setString(4, obj.getPassword());
        stmt.setInt(5, obj.getCompany());
        stmt.setString(6, obj.getCompanyName());
        stmt.setBoolean(7, obj.isEnvironment());

        stmt.execute();

        ResultSet resultSet = stmt.getGeneratedKeys();

        stmt.close();
        connection.close();

        Integer id = null;

        if (resultSet.next()) {
            id = resultSet.getInt(1);
        }

        Credential credentials = null;

        if (id != null && id > 0) {
            credentials = findById(id);
        }

        return credentials;
    }

    /**
     * Buscar credencial por id
     *
     * @param id
     * @return
     * @throws SQLException
     */
    public Credential findById(Integer id) throws SQLException {
        String query = "";
        query += " SELECT ";
        query += "   * ";
        query += " FROM ";
        query += "   credentials ";
        query += " WHERE ";
        query += "   id = ? ";

        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(0, id);

        ResultSet resultSet = stmt.executeQuery();

        Credential obj = null;

        if (resultSet.next()) {
            obj = new Credential();
            obj.setId(resultSet.getInt("id"));
            obj.setCompany(resultSet.getLong("company"));
            obj.setCompanyName(resultSet.getString("company_name"));
            obj.setEnvironment(resultSet.getBoolean("environment"));
            obj.setPassword(resultSet.getString("password"));
            obj.setUrl(resultSet.getString("url"));
            obj.setUserCode(resultSet.getString("user_code"));
            obj.setUserName(resultSet.getString("user_name"));
        }

        stmt.close();
        connection.close();
        return obj;
    }

    public List<Credential> findAll() throws SQLException {
        String query = "";
        query += " SELECT ";
        query += "   * ";
        query += " FROM ";
        query += "   credentials ";

        Connection connection = ConnectionFactory.getConnection();
        PreparedStatement stmt = connection.prepareStatement(query);
        ResultSet resultSet = stmt.executeQuery();

        List<Credential> credentialsList = new ArrayList<Credential>();

        while (resultSet.next()) {
            Credential obj = new Credential(
                    resultSet.getString("url"),
                    resultSet.getString("user_code"),
                    resultSet.getString("user_name"),
                    resultSet.getString("password"),
                    resultSet.getLong("company"),
                    resultSet.getString("company_name"),
                    resultSet.getBoolean("environment")
            );
            obj.setId(resultSet.getInt("id"));

            credentialsList.add(obj);
        }

        stmt.close();
        connection.close();
        return credentialsList;
    }

    /**
     * Criar tabela no banco de dados
     *
     * @throws SQLException
     */
    private void createDataBase() {
        LOG.info("Criando tabela...");

        String query = "";
        query += " CREATE TABLE credentials ( ";
        query += "   id           INTEGER PRIMARY KEY AUTOINCREMENT, ";
        query += "   url          VARCHAR, ";
        query += "   user_code    VARCHAR, ";
        query += "   user_name    VARCHAR, ";
        query += "   password     VARCHAR, ";
        query += "   company      INTEGER, ";
        query += "   company_name VARCHAR, ";
        query += "   environment  BOOLEAN ";
        query += " );";

        try {
            Connection connection = ConnectionFactory.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.executeUpdate();
            stmt.close();

            if (!connection.isClosed()) {
                connection.close();
            }

            LOG.info("Tabela criada");
        } catch (SQLException e) {
            LOG.error("Falha ao criar tabela: " + e);
        }
    }

}
