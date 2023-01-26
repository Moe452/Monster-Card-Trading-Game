package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.repository.BattleRepository;
import at.fhtw.sampleapp.repository.CardRepository;
import at.fhtw.sampleapp.repository.UserRepository;
import at.fhtw.sampleapp.model.Stats;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BattleController extends Controller {
    private BattleRepository battleRepository;
    private UserRepository userRepository;
    private CardRepository cardRepository;

    public BattleController(BattleRepository battleRepository, UserRepository userRepository, CardRepository cardRepository) {
        this.battleRepository = battleRepository;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
    }

    public Response retrieveUserStatistics(Request request) {

        UnitOfWork unitOfWork = new UnitOfWork();
        String username = request.getTokenUser();

        if (username == null || username.isEmpty()) {
            unitOfWork.close();
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Missing or invalid authentication information."
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

            Stats stats = this.userRepository.retrieveUserStats(username, unitOfWork);
            String userStatsJson = new ObjectMapper().writeValueAsString(stats);

            unitOfWork.close();
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    userStatsJson
            );

        } catch (Exception e) {
            e.printStackTrace();
            unitOfWork.close();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Server error occurred\" }"
            );
        }
    }
}