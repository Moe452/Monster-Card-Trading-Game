package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.repository.UserRepository;
import at.fhtw.sampleapp.model.User;

public class UserController extends Controller {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Response createNewUser(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();
        try {
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);
            if (this.userRepository.verifyUserExistence(user.getUsername(), unitOfWork)) {
                unitOfWork.rollbackTransaction();
                return new Response(
                        HttpStatus.CONFLICT,
                        ContentType.PLAIN_TEXT,
                        "A user with that username already exists."
                );
            }
            this.userRepository.createNewUser(user, unitOfWork);
            unitOfWork.commitTransaction();

            return new Response(
                    HttpStatus.CREATED,
                    ContentType.PLAIN_TEXT,
                    "User successfully created"
            );
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

    public Response updateUserInformation(Request request) throws Exception {
        UnitOfWork unitOfWork = new UnitOfWork();
        if (!request.isAuthenticationTokenValid()) {
            unitOfWork.close();
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Missing or invalid authentication information"
            );
        }

        try {

            String username = request.getPathParts().get(1);
            if (!this.userRepository.verifyUserExistence(username, unitOfWork)) {
                unitOfWork.close();
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "User not found"
                );
            }

            User user = this.getObjectMapper().readValue(request.getBody(), User.class);

            this.userRepository.updateUserInformation(username, user, unitOfWork);

            unitOfWork.commitTransaction();

            return new Response(
                    HttpStatus.OK,
                    ContentType.PLAIN_TEXT,
                    "The user's information has been updated successfully"
            );
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

    public Response retrieveUserInformation(Request request) throws Exception {
        UnitOfWork unitOfWork = new UnitOfWork();
        if (!request.isAuthenticationTokenValid()) {
            unitOfWork.close();
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Missing or invalid authentication information"
            );
        }

        String username = request.getPathParts().get(1);
        try {
            if (!this.userRepository.verifyUserExistence(username, unitOfWork)) {
                unitOfWork.close();
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "User not found"
                );
            }
            User user = this.userRepository.retrieveUserInformation(username, unitOfWork);
            String userDataJSON = this.getObjectMapper().writeValueAsString(user);

            unitOfWork.close();

            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    userDataJSON
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        unitOfWork.close();
        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\" : \"Server error occurred\" }"
        );
    }

}

