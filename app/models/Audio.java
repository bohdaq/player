package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Audio extends Model {
    public String year;
    public String name;
    public String artist;
    public String title;
    public String album;
    public Long length;
    public Integer bitrate;
    public Integer sampleRate;
    public Integer genre;
    public String genreDescription;
    public String encoder;
    public boolean hasId3v1Tag;
    public boolean hasId3v2Tag;
    public boolean hasCustomTag;


    public Audio(String name){
        this.name = name;
    }

    @ManyToOne
    public User user;
}
