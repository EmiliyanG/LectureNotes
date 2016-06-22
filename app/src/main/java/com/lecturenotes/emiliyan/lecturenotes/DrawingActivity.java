package com.lecturenotes.emiliyan.lecturenotes;




import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.Date;


public class DrawingActivity extends AppCompatActivity {
    public static final int STROKE_WIDTH = 12;
    public static final int ERASER_WIDTH = 120;

    private Activity myActivity = this;

    private long noteName=0l;
    private String moduleName;
    private String fileName;


//    /*hide keyboard*/
//    ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
//            .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

    private Button drawingSave;
    private Button drawingCancel;
    private DrawingView drawingView;
    private LinearLayout colorWindow;
    private LinearLayout colorPickerLayout;
    private ImageButton colorPickerImageBtn;
    private ImageButton eraser;
    private ImageButton pencil;
    private ScrollView scrollView;
    private int r=0,g=0,b=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drawing);

        //get note title
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            noteName = extras.getLong(MainActivity.NOTE_NAME_EXTRAS);
            moduleName = extras.getString(MainActivity.MODULE_NAME_FOR_NOTE_EXTRAS);
            fileName = extras.getString(NoteActivity.IMAGE_FILE_NAME);
        }

        linkComponents();


        if(fileName!= null){
            loadImage(fileName,drawingView);
        }


    }


    private void linkComponents(){
        SeekBar colorSeekBarR = (SeekBar) findViewById(R.id.colorSeekBarR);
        SeekBar colorSeekBarG = (SeekBar) findViewById(R.id.colorSeekBarG);
        SeekBar colorSeekBarB = (SeekBar) findViewById(R.id.colorSeekBarB);
        Button colorPickerBtn = (Button) findViewById(R.id.colorPickerBtn);
        drawingView = (DrawingView) findViewById(R.id.drawingView);
        colorWindow = (LinearLayout) findViewById(R.id.color_window);
        colorPickerLayout = (LinearLayout) findViewById(R.id.colorPicker);
        colorPickerImageBtn = (ImageButton) findViewById(R.id.colorPickerImageBtn);
        drawingSave = (Button) findViewById(R.id.drawingSave);
        drawingCancel = (Button) findViewById(R.id.drawingCancel);
        eraser= (ImageButton) findViewById(R.id.eraser);
        pencil = (ImageButton) findViewById(R.id.pencil);




        pencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.setStrokeWidth(STROKE_WIDTH);
                drawingView.eraserSelected(false);
                drawingView.setStrokeColor(Color.rgb(r, g, b));
                pencil.setBackground(ContextCompat.getDrawable(myActivity, R.drawable.pencil_selected));
                eraser.setBackground(ContextCompat.getDrawable(myActivity, R.drawable.eraser));

            }
        });

        eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.setStrokeWidth(ERASER_WIDTH);
                drawingView.setStrokeColor(ContextCompat.getColor(myActivity, R.color.customLight));
                drawingView.eraserSelected(true);
                eraser.setBackground(ContextCompat.getDrawable(myActivity, R.drawable.eraser_selected));
                pencil.setBackground(ContextCompat.getDrawable(myActivity, R.drawable.pencil));

            }
        });

        drawingSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(fileName!= null){
                updateDrawing(fileName);
            }else saveDrawing();
            startNoteActivity();
            finish();
            }
        });

        drawingCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNoteActivity();
                finish();
            }
        });


        colorPickerImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorPickerLayout.setVisibility(View.VISIBLE);
            }
        });

        colorPickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorPickerImageBtn.setBackgroundColor(Color.rgb(r, g, b));
                drawingView.setStrokeColor(Color.rgb(r, g, b));
                colorPickerLayout.setVisibility(View.GONE);
            }
        });

        colorSeekBarR.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                r = progress;
                colorWindow.setBackgroundColor(Color.rgb(r, g, b));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        colorSeekBarG.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                g = progress;
                colorWindow.setBackgroundColor(Color.rgb(r, g, b));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        colorSeekBarB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                b = progress;
                colorWindow.setBackgroundColor(Color.rgb(r, g, b));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    private void loadImage(String fileName, ImageView imageView) {
        LoadImageTask task = new LoadImageTask(this,imageView);
        task.execute(fileName);
    }


    public void startNoteActivity(){
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra(MainActivity.NOTE_NAME_EXTRAS, noteName);
        intent.putExtra(MainActivity.MODULE_NAME_FOR_NOTE_EXTRAS, moduleName);
        startActivity(intent);
    }

    private void updateDrawing(String fileName){
        Bitmap myImage = drawingView.saveDrawing();//get image Bitmap
        boolean fileDeleted = SaveVariables.deleteFile(this, fileName);//delete previous file
        System.out.println("file should be deleted: " + fileDeleted);
        if(fileDeleted) SaveVariables.saveImage(this, myImage, fileName);// save image again with the same name
        //note objects keep only the file name of the picture
        //therefore changing the file should update the picture
    }


    private void saveDrawing(){
        Bitmap myImage = drawingView.saveDrawing();//get image Bitmap
        String fileName = generatePictureFileName();//generate unique fileName

        SaveVariables.saveImage(this, myImage, fileName);// save image
        Note n = SaveVariables.getNote(this,noteName,moduleName);// get current Note object

        ArrayList<NoteElement> strings = n.getStrings();//get the ArrayList of note elements for this Note

        strings.add(new NoteElement(true,fileName));//save the fileName of the image
        SaveVariables.updateNote(this, moduleName, noteName,strings);//update Note
    }

    //to make sure that picture file names are always unique
    //get the noteName and append current time(long)
    private String generatePictureFileName(){
        return "" + noteName+ "" + new Date().getTime();
    }


}
