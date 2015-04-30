package controllers;

import play.*;
import play.libs.MimeTypes;
import play.mvc.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import models.*;

public class Application extends Controller {
    static String homeRoute = Play.configuration.getProperty("home.route");

    public static void index() {
        render();
    }

    public static void downloadFile(final Long fileId) throws IOException {
        response.setHeader("Accept-Ranges", "bytes");
//        notFoundIfNull(fileId);

        String fileName = "Bcee_Think_Twice.mp3";//name of the file
        File underlyingFile = new File(homeRoute + "/public/audios/Bcee_Think_Twice.mp3"); //load file

        Http.Header rangeHeader = request.headers.get("range");
        if (rangeHeader != null) {
            throw new PartialContent(underlyingFile, fileName);
        } else {
            renderBinary(new FileInputStream(underlyingFile),
                    fileName, underlyingFile.length(),
                    MimeTypes.getContentType(fileName), false);
        }
    }

}