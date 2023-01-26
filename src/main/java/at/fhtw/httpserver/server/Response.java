package at.fhtw.httpserver.server;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Response {
    private final int status;
    private final String message;
    private String contentType;
    private final String content;


    public Response(HttpStatus httpStatus, ContentType contentType, String content) {
        this.status = httpStatus.code;
        this.message = httpStatus.message;
        this.contentType = contentType.type;
        this.content = content;
    }

    public String get() {

        String localDatetime = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneId.of("UTC")));
        return "HTTP/1.1 " + this.status + " " + this.message + "\r\n" +
                "Cache-Control: max-age=0\r\n" +
                "Connection: close\r\n" +
                "Date: " + localDatetime + "\r\n" +
                "Expires: " + localDatetime + "\r\n" +
                "Content-Type: " + this.contentType + "\r\n" +
                "Content-Length: " + this.content.length() + "\r\n" +
                "\r\n" +
                this.content;
    }

    public int getStatus() {
        return status;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = String.valueOf(contentType);
    }

    private String body;

    public String getBody() {
        return body;
    }


}
