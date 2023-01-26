package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.repository.PackageRepository;
import at.fhtw.sampleapp.model.Card;

public class PackageController extends Controller {

    private final PackageRepository packageRepository;

    public PackageController(PackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }
    public Response createCardPackage(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();
        try {
            if(!request.isAdminTokenValid()){
                unitOfWork.close();
                return new Response(
                        HttpStatus.FORBIDDEN,
                        ContentType.PLAIN_TEXT,
                        "User is not \"admin\""
                );
            }

            String packageID;
            do{
                packageID=generatePackageIdentification(15);
            }while(this.packageRepository.verifyPackagePresence(packageID, unitOfWork));



            Card[] cards = this.getObjectMapper().readValue(request.getBody(), Card[].class);
            for (Card card : cards){
                if(card.getName().contains("Fire")){
                    card.setElement("fire");
                }else if(card.getName().contains("Water")){
                    card.setElement("water");
                }else{
                    card.setElement("normal");
                }

                if(card.getName().contains("Spell")){
                    card.setType("spell");
                }else{
                    card.setType("monster");
                }

                if(this.packageRepository.verifyCardPresence(card.getId(), unitOfWork)){
                    unitOfWork.close();
                    return new Response(
                            HttpStatus.CONFLICT,
                            ContentType.PLAIN_TEXT,
                            "One or more cards in the package already exist"
                    );
                }


            }
            this.packageRepository.createNewPackage(cards, packageID, unitOfWork);

            unitOfWork.commitTransaction();

            return new Response(
                    HttpStatus.CREATED,
                    ContentType.PLAIN_TEXT,
                    "Package and its cards have been created successfully."
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

    private String generatePackageIdentification(int length) {
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

}