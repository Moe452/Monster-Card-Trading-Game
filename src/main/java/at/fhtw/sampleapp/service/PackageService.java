package at.fhtw.sampleapp.service;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;
import at.fhtw.sampleapp.controller.PackageController;
import at.fhtw.sampleapp.repository.PackageRepository;

public class PackageService implements Service {

    private final PackageController packageController;

    public PackageService() {
        this.packageController = new PackageController(new PackageRepository());
    }

    @Override
    public Response handleRequest(Request request) {
        switch (request.getMethod()) {
            case POST:
                return this.packageController.createCardPackage(request);
            default:
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{\"error\":\"Invalid request method\"}"
                );
        }
    }
}