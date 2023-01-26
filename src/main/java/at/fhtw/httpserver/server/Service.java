package at.fhtw.httpserver.server;

public interface Service {
    /**
     * @param request
     * @return
     * @throws Exception
     */
    Response handleRequest(Request request) throws Exception;
}
