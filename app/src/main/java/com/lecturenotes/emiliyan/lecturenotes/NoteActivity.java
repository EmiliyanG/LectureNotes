package com.lecturenotes.emiliyan.lecturenotes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class NoteActivity extends AppCompatActivity {
    public static final String IMAGE_FILE_NAME = "imageName";
    private int imgThumbnailRes;

    private String moduleName;
    private long noteName;
    private Note currentNote;
    private TextView editNoteTitle;
    private LinearLayout noteText;
    private ImageButton sketchButton;
    private Button editNoteSave;
    private Button editNoteCancel;
    private ArrayList<NoteViewElement> noteViewElements = new ArrayList<>();
    private EditText editText;
    private Typeface typeHighTower;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        linkComponents();
    }




    private void linkComponents(){
        editNoteTitle = (TextView) findViewById(R.id.editNoteTitle);
        noteText = (LinearLayout) findViewById(R.id.noteText);

        sketchButton = (ImageButton) findViewById(R.id.sketchButton);
        editNoteSave = (Button) findViewById(R.id.editNoteSave);
        editNoteCancel = (Button) findViewById(R.id.editNoteCancel);
        editText = (EditText) findViewById(R.id.editText);
        typeHighTower = Typeface.createFromAsset(getAssets(), "fonts/high_tower_text_italic.ttf");
        styleEditText(editText);


        //get note title
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            noteName = extras.getLong(MainActivity.NOTE_NAME_EXTRAS);
            editNoteTitle.setText( MainActivity.sdf.format(noteName) );

            moduleName =extras.getString(MainActivity.MODULE_NAME_FOR_NOTE_EXTRAS);
            System.out.println(moduleName);
        }
        determineImageThumbnailResolution();

        getNoteState(noteName, moduleName);




        editNoteCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); //close activity
            }
        });
        editNoteSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNoteState();
                finish();
            }
        });
        sketchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDrawingActivity();
            }
        });


    }

    private void determineImageThumbnailResolution(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int w = metrics.widthPixels;//get screen width
        int h  = metrics.heightPixels;//get screen height
        if(w>h) imgThumbnailRes = h/2;//if width is bigger than height orientation is landscape
        else imgThumbnailRes = w/2;
    }

    private Intent drawingActivity(){
        saveNoteState();//save current state
        Intent intent = new Intent(this, DrawingActivity.class);
        intent.putExtra(MainActivity.NOTE_NAME_EXTRAS, noteName);
        intent.putExtra(MainActivity.MODULE_NAME_FOR_NOTE_EXTRAS, moduleName);
        return intent;
    }
    public void startDrawingActivity(){

        Intent intent = drawingActivity();
        startActivity(intent);
        finish();// close activity
        //after the user made a sketch and wants to save it it should appear in the note activity as well
        //therefore note activity will be restarted
    }

    public void startDrawingActivity(String fileName){
        Intent intent = drawingActivity();
        intent.putExtra(IMAGE_FILE_NAME,fileName);
        startActivity(intent);
        finish();// close activity
        //after the user made a sketch and wants to save it it should appear in the note activity as well
        //therefore note activity will be restarted
    }




    private ArrayList<NoteElement> getStrings(){


        ArrayList<NoteElement> strings = new ArrayList<>();
        for(NoteViewElement nve: noteViewElements){
            if(nve.isPicture()){
                strings.add(new NoteElement(true,nve.getFileName()));
            }else{
                strings.add(new NoteElement( false,nve.getMyEditText().getText().toString() ) );
            }
        }
        return strings;
    }

    private ImageView generateImageView(String fileName){
        ImageView imageView = new ImageView(this);
        styleImageView(imageView,fileName);

        noteText.addView(imageView);
        loadImage(fileName, imageView);

        noteViewElements.add(new NoteViewElement(imageView, fileName));
        return imageView;
    }


    private EditText generateEditText(String text){
        EditText ed = new EditText(this);
        ed.setText(text);
        styleEditText(ed);
        noteText.addView(ed);
        noteViewElements.add(new NoteViewElement(ed));
        return ed;
    }


    private void getNoteState(long noteName, String moduleName){

        currentNote = SaveVariables.getNote(this, noteName, moduleName);
        if(currentNote!= null){
           ArrayList<NoteElement> noteStrings =  currentNote.getStrings();
            if(noteStrings!=null && noteStrings.size()>0){
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                for(NoteElement ne: noteStrings){
                    if(ne.isPicture()){// if picture add picture

                       generateImageView(ne.getData());

                    }else{

                        generateEditText(ne.getData());
                    }
                }
                if(noteStrings.get(noteStrings.size()-1).isPicture()){
                    generateEditText("");
                }
            }else{
                editText.setVisibility(View.VISIBLE);

                noteViewElements.add(new NoteViewElement(editText));

                System.out.println("this note has no strings noteActivity getnoteState method");
            }
        }else{
            System.out.println("this note is null noteActivity getnoteState method");
        }
    }

    private void styleImageView(ImageView imageView, final String fileName){
        imageView.setBackgroundColor(ContextCompat.getColor(this, R.color.customDarkest));
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(imgThumbnailRes,imgThumbnailRes);
        parms.setMargins(0,6,0,0);//left,top,right,bottom
        imageView.setLayoutParams(parms);

        imageView.setOnClickListener(new MyOnClickListener(fileName){
            @Override
            public void onClick(View v) {
                startDrawingActivity(fileName);
            }
        });
    }

    private void styleEditText(EditText editText){
        editText.setBackgroundColor(ContextCompat.getColor(this, R.color.customLightest));
        editText.setTextColor(ContextCompat.getColor(this, R.color.customDarkest));
        editText.setTextSize(30);
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        editText.setTypeface(typeHighTower);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editText.setMinimumHeight(70);
        parms.setMargins(0, 6, 0, 0);//left,top,right,bottom
        editText.setLayoutParams(parms);
    }

    private void showKeyboard(){
        InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void saveNoteState(){
        SaveVariables.updateNote(this, moduleName, noteName, getStrings());
    }

    private void loadImage(String fileName, ImageView imageView) {
        LoadImageTask task = new LoadImageTask(this,imageView,imgThumbnailRes);
        task.execute(fileName);
    }


    private class NoteViewElement{
        private ImageView myImageView;
        private EditText myEditText;
        private boolean isPicture = false;
        private String fileName;
        public NoteViewElement(ImageView imageView, String fileName){
            myImageView = imageView;
            this.fileName = fileName;
            isPicture = true;
        }
        public NoteViewElement(EditText editText){
            myEditText = editText;
        }

        public boolean isPicture(){
            return isPicture;
        }
        public ImageView getMyImageView() {
            return myImageView;
        }

        public EditText getMyEditText() {
            return myEditText;
        }

        public String getFileName() {
            return fileName;
        }

    }



    private abstract class MyOnClickListener implements View.OnClickListener{
        private String fileName;

        public MyOnClickListener(String fileName){
            this.fileName = fileName;
        }
    }

}
