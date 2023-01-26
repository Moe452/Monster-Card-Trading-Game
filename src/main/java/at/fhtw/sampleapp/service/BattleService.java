package at.fhtw.sampleapp.service;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.sampleapp.controller.BattleController;
import at.fhtw.sampleapp.repository.BattleRepository;
import at.fhtw.sampleapp.repository.CardRepository;
import at.fhtw.sampleapp.repository.UserRepository;


public class BattleService implements Service {
    private final BattleController battleController;
    private static final String STATS_ENDPOINT = "stats";
    public BattleService() {
        this.battleController = new BattleController(new BattleRepository(), new UserRepository(), new CardRepository());
    }

    @Override
    public Response handleRequest(Request request) {
        String endpoint = request.getPathParts().get(0);
        Method method = request.getMethod();
        try {
            if (endpoint.equals(STATS_ENDPOINT) && method == Method.GET) {
                return this.battleController.retrieveUserStatistics(request);
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Missing or invalid authentication information");
    }
}

