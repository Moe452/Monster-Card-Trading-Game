package at.fhtw.sampleapp.service;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.sampleapp.controller.LoginController;
import at.fhtw.sampleapp.repository.UserRepository;

public class LoginService implements Service {

    private final LoginController loginController;

    public LoginService() {
        this.loginController = new LoginController(new UserRepository());
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.POST) {
            try {
                return loginController.login(request);
            } catch (Exception e) {
                return new Response(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ContentType.JSON,
                        "{ \"message\" : \"Server error occurred\" }"
                );
            }
        } else {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{\"error\":\"Invalid request method\"}"
            );
        }
    }
}
