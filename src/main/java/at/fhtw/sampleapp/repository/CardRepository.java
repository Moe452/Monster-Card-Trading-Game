package at.fhtw.sampleapp.repository;

import at.fhtw.sampleapp.dal.DataAccessException;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.model.Card;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class CardRepository {
    private static final String SELECT_FROM_STACK_SQL = "SELECT * FROM stack WHERE username=? AND card_id=?";
    private static final String SELECT_FROM_DECKS_SQL = "SELECT * FROM decks WHERE owner_id=?";
    private static final String SELECT_FROM_CARD_SQL = "SELECT * FROM cards WHERE id IN (SELECT card_id FROM stack WHERE username=?)";
    private static final String INSERT_CARD_SQL = "INSERT INTO customCard(name, type, id, damage, element_type, package_id) VALUES(?,?,?,?,?,?)";
    private static final String UPDATE_SET = "UPDATE decks SET \"first_card_id\"=?, \"second_card_id\"=?, \"third_card_id\"=?, \"fourth_card_id\"=? WHERE owner_id=?";

    public List<Card> retrieveCards(String username, UnitOfWork unitOfWork){
        try (PreparedStatement statement = unitOfWork.prepareStatement(SELECT_FROM_CARD_SQL)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return retrieveListOfCards(resultSet);
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Could not get cards for user: " + username, exception);
        }
    }

    public void placeCardsInDeck(String username, List<String> cardIDs, UnitOfWork unitOfWork){
        final int CARD_ID_PARAM_COUNT = 4;
        if (cardIDs.size() != CARD_ID_PARAM_COUNT) {
            throw new IllegalArgumentException("Invalid number of cardIDs. Expected: " + CARD_ID_PARAM_COUNT + " but got: " + cardIDs.size());
        }
        try (PreparedStatement statement = unitOfWork.prepareStatement(UPDATE_SET)) {
            for (int i = 0; i < CARD_ID_PARAM_COUNT; i++) {
                statement.setString(i + 1, cardIDs.get(i));
            }
            statement.setString(CARD_ID_PARAM_COUNT + 1, username);
            statement.execute();
        } catch (SQLException exception) {
            throw new DataAccessException("Error occurred when adding cards to the user's deck: " + username, exception);
        }
    }

    public boolean verifyCardOwnershipByUser(String username, List<String> cardIDs, UnitOfWork unitOfWork){
        try (PreparedStatement statement = unitOfWork.prepareStatement(SELECT_FROM_STACK_SQL)) {
            statement.setString(1, username);
            for (String cardID : cardIDs) {
                cardID = cardID.replaceAll("\"", "");
                statement.setString(2, cardID);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        return false;
                    }
                }
            }
            return true;
        } catch (SQLException exception) {
            throw new DataAccessException("Error occurred when checking if cards belong to the user: " + username, exception);
        }
    }

    public List<Card> displayDeckOfUser(String username, UnitOfWork unitOfWork){
        List<Card> deckCards = new ArrayList<>();
        try (PreparedStatement statement = unitOfWork.prepareStatement(SELECT_FROM_DECKS_SQL)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // selects all card IDs
                    String firstCardId = resultSet.getString("first_card_id");
                    String secondCardId = resultSet.getString("second_card_id");
                    String thirdCardId = resultSet.getString("third_card_id");
                    String fourthCardId = resultSet.getString("fourth_card_id");

                    //if deck is not configured
                    if (firstCardId == null || secondCardId == null || thirdCardId == null || fourthCardId == null) {
                        return deckCards;
                    }

                    // Build list of cards ids
                    List<String> cardIDs = Arrays.asList(firstCardId, secondCardId, thirdCardId, fourthCardId);

                    //Checking if cards belong to user
                    if (!verifyCardOwnershipByUser(username, cardIDs, unitOfWork)) {
                        return deckCards;
                    }

                    //Select all data from cards
                    String sql = "SELECT * FROM cards WHERE id IN ( " + String.join(",", Collections.nCopies(cardIDs.size(), "?")) + ")";
                    try (PreparedStatement statementCards = unitOfWork.prepareStatement(sql)) {
                        for (int i = 0; i < cardIDs.size(); i++) {
                            statementCards.setString(i + 1, cardIDs.get(i));
                        }
                        try (ResultSet resultSetCards = statementCards.executeQuery()) {
                            deckCards = retrieveListOfCards(resultSetCards);
                        }
                    }
                }
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Error occurred when retrieving cards from the user's deck: " + username, exception);
        }
            return deckCards;
    }



    private List<Card> retrieveListOfCards(ResultSet resultSet){
        List<Card> cards = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Card card = new Card();
                card.setName(resultSet.getString("name"));
                card.setType(resultSet.getString("type"));
                card.setId(resultSet.getString("id"));
                card.setElement(resultSet.getString("element_type"));
                card.setPackageID(resultSet.getString("package_id"));
                card.setDamage(resultSet.getInt("damage"));
                cards.add(card);
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Error occurred when retrieving cards from the results set", exception);
        }
        return cards;
    }

    public void createCustomCard(Card card, UnitOfWork unitOfWork) {
        try (PreparedStatement statement = unitOfWork.prepareStatement(INSERT_CARD_SQL)) {
            statement.setString(1, card.getName());
            statement.setString(2, card.getType());
            statement.setString(3, card.getId());
            statement.setInt(4, card.getDamage());
            statement.setString(5, card.getElement());
            statement.setString(6, card.getPackageID());
            statement.execute();
        } catch (SQLException exception) {
            throw new DataAccessException("Error occurred while creating card", exception);
        }
    }

}

