package at.fhtw.sampleapp.repository;


import at.fhtw.sampleapp.dal.DataAccessException;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.model.Stats;
import at.fhtw.sampleapp.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserRepository {
    private static final String INSERT_USER_SQL = "INSERT INTO users(username, password, coins, elo, wins, losses, mtcg_token) VALUES(?,?,?,?,?,?,?)";
    private static final String INSERT_DECK_SQL = "INSERT INTO decks(owner_id) VALUES(?)";
    private static final String SELECT_USER_SQL = "SELECT name, bio, image FROM users WHERE username=?";
    private static final String SELECT_PASSWORD_AND_TOKEN_SQL = "SELECT password, mtcg_token FROM users WHERE username=?";
    private static final String SELECT_USER_DATA_SQL = "SELECT username, coins, elo, wins, losses, name, bio, image FROM users WHERE username=?";
    private static final String UPDATE_USER_DATA_SQL = "UPDATE users SET bio=?, image=?, name=? WHERE username=?";
    private static final String SELECT_USER_COINS_SQL = "SELECT coins FROM users WHERE username=?";
    private static final String UPDATE_USER_COINS_SQL = "UPDATE users SET coins=? WHERE username=?";
    private static final String SELECT_DATA_FROM_USERS = "SELECT name, elo, wins, losses FROM users WHERE username=?";

    public void createNewUser(User user, UnitOfWork unitOfWork) {
        try (PreparedStatement statement = unitOfWork.prepareStatement(INSERT_USER_SQL)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setInt(3, 20);
            statement.setInt(4, 100);
            statement.setInt(5, 0);
            statement.setInt(6, 0);
            statement.setString(7, user.getUsername() + "-mtcgToken");

            statement.execute();

            try (PreparedStatement statementDeck = unitOfWork.prepareStatement(INSERT_DECK_SQL)) {
                statementDeck.setString(1, user.getUsername());
                statementDeck.execute();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Error occurred while creating user", exception);
        }
    }

    public boolean verifyUserExistence(String username, UnitOfWork unitOfWork) {
        try (PreparedStatement statement = unitOfWork.prepareStatement(SELECT_USER_SQL)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Error occurred while checking if the user exists", exception);
        }
    }

    public String verifyLoginInformation(User user, UnitOfWork unitOfWork) {
        try (PreparedStatement statement = unitOfWork.prepareStatement(SELECT_PASSWORD_AND_TOKEN_SQL)) {
            statement.setString(1, user.getUsername());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String password = resultSet.getString("password");
                    if (password.equals(user.getPassword())) {
                        return resultSet.getString("mtcg_token");
                    }
                }
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Error occured while checking login credentials", exception);
        }

        return "None";
    }


    public User retrieveUserInformation(String username, UnitOfWork unitOfWork) {
        try (PreparedStatement statement = unitOfWork.prepareStatement(SELECT_USER_DATA_SQL)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setUsername(resultSet.getString("username"));
                    user.setCoins(resultSet.getInt("coins"));
                    user.setElo(resultSet.getInt("elo"));
                    user.setWins(resultSet.getInt("wins"));
                    user.setLosses(resultSet.getInt("losses"));
                    user.setName(resultSet.getString("name"));
                    user.setBio(resultSet.getString("bio"));
                    user.setImage(resultSet.getString("image"));
                    return user;
                } else {
                    return new User();
                }
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Error occured while retrieving user's data", exception);
        }
    }


    public void updateUserInformation(String username, User user, UnitOfWork unitOfWork) {
        try (PreparedStatement statement = unitOfWork.prepareStatement(UPDATE_USER_DATA_SQL)) {
            statement.setString(1, user.getBio());
            statement.setString(2, user.getImage());
            statement.setString(3, user.getName());
            statement.setString(4, username);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException("Error occured while updating user's information", exception);
        }
    }

    public boolean retrieveUserCoins(String username, UnitOfWork unitOfWork) {
        try (PreparedStatement statement = unitOfWork.prepareStatement(SELECT_USER_COINS_SQL)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("coins") >= 5;
                }
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Error occured while checking user's money", exception);
        }

        return false;
    }

    public void updateUserCoins(String username, UnitOfWork unitOfWork) {
        try (PreparedStatement selectStatement = unitOfWork.prepareStatement(SELECT_USER_COINS_SQL)) {
            selectStatement.setString(1, username);

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    int coins = resultSet.getInt("coins");

                    try (PreparedStatement updateStatement = unitOfWork.prepareStatement(UPDATE_USER_COINS_SQL)) {
                        updateStatement.setInt(1, coins - 5);
                        updateStatement.setString(2, username);
                        updateStatement.executeUpdate();
                    }
                } else {
                    throw new DataAccessException("Error occurred while looking for the user's coins: " + username);
                }
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Error occurred when trying to subtract coins from user's account", exception);
        }
    }

    public Stats retrieveUserStats(String username, UnitOfWork unitOfWork) throws Exception {
        try {
            PreparedStatement statement = unitOfWork.prepareStatement(SELECT_DATA_FROM_USERS);
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return new Stats();
            }

            String name = resultSet.getString("name");
            int elo = resultSet.getInt("elo");
            int wins = resultSet.getInt("wins");
            int losses = resultSet.getInt("losses");

            Stats statsOfUser = new Stats();
            statsOfUser.setName(name);
            statsOfUser.setElo(elo);
            statsOfUser.setWins(wins);
            statsOfUser.setLosses(losses);

            return statsOfUser;
        } catch (SQLException exception) {
            throw new Exception("Error occurred while trying to retrieve user's data again.", exception);
        }
    }


}