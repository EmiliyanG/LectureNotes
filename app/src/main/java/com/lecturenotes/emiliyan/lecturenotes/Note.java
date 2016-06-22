package com.lecturenotes.emiliyan.lecturenotes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Emiliyan on 3/3/2016.
 */
public class Note implements Serializable{
    private long noteName;
    private final UUID noteId;
    private ArrayList<NoteElement> strings ;
    private String noteModule;
    public Note(long noteName,String noteModule){
        this.noteName = noteName;
        noteId=UUID.randomUUID();
        strings= new ArrayList<>();
        this.noteModule = noteModule;
    }


    public long getNoteName() {
        return noteName;
    }

    public void setNoteName(long noteName) {
        this.noteName = noteName;
    }

    public UUID getNoteId() {
        return noteId;
    }


    public void addString(String string) {
        strings.add(new NoteElement(false,string));
    }
    public void addPicture(String pictureFileName){
        strings.add(new NoteElement(true,pictureFileName));
    }


    public ArrayList<NoteElement> getStrings() {
        return strings;
    }

    public void setStrings(ArrayList<NoteElement> strings) {
        this.strings = strings;
    }


    public String getNoteModule() {
        return noteModule;
    }

}
