package controllers;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import models.Audio;
import models.User;
import play.*;
import play.db.jpa.JPABase;
import play.libs.MimeTypes;
import play.mvc.*;

import java.io.*;
import java.util.List;

import play.data.*;

public class Application extends Controller {
    public static final String HOMEROUTE = Play.configuration.getProperty("home.route");

    public static void loginForm(){
        render();
    }

    public static void registrationForm(){
        render();
    }


    public static void login(String email, String password){
        User user = User.find("byEmailAndPassword", email, password).first();
        if(user == null){
            renderJSON("{ \"status\": \"User not found\"}");
        } else {
            user.token = System.currentTimeMillis() + email;
            response.setCookie("token", user.token);
            user.save();
            renderJSON("{ \"token\": \"" + user.token + "\"}");
        }
    }

    public static void register(String email, String password, String xpassword){
      //  ^[_A-Za-z0-9-\+]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,})$

        if(email.contains("@") && email.contains(".")) {

            if(password.equals(xpassword)){
                User user = new User(email, password);
                user.token = System.currentTimeMillis() + email;
                user.save();
                response.setCookie("token", user.token);
                renderJSON("{ \"status\": \"User registered\", \"token\": \"" + user.token + "\"}");
            }else{
                renderJSON("{ \"status\": \"Password missmatch\"}");
            }
        }else{
            renderJSON("{ \"status\": \"Invalid email\"}");
        }

    }

    public static void index() {
        Http.Cookie userLoggedInCookie = request.cookies.get("token");
        try {
            if(userLoggedInCookie == null || User.find("byToken", userLoggedInCookie.value).first() == null) {
                loginForm();
            }
        } catch (JPABase.JPAQueryException ex){
            loginForm();
        }

        User user = User.find("byToken", userLoggedInCookie.value).first();
        List<Audio> audios = Audio.find("byUser", user).fetch();
        render(audios);
    }

    public static void logout() {
        response.removeCookie("token");
        index();
    }

    public static void upload(Upload audioFile) throws Exception{
        Http.Cookie userLoggedInCookie = request.cookies.get("token");
        User user = User.find("byToken", userLoggedInCookie.value).first();

        String filename = audioFile.getFileName();
        Audio audioEntity = new Audio(filename);
        audioEntity.user = user;

        FileOutputStream out = new FileOutputStream(HOMEROUTE + "/public/audios/" + filename);
        out.write(audioFile.asBytes());
        out.close();

        Mp3File mp3file = new Mp3File(HOMEROUTE + "/public/audios/" + filename);
        audioEntity.length = mp3file.getLengthInSeconds();
        audioEntity.bitrate = mp3file.getBitrate();
        audioEntity.sampleRate = mp3file.getSampleRate();
        audioEntity.hasId3v1Tag = mp3file.hasId3v1Tag();
        audioEntity.hasId3v2Tag = mp3file.hasId3v2Tag();
        audioEntity.hasCustomTag = mp3file.hasCustomTag();

        if (mp3file.hasId3v1Tag()) {
            ID3v1 id3v1Tag = mp3file.getId3v1Tag();
            System.out.println(id3v1Tag.getArtist());
            audioEntity.artist = id3v1Tag.getArtist();
            audioEntity.title = id3v1Tag.getTitle();
            audioEntity.album = id3v1Tag.getAlbum();
            audioEntity.year = id3v1Tag.getYear();
            audioEntity.genre = id3v1Tag.getGenre();
            audioEntity.genreDescription = id3v1Tag.getGenreDescription();
        }
        if (mp3file.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3file.getId3v2Tag();
            System.out.println(id3v2Tag.getArtist());
            audioEntity.artist = id3v2Tag.getArtist();
            audioEntity.title = id3v2Tag.getTitle();
            audioEntity.album = id3v2Tag.getAlbum();
            audioEntity.year = id3v2Tag.getYear();
            audioEntity.genre = id3v2Tag.getGenre();
            audioEntity.genreDescription = id3v2Tag.getGenreDescription();
            audioEntity.encoder = id3v2Tag.getEncoder();
        }
        audioEntity.save();
        if (mp3file.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3file.getId3v2Tag();
            byte[] imageData = id3v2Tag.getAlbumImage();
            if (imageData != null) {
                String mimeType = id3v2Tag.getAlbumImageMimeType();
                System.out.println("Mime type: " + mimeType);
                // Write image to file - can determine appropriate file extension from the mime type
                RandomAccessFile file = new RandomAccessFile(HOMEROUTE + "/public/album-artwork/" + audioEntity.getId()+".jpg", "rw");
                file.write(imageData);
                file.close();
            }
        }
        audioEntity.save();
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