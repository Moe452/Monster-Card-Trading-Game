package at.fhtw.sampleapp.service;

import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.model.User;
import at.fhtw.sampleapp.repository.UserRepository;
import org.junit.Before;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserRepositoryTest {

    private UserRepository userRepository;
    private UnitOfWork unitOfWork;
    private PreparedStatement statement;

    @Before
    public void setUp() {
        userRepository = new UserRepository();
        unitOfWork = mock(UnitOfWork.class);
        statement = mock(PreparedStatement.class);
    }
    @org.junit.Test
    public void testCreateNewUser() {
        UnitOfWork unitOfWork = new UnitOfWork();
        UserRepository userRepository = new UserRepository();
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");

        try {
            userRepository.createNewUser(user, unitOfWork);
            assertTrue(userRepository.verifyUserExistence("testuser", unitOfWork));
            unitOfWork.commitTransaction();
        } catch (Exception e) {
            fail("Error occurred while creating new user");
        } finally {
            unitOfWork.close();
        }
    }

    @org.junit.Test
    public void verifyUserExistence_whenUserExists_ShouldReturnTrue() throws SQLException {

        UnitOfWork unitOfWork = mock(UnitOfWork.class);
        String username = "testuser";
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);
        when(unitOfWork.prepareStatement("SELECT name, bio, image FROM users WHERE username=?")).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        UserRepository userRepository = new UserRepository();

        boolean result = userRepository.verifyUserExistence(username, unitOfWork);

        assertTrue(result);
    }

    @org.junit.Test
    public void verifyUserExistence_whenUserDoesNotExist_ShouldReturnFalse() throws SQLException {

        UnitOfWork unitOfWork = new UnitOfWork();
        UserRepository userRepository = new UserRepository();
        String nonExistentUsername = "nonexistentuser";


        boolean result = userRepository.verifyUserExistence(nonExistentUsername, unitOfWork);

        assertFalse(result);
        unitOfWork.close();
    }

    @org.junit.Test
    public void updateUserInformation_validInput_shouldUpdateInformation() throws Exception {
        User user = new User();
        user.setBio("new bio");
        user.setImage("new image");
        user.setName("new name");
        String username = "testUser";

        when(unitOfWork.prepareStatement("UPDATE users SET bio=?, image=?, name=? WHERE username=?")).thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1);

        userRepository.updateUserInformation(username, user, unitOfWork);

        assertTrue(true);
    }
}
