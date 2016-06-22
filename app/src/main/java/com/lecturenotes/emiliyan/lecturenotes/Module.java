package com.lecturenotes.emiliyan.lecturenotes;

/**
 * Created by Emiliyan on 2/29/2016.
 */
import android.provider.ContactsContract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
public class Module implements Serializable{
    private String moduleName;
    private final UUID MODULE_ID;
    private ArrayList<Note> notes = new ArrayList<Note>();
    public Module(String moduleName){
         this.moduleName = moduleName;
        MODULE_ID=UUID.randomUUID();
    }

    public void addNote(Note note){
        notes.add(0,note);
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public UUID getModuleID(){
        return MODULE_ID;
    }
    public String getModuleName(){
        return moduleName;
    }

}
