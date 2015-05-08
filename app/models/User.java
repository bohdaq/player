package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class User extends Model {
    public User(String email, String password){
        this.email = email;
        this.password = password;
    }
    public String email;
    public String password;
    public String token;

    @OneToMany
    public List<Audio> audios;
}