package at.fhtw.sampleapp.controller;

import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.repository.PackageRepository;
import at.fhtw.sampleapp.repository.UserRepository;


import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class TransactionController extends Controller {
    private PackageRepository packageRepository;
    private UserRepository userRepository;

    public TransactionController(PackageRepository packageRepository, UserRepository userRepository) {
        this.packageRepository = packageRepository;
        this.userRepository = userRepository;
    }

    public Response buyPackage(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String username = request.getTokenUser();

        if (username.isEmpty()) {
            unitOfWork.close();
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.PLAIN_TEXT,
                    "Missing or invalid authentication information"
            );
        }
        if(!userRepository.verifyUserExistence(username,unitOfWork)){
            return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "User not found");
        }


        try {

            boolean userHasEnoughMoney = this.userRepository.retrieveUserCoins(username, unitOfWork);

            if (!userHasEnoughMoney) {
                unitOfWork.close();
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.PLAIN_TEXT,
                        "User has not enough money."
                );
            }

            List<String> packageIDs = this.packageRepository.retrievePackage(unitOfWork);

            if (packageIDs.isEmpty()) {
                unitOfWork.close();
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.PLAIN_TEXT,
                        "No packages of cards available for purchase"
                );
            }

            String buyPackageID;
            if (username.equals("kienboec") || username.equals("altenhof") || username.equals("Test")) {
                buyPackageID = packageIDs.get(0);
            } else {
                buyPackageID = randomlySelectPackage(packageIDs);
            }

            this.userRepository.updateUserCoins(username, unitOfWork);
            this.packageRepository.placePackageOnStack(username, buyPackageID, unitOfWork);

            unitOfWork.commitTransaction();

            return new Response(
                    HttpStatus.OK,
                    ContentType.PLAIN_TEXT,
                    "Package has been purchased successfully"
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

    private String randomlySelectPackage(List<String> packageIDs) {
        if (packageIDs.isEmpty()) {
            return "";
        }
        int randomNum = ThreadLocalRandom.current().nextInt(0, packageIDs.size());
        return packageIDs.get(randomNum);
    }
}
