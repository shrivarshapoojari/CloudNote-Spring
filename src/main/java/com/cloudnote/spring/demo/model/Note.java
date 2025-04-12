package com.cloudnote.spring.demo.model;



import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Note() {
    }

    @Lob
    private String content;

    public Note(Long id, String content, String ownerUsername) {
        this.id = id;
        this.content = content;
        this.ownerUsername = ownerUsername;
    }

    private String ownerUsername;
}
