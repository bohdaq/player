package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.List;

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

    @ManyToMany
    public List<Playlist> playlists;


    public Audio(String name){
        this.name = name;
    }

    @ManyToOne
    public User user;
}
