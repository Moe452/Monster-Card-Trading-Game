package at.fhtw.sampleapp.service;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.sampleapp.controller.CardController;
import at.fhtw.sampleapp.repository.CardRepository;
import at.fhtw.sampleapp.repository.UserRepository;

public class CardService implements Service {
    private final CardController cardController;
    private static final String CARDS_ENDPOINT = "cards";
    private static final String DECK_ENDPOINT = "deck";
    private static final String CREATE_CARD_ENDPOINT = "create_card";

    public CardService() {
        this.cardController = new CardController(new CardRepository(), new UserRepository());
    }

    @Override
    public Response handleRequest(Request request) {
        String endpoint = request.getPathParts().get(0);
        Method method = request.getMethod();
        try {
            if (endpoint.equals(CARDS_ENDPOINT) && method == Method.GET) {
                return this.cardController.displayCards(request);
            } else if (endpoint.equals(DECK_ENDPOINT)) {
                if (method == Method.GET) {
                    return this.cardController.displayDeck(request);
                } else if (method == Method.PUT) {
                    return this.cardController.setUpDeck(request);
                }else if (endpoint.equals(CREATE_CARD_ENDPOINT) && method == Method.POST) {
                    return this.cardController.createCustomCard(request);
                }
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\":\"Invalid request method\"}");
    }
}
