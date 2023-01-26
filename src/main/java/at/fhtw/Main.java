package at.fhtw;

import at.fhtw.httpserver.server.Server;
import at.fhtw.httpserver.utils.Router;
import at.fhtw.sampleapp.service.*;
import at.fhtw.sampleapp.service.echo.EchoService;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(10001, configureRouter());
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Router configureRouter() {
        Router router = new Router();
        router.addService("/weather", new WeatherService());
        router.addService("/echo", new EchoService());
        router.addService("/users", new UserService());
        router.addService("/sessions", new LoginService());
        router.addService("/packages", new PackageService());
        router.addService("/transactions", new TransactionService());
        router.addService("/cards", new CardService());
        router.addService("/deck", new CardService());
        router.addService("/stats", new BattleService());

        return router;
    }
}

