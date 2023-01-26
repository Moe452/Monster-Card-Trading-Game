package at.fhtw.sampleapp.service;

import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.model.Card;
import at.fhtw.sampleapp.repository.CardRepository;
import at.fhtw.sampleapp.repository.PackageRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class CardRepositoryTest {
    @Test
   public void verifyCardPresence_whenCardExists_ShouldReturnTrue() throws SQLException {
        // Arrange
        String cardID = "845f0dc7-37d0-426e-994e-43fc3ac83c08";
        UnitOfWork unitOfWork = new UnitOfWork();

        // Act
        boolean result = new PackageRepository().verifyCardPresence(cardID, unitOfWork);
        unitOfWork.close();

        // Assert
        Assert.assertTrue(result);
    }

    @Test
    public void verifyCardPresence_whenCardDoesNotExist_ShouldReturnFalse() throws SQLException {
        // Arrange
        String cardID = "999";
        UnitOfWork unitOfWork = new UnitOfWork();

        // Act
        boolean result = new PackageRepository().verifyCardPresence(cardID, unitOfWork);
        unitOfWork.close();

        // Assert
        Assert.assertFalse(result);

    }
    //unit Test for custom Card
    @org.junit.Test
    public void testCreateCustomCard() {
        CardRepository repository = new CardRepository();
        UnitOfWork unitOfWork = new UnitOfWork();
        Card card = new Card();
        card.setName("Fireball");
        card.setType("Spell");
        card.setId("1234");
        card.setDamage(20);
        card.setElement("Fire");
        card.setPackageID("Package1");

        repository.createCustomCard(card, unitOfWork);

        unitOfWork.commitTransaction();
    }
}
