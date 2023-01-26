package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;

import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.repository.CardRepository;
import at.fhtw.sampleapp.repository.UserRepository;
import at.fhtw.sampleapp.model.Card;



import java.util.ArrayList;
import java.util.List;


public class CardController extends Controller {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public CardController(CardRepository cardRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    public Response createCustomCard(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();
        try {
            Card card = this.getObjectMapper().readValue(request.getBody(), Card.class);
            this.cardRepository.createCustomCard(card, unitOfWork);
            unitOfWork.commitTransaction();
            return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "Card successfully created");
        } catch (Exception e) {
            e.printStackTrace();
        }
        unitOfWork.rollbackTransaction();
        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Server error occurred\" }"
        );
    }
    public Response setUpDeck(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String username = request.getTokenUser();

        if (username.equals("")) {
            unitOfWork.close();
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "The provided authentication details are either missing or not valid."
            );
        }
        try {
            boolean userExists = this.userRepository.verifyUserExistence(username, unitOfWork);
            if (!userExists) {
                unitOfWork.close();
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "User not found"
                );
            }

            List<String> cardIDs = new ArrayList<String>();
            String requestBody = request.getBody();

            String[] splitRequest = requestBody.split(", \"");
            if (splitRequest.length != 4) {
                unitOfWork.close();
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.PLAIN_TEXT,
                        "Deck provided is missing necessary cards"
                );
            }
            for (int i = 0; i < 4; i++) {
                splitRequest[i] = splitRequest[i].replace("[", "");
                splitRequest[i] = splitRequest[i].replace("]", "");
                splitRequest[i] = splitRequest[i].replace("\"", "");
                cardIDs.add(splitRequest[i]);
            }

            if (!this.cardRepository.verifyCardOwnershipByUser(username, cardIDs, unitOfWork)) {
                unitOfWork.close();
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.PLAIN_TEXT,
                        "At least one of the provided cards does not belong to the user or is not available."
                );
            }

            this.cardRepository.placeCardsInDeck(username, cardIDs, unitOfWork);


            unitOfWork.commitTransaction();

            return new Response(
                    HttpStatus.OK,
                    ContentType.PLAIN_TEXT,
                    "The deck has been successfully configured"
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        unitOfWork.rollbackTransaction();

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"User not found\" }"
        );
    }

    public Response displayDeck(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String username = request.getTokenUser();

        if (username.equals("")) {
            unitOfWork.close();
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "The provided authentication details are either missing or not valid."
            );
        }
        try {
            boolean userExists = this.userRepository.verifyUserExistence(username, unitOfWork);
            if (!userExists) {
                unitOfWork.close();
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "User not found"
                );
            }

            List<Card> cards = this.cardRepository.displayDeckOfUser(username, unitOfWork);

            if (cards.isEmpty()) {
                unitOfWork.close();
                return new Response(
                        HttpStatus.NO_CONTENT,
                        ContentType.PLAIN_TEXT,
                        "The request was valid, but the deck has no cards."
                );
            }
            String cardsJSON = this.getObjectMapper().writeValueAsString(cards);
            unitOfWork.close();
            if (request.hasParams() && request.getParams().equals("format=plain")) {
                return new Response(
                        HttpStatus.OK,
                        ContentType.PLAIN_TEXT,
                        cardsJSON
                );
            } else {
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        cardsJSON
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        unitOfWork.close();
        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"User not found\" }"
        );
    }
    public Response displayCards(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();

        String username = request.getTokenUser();

        if (username.equals("")) {
            unitOfWork.close();
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Missing or invalid authentication information"
            );
        }

        try {
            boolean userExists = this.userRepository.verifyUserExistence(username, unitOfWork);
            if (!userExists) {
                unitOfWork.close();
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "User not found"
                );
            }

            List<Card> cards = this.cardRepository.retrieveCards(username, unitOfWork);

            if (cards.isEmpty()) {
                unitOfWork.close();
                return new Response(
                        HttpStatus.NO_CONTENT,
                        ContentType.PLAIN_TEXT,
                        "User has no cards associated with their account"
                );
            }
            String cardsJSON = this.getObjectMapper().writeValueAsString(cards);

            unitOfWork.close();
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    cardsJSON
            );


        } catch (Exception e) {
            e.printStackTrace();
        }


        unitOfWork.close();
        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"User not found\" }"
        );
    }
}






