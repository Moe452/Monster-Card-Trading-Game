package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.repository.UserRepository;
import at.fhtw.sampleapp.model.User;

public class LoginController extends Controller {

    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Response login(Request request) {
        try (UnitOfWork unitOfWork = new UnitOfWork()) {
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);

            String content = this.userRepository.verifyLoginInformation(user, unitOfWork);

            if ("None".equals(content)) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.PLAIN_TEXT,
                        "Missing or invalid authentication information"
                );
            }

            return new Response(
                    HttpStatus.OK,
                    ContentType.PLAIN_TEXT,
                    "Login successful\n" + content
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Server error occurred\" }"
            );
        }
    }
}
