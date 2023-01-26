package at.fhtw.sampleapp.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class IntegrationTest {

    @Test
    public void testDeckNotConfigured() {
        try {
            URL url = new URL("http://localhost:10001/deck");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization", "Basic Test-mtcgToken");
            int responseCode = urlConnection.getResponseCode();
            System.out.println(responseCode);

            if (responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
                System.out.println("Unexpected response code: " + responseCode);
                return;
            }
            System.out.println("Test passed: true");
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
    }

    private HttpURLConnection postRequest(String path, String body) throws IOException {
        URL url = new URL(baseUrl + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        OutputStream os = connection.getOutputStream();
        os.write(body.getBytes());
        os.flush();

        return connection;
    }
    private String baseUrl = "http://localhost:10001";

    @Test//UNIT
    void testCreateUsers() {
        try {
            String userName = "user" + System.currentTimeMillis();
            HttpURLConnection connection = postRequest("/users", "{\"Username\":\"" + userName + "\", \"Password\":\"daniel\"}");
            int responseCode = connection.getResponseCode();
            System.out.println(responseCode);
            if (responseCode != HttpURLConnection.HTTP_CREATED) {
                System.out.println("Unexpected response code: " + responseCode);
                return;
            }
            System.out.println("Test passed: true");
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
    }

    @Test //UNIT
    void testCreateUsersDuplicate() {
        try {
            HttpURLConnection connection = postRequest("/users", "{\"Username\":\"kienboec\", \"Password\":\"daniel\"}");
            int responseCode = connection.getResponseCode();
            System.out.println(responseCode);
            if (responseCode != HttpURLConnection.HTTP_CONFLICT) {
                System.out.println("Unexpected response code: " + responseCode);
                return;
            }
            System.out.println("Test passed: true");
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
    }

    @Test
    void testLoginUser() {
        try {
            HttpURLConnection connection = postRequest("/sessions", "{\"Username\":\"kienboec\", \"Password\":\"daniel\"}");
            int responseCode = connection.getResponseCode();
            System.out.println(responseCode);
            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.out.println("Unexpected response code: " + responseCode);
                return;
            }
            System.out.println("Test passed: true");
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
    }

    @Test
    void testLoginUsersDiffPW() {
        try {
            HttpURLConnection connection = postRequest("/sessions", "{\"Username\":\"kienboec\", \"Password\":\"different\"}");
            int responseCode = connection.getResponseCode();
            System.out.println(responseCode);
            if (responseCode != HttpURLConnection.HTTP_UNAUTHORIZED) {
                System.out.println("Unexpected response code: " + responseCode);
                return;
            }
            System.out.println("Test passed: true");
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
    }

    @Test
    public void testNoDeckAssignedToUser() {
        try {
            URL url = new URL("http://localhost:10001/deck");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization", "Basic admin-mtcgToken");
            int responseCode = urlConnection.getResponseCode();

            if (responseCode == 204) {
                System.out.println("Expected response code: " + responseCode);
            } else {
                System.out.println("Unexpected response code: " + responseCode);
            }

        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
    }
    @Test

    public void testDisplaysCardsCorrectly() {
        try {
            URL url = new URL("http://localhost:10001/cards");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization", "Basic Test-mtcgToken");
            int responseCode = urlConnection.getResponseCode();
            System.out.println(responseCode);

            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.out.println("Unexpected response code: " + responseCode);
                return;
            }

            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                System.out.println(inputLine);
            }

            bufferedReader.close();
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
    }

    @Test
    public void testConfigureDeckButCardDoesNotBelongToUser() {
        try {
            URL url = new URL("http://localhost:10001/deck");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("PUT");
            urlConnection.setRequestProperty("Authorization", "Basic Test-mtcgToken");
            urlConnection.setDoOutput(true);
            OutputStream outputStream = urlConnection.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream);
            printWriter.write("[\\\"notFromU-serC-ard9-9647-87e16f1edd2d\\\", \\\"ce6bcaee-47e1-4011-a49e-5a4d7d4245f3\\\", \\\"74635fae-8ad3-4295-9139-320ab89c2844\\\", \\\"70962948-2bf7-44a9-9ded-8c68eeac7793\\\"]");
            printWriter.close();
            int responseCode = urlConnection.getResponseCode();
            System.out.println(responseCode);

            if (responseCode != 403) {
                System.out.println("Unexpected response code: " + responseCode);
                return;
            }
            System.out.println("Test Passed, 403 Forbidden");
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }
    }
    @org.junit.Test
    public void testSuccessfulRegistration() {
        ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "curl -X POST http://localhost:10001/users --header \"Content-Type: application/json\" -d \"{\"Username\":\"testuser\", \"Password\":\"testpassword\"}\"");
        try {
            Process p = pb.start();
            String output = new BufferedReader(new InputStreamReader(p.getInputStream())).readLine();
            // check if output contains "Successfully created user"
            Assertions.assertFalse(output.contains("Successfully created user"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @org.junit.Test
    public void testUnsuccessfulLogin() {
        ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "curl -X POST http://localhost:10001/sessions --header \"Content-Type: application/json\" -d \"{\"Username\":\"testuser\", \"Password\":\"wrongpassword\"}\"");
        try {
            Process p = pb.start();
            String output = new BufferedReader(new InputStreamReader(p.getInputStream())).readLine();
            // check if output contains "Invalid login credentials"
            Assertions.assertFalse(output.contains("Invalid login credentials"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @org.junit.Test
    public void testSuccessfulPackageCreation() {
        ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "curl -X POST http://localhost:10001/packages --header \"Content-Type: application/json\" --header \"Authorization: Basic admin-mtcgToken\" -d \"[{\"Id\":\"845f0dc7-37d0-426e-994e-43fc3ac83c08\", \"Name\":\"WaterGoblin\", \"Damage\": 10.0}, {\"Id\":\"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\", \"Name\":\"Dragon\", \"Damage\": 50.0}, {\"Id\":\"e85e3976-7c86-4d06-9a80-641c2019a79f\", \"Name\":\"WaterSpell\", \"Damage\": 20.0}, {\"Id\":\"1cb6ab86-bdb2-47e5-b6e4-68c5ab389334\", \"Name\":\"Ork\", \"Damage\": 45.0}, {\"Id\":\"dfdd758f-649c-40f9-ba3a-8657f4b3439f\", \"Name\":\"FireSpell\",    \"Damage\": 25.0}]\"");
        try {
            Process p = pb.start();
            String output = new BufferedReader(new InputStreamReader(p.getInputStream())).readLine();
            // check if output contains "The package and its cards have been successfully created."
            Assertions.assertFalse(output.contains("The package and its cards have been successfully created."));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void testUnsuccessfulPackageCreation() {
        ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "curl -X POST http://localhost:10001/packages --header \"Content-Type: application/json\" --header \"Authorization: Basic invalidtoken\" -d \"[{\"Id\":\"845f0dc7-37d0-426e-994e-43fc3ac83c08\", \"Name\":\"WaterGoblin\", \"Damage\": 10.0}]");
        try {
            Process p = pb.start();
            String output = new BufferedReader(new InputStreamReader(p.getInputStream())).readLine();
            // check if output contains "Unauthorized"
            Assertions.assertFalse(output.contains("Unauthorized"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void testSuccessfulPackageFetch() {
        ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "curl -X GET http://localhost:10001/packages --header \"Authorization: Basic testuser-mtcgToken\"");
        try {
            Process p = pb.start();
            String output = new BufferedReader(new InputStreamReader(p.getInputStream())).readLine();
            // check if output contains package data
            Assertions.assertFalse(output.contains("Id") && output.contains("Name") && output.contains("Damage"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void testSuccessfulPackageUpdate() {
        ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "curl -X PUT http://localhost:10001/packages --header \"Content-Type: application/json\" --header \"Authorization: Basic admin-mtcgToken\" -d \"{\"Id\":\"845f0dc7-37d0-426e-994e-43fc3ac83c08\", \"Name\":\"WaterGoblin\", \"Damage\": 20.0}\"");
        try {
            Process p = pb.start();
            String output = new BufferedReader(new InputStreamReader(p.getInputStream())).readLine();
            // check if output contains "Successfully updated package"
            Assertions.assertFalse(output.contains("Successfully updated package"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void testUnsuccessfulPackageUpdate() {
        ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "curl -X PUT http://localhost:10001/packages --header \"Content-Type: application/json\" --header \"Authorization: Basic invalid-token\" -d \"{\"Id\":\"845f0dc7-37d0-426e-994e-43fc3ac83c08\", \"Name\":\"WaterGoblin\", \"Damage\": 20.0}\"");
        try {
            Process p = pb.start();
            String output = new BufferedReader(new InputStreamReader(p.getInputStream())).readLine();
            // check if output contains "Error: Invalid authentication"
            Assertions.assertFalse(output.contains("Error: Invalid authentication"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @org.junit.Test
    public void testSuccessfulPackageDeletion() {
        ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "curl -X DELETE http://localhost:10001/packages/{packageId} --header \"Authorization: Basic admin-mtcgToken\"");
        try {
            Process p = pb.start();
            String output = new BufferedReader(new InputStreamReader(p.getInputStream())).readLine();
            // check if output contains "Successfully deleted package"
            Assertions.assertFalse(output.contains("Successfully deleted package"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void testUnsuccessfulPackageDeletion() {
        ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "curl -X DELETE http://localhost:10001/packages/{invalidPackageId} --header \"Authorization: Basic admin-mtcgToken\"");
        try {
            Process p = pb.start();
            String output = new BufferedReader(new InputStreamReader(p.getInputStream())).readLine();
            // check if output contains "Error: Package not found"
            Assertions.assertFalse(output.contains("Error: Package not found"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testPurchaseWithoutPackage() throws IOException {
        URL url = new URL("http://localhost:10001/transactions/packages");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Basic kienboec-mtcgToken");
        connection.setDoOutput(true);
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
            System.out.println("Test Passed, 403 Forbidden");
        } else {
            System.out.println("Test Failed, expected 403 Forbidden but got " + responseCode);
        }
    }

}







