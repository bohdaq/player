package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
public class Playlist extends Model {
    public String name;

    @ManyToMany
    public List<Audio> audios;

    @ManyToOne
    public User user;

    public Playlist(User user, String name) {
        this.user = user;
        this.name = name;
    }
}
