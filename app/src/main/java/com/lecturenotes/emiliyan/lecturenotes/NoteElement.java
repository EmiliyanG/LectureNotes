package com.lecturenotes.emiliyan.lecturenotes;

import java.io.Serializable;

/**
 * Created by Emiliyan on 3/12/2016.
 */
public class NoteElement implements Serializable {

    private boolean isPicture = false;

    private String data;

    public NoteElement(boolean isPicture, String data){
        this.isPicture = isPicture;
        this.data = data;
    }
    public boolean isPicture(){ return isPicture;}
    public String getData(){return data;}
}
