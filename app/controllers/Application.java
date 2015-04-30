package controllers;

import models.Audio;
import play.*;
import play.libs.MimeTypes;
import play.mvc.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import play.data.*;

public class Application extends Controller {
    public static final String HOMEROUTE = Play.configuration.getProperty("home.route");

    public static void index() {
        List<Audio> audios = Audio.findAll();
        render(audios);
    }

    public static void upload(Upload audioFile) throws Exception{
        String filename = audioFile.getFileName();
        Audio audioEntity = new Audio(filename);
        audioEntity.save();

        FileOutputStream out = new FileOutputStream(HOMEROUTE + "/public/audios/" + filename);
        out.write(audioFile.asBytes());
        out.close();
        index();
    }

    public static void downloadFile(final Long fileId) throws IOException {
        response.setHeader("Accept-Ranges", "bytes");
//        notFoundIfNull(fileId);
        Audio audio = Audio.findById(fileId);

        File underlyingFile = new File(HOMEROUTE + "/public/audios/" + audio.name); //load file

        Http.Header rangeHeader = request.headers.get("range");
        if (rangeHeader != null) {
            throw new PartialContent(underlyingFile, audio.name);
        } else {
            renderBinary(new FileInputStream(underlyingFile),
                    audio.name, underlyingFile.length(),
                    MimeTypes.getContentType(audio.name), false);
        }
    }

}