package controllers;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import models.Audio;
import models.User;
import play.*;
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
        System.out.println(email + password + xpassword);
        User user = new User(email, password);
        user.token = System.currentTimeMillis() + email;
        user.save();
        response.setCookie("token", user.token);
        renderJSON("{ \"status\": \"User registered\", \"token\": \"" + user.token + "\"}");
    }

    public static void index() {
        Http.Cookie userLoggedInCookie = request.cookies.get("token");
        if(userLoggedInCookie == null) {
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
        audioEntity.save();

        FileOutputStream out = new FileOutputStream(HOMEROUTE + "/public/audios/" + filename);
        out.write(audioFile.asBytes());
        out.close();

        Mp3File mp3file = new Mp3File(HOMEROUTE + "/public/audios/" + filename);
        System.out.println("Length of this mp3 is: " + mp3file.getLengthInSeconds() + " seconds");
        System.out.println("Bitrate: " + mp3file.getLengthInSeconds() + " kbps " + (mp3file.isVbr() ? "(VBR)" : "(CBR)"));
        System.out.println("Sample rate: " + mp3file.getSampleRate() + " Hz");
        System.out.println("Has ID3v1 tag?: " + (mp3file.hasId3v1Tag() ? "YES" : "NO"));
        System.out.println("Has ID3v2 tag?: " + (mp3file.hasId3v2Tag() ? "YES" : "NO"));
        System.out.println("Has custom tag?: " + (mp3file.hasCustomTag() ? "YES" : "NO"));

        if (mp3file.hasId3v1Tag()) {
            ID3v1 id3v1Tag = mp3file.getId3v1Tag();
            System.out.println("Track: " + id3v1Tag.getTrack());
            System.out.println("Artist: " + id3v1Tag.getArtist());
            System.out.println("Title: " + id3v1Tag.getTitle());
            System.out.println("Album: " + id3v1Tag.getAlbum());
            System.out.println("Year: " + id3v1Tag.getYear());
            System.out.println("Genre: " + id3v1Tag.getGenre() + " (" + id3v1Tag.getGenreDescription() + ")");
            System.out.println("Comment: " + id3v1Tag.getComment());
        }
        if (mp3file.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3file.getId3v2Tag();
            System.out.println("Track: " + id3v2Tag.getTrack());
            System.out.println("Artist: " + id3v2Tag.getArtist());
            System.out.println("Title: " + id3v2Tag.getTitle());
            System.out.println("Album: " + id3v2Tag.getAlbum());
            System.out.println("Year: " + id3v2Tag.getYear());
            System.out.println("Genre: " + id3v2Tag.getGenre() + " (" + id3v2Tag.getGenreDescription() + ")");
            System.out.println("Comment: " + id3v2Tag.getComment());
            System.out.println("Composer: " + id3v2Tag.getComposer());
            System.out.println("Publisher: " + id3v2Tag.getPublisher());
            System.out.println("Original artist: " + id3v2Tag.getOriginalArtist());
            System.out.println("Album artist: " + id3v2Tag.getAlbumArtist());
            System.out.println("Copyright: " + id3v2Tag.getCopyright());
            System.out.println("URL: " + id3v2Tag.getUrl());
            System.out.println("Encoder: " + id3v2Tag.getEncoder());
        }

        if (mp3file.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3file.getId3v2Tag();
            byte[] imageData = id3v2Tag.getAlbumImage();
            if (imageData != null) {
                String mimeType = id3v2Tag.getAlbumImageMimeType();
                System.out.println("Mime type: " + mimeType);
                // Write image to file - can determine appropriate file extension from the mime type
                RandomAccessFile file = new RandomAccessFile(HOMEROUTE + "/public/album-artwork/" + filename+".artwork", "rw");
                file.write(imageData);
                file.close();
            }
        }

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