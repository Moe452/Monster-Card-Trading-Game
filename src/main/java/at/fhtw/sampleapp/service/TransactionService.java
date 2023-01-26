package at.fhtw.sampleapp.service;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.sampleapp.controller.TransactionController;
import at.fhtw.sampleapp.repository.PackageRepository;
import at.fhtw.sampleapp.repository.UserRepository;

public class TransactionService implements Service {

    private final TransactionController transactionController;
    private static final String STATS_ENDPOINT = "packages";
    public TransactionService() {
        this.transactionController = new TransactionController(new PackageRepository(), new UserRepository());
    }

    @Override
    public Response handleRequest(Request request) {
        Response response;
        try {
            if (request.getMethod() == Method.POST && request.getPathParts().get(1).equals(STATS_ENDPOINT)) {
                response = this.transactionController.buyPackage(request);
            } else {
                response = new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{\"error\":\"Invalid request method\"}");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }
}




