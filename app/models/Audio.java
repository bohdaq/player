package models;

import play.db.jpa.Model;

import javax.persistence.Entity;

@Entity
public class Audio extends Model {
    public Audio(String name){
        this.name = name;
    }
    public String name;
}
