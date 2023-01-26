package at.fhtw.sampleapp.service;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.sampleapp.controller.UserController;
import at.fhtw.sampleapp.repository.UserRepository;

public class UserService implements Service {

    final UserController userController;

    public UserService() {
        this.userController = new UserController(new UserRepository());
    }

    @Override
    public Response handleRequest(Request request) {
        Response response;
        try {
            switch (request.getMethod()) {
                case POST:
                    response = this.userController.createNewUser(request);
                    break;
                case GET:
                    response = this.userController.retrieveUserInformation(request);
                    break;
                case PUT:
                    response = this.userController.updateUserInformation(request);
                    break;
                default:
                    response = new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\":\"Invalid request method\"}");
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return response;
    }
}