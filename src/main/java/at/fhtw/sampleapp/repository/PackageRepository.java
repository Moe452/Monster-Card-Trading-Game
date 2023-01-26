package at.fhtw.sampleapp.repository;


import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.model.Card;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PackageRepository {
    private static final String INSERT_INTO_CARDS_SQL = "INSERT INTO cards(name, type, id, damage, element_type, package_id) VALUES(?,?,?,?,?,?)";
    private static final String SELECT_FROM_CARDS_SQL = "SELECT * FROM cards WHERE id=?";
    private static final String SELECT_FROM_CARDS_PACKAGE_SQL = "SELECT * FROM cards WHERE package_id=?";
    private static final String SELECT_PACKAGEID_SQL = "SELECT package_id FROM cards WHERE buyable=?";
    private static final String UPDATE_SET_SQL = "UPDATE cards SET buyable=? WHERE package_id=?";
    private static final String SELECT_ID_CARDS = "SELECT id FROM cards WHERE package_id=?";
    private static final String INSERT_STACK_SQL = "INSERT INTO stack(username, card_id) VALUES(?,?)";

    public boolean createNewPackage(Card[] cards, String packageID, UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement statement = unitOfWork.prepareStatement(INSERT_INTO_CARDS_SQL);
        for (Card card : cards) {
            if (verifyCardPresence(card.getId(), unitOfWork)) {
                return false;
            }
            statement.setString(1, card.getName());
            statement.setString(2, card.getType());
            statement.setString(3, card.getId());
            statement.setInt(4, card.getDamage());
            statement.setString(5, card.getElement());
            statement.setString(6, packageID);

            statement.addBatch();
        }
        statement.executeBatch();
        return true;

    }

    public boolean verifyCardPresence(String cardID, UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement statement = unitOfWork.prepareStatement(SELECT_FROM_CARDS_SQL);
        statement.setString(1, cardID);

        ResultSet resultSet = statement.executeQuery();
        return resultSet.next();
    }

    public boolean verifyPackagePresence(String packageID, UnitOfWork unitOfWork) throws SQLException {
        PreparedStatement statement = unitOfWork.prepareStatement(SELECT_FROM_CARDS_PACKAGE_SQL);
        statement.setString(1, packageID);

        ResultSet resultSet = statement.executeQuery();
        return resultSet.next();
    }

    public List<String> retrievePackage(UnitOfWork unitOfWork) throws SQLException {
        List<String> allPackages = new ArrayList<>();

        PreparedStatement statement = unitOfWork.prepareStatement(SELECT_PACKAGEID_SQL);
        statement.setBoolean(1, true);

        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            allPackages.add(resultSet.getString("package_id"));
        }

        return allPackages;
    }

    public void placePackageOnStack(String username, String buyPackageID, UnitOfWork unitOfWork) throws SQLException {
        // update package status
        PreparedStatement statementUpdate = unitOfWork.prepareStatement(UPDATE_SET_SQL);
        statementUpdate.setBoolean(1, false);
        statementUpdate.setString(2, buyPackageID);
        statementUpdate.executeUpdate();
        // select card ids from package
        PreparedStatement statementSelect = unitOfWork.prepareStatement(SELECT_ID_CARDS);
        statementSelect.setString(1, buyPackageID);
        ResultSet resultSet = statementSelect.executeQuery();

        // create list of card ids
        List<String> cardIDs = new ArrayList<>();
        while (resultSet.next()) {
            cardIDs.add(resultSet.getString("id"));
        }

        // insert card ids into stack
        PreparedStatement statement = unitOfWork.prepareStatement(INSERT_STACK_SQL);
        for (String cardID : cardIDs) {
            statement.setString(1, username);
            statement.setString(2, cardID);
            statement.addBatch();
        }
        statement.executeBatch();
    }
}
