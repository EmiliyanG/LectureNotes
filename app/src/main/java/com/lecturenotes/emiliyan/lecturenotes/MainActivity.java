package com.lecturenotes.emiliyan.lecturenotes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    public static final String MODULE_NAME_FOR_NOTE_EXTRAS = "noteModule";
    public static final String NOTE_NAME_EXTRAS= "noteName";
    private static final int WARNING_DURATION = 3000;//miliseconds
    private static final int INDIVIDUAL_MENU_DURATION = 5000;//miliseconds


    private LinearLayout addModuleLayout;
    private ImageButton addModuleBtn;
    private ImageButton addNoteBtn;
    private TextView title;
    private LinearLayout modulesContainer;
    private LinearLayout searchBarContainer;
    private EditText addModuleEditText;
    private TextView warningAddModule;
    private TextView noModulesNotesHint;
    private Activity myActivity = this;
    private ArrayList<Module> modules;
    private LinearLayout notesContainer;
    private ScrollView modulesScrollView;
    private ScrollView notesScrollView;
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private ImageButton backButton;


    private Typeface typeHighTower;
    private Typeface typeKristen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linkUIElements();

        displayModules();
    }

    private void linkUIElements() {
        addModuleLayout = (LinearLayout) findViewById(R.id.addModuleLayout);
        addModuleBtn = (ImageButton) findViewById(R.id.addModuleBtn);
        modulesContainer = (LinearLayout) findViewById(R.id.modulesContainer);
        addModuleEditText = (EditText) findViewById(R.id.addModuleEditText);
        Button addBtn = (Button) findViewById(R.id.addBtn);
        Button cancelBtn = (Button) findViewById(R.id.cancelBtn);
        searchBarContainer = (LinearLayout) findViewById(R.id.searchBarContainer);
        warningAddModule = (TextView) findViewById(R.id.warningAddModule);
        noModulesNotesHint = (TextView) findViewById(R.id.noModulesNotesHint);
        addNoteBtn = (ImageButton) findViewById(R.id.addNoteBtn);
        notesContainer = (LinearLayout) findViewById(R.id.notesContainer);
        modulesScrollView = (ScrollView) findViewById(R.id.modulesScrollView);
        notesScrollView = (ScrollView) findViewById(R.id.notesScrollView);
        title = (TextView) findViewById(R.id.title);
        backButton = (ImageButton) findViewById(R.id.backButton);
        ImageButton searchIconBtn = (ImageButton) findViewById(R.id.searchIconBtn);
        typeHighTower = Typeface.createFromAsset(getAssets(), "fonts/high_tower_text_italic.ttf");
        typeKristen = Typeface.createFromAsset(getAssets(), "fonts/kristen_itc_regular.TTF");

        title.setTypeface(typeKristen);

        searchIconBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchBarContainer.getVisibility() == View.GONE)
                    searchBarContainer.setVisibility(View.VISIBLE);
                else searchBarContainer.setVisibility(View.GONE);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleNoteModuleScrollViews();
                toggleNoteModuleBtns();
                title.setText(getResources().getString(R.string.title_modules));
                backButton.setVisibility(View.GONE);

            }
        });
        //add listeners
        addModuleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addModuleLayout.setVisibility(View.VISIBLE);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeAddModuleLayout();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String moduleName = addModuleEditText.getText().toString();
                boolean wasSaved = false;

                if (Validator.validateModuleName(moduleName)) {//module name is valid
                    wasSaved = SaveVariables.saveModule(myActivity, moduleName);//try to save module
                    if (!wasSaved) {
                        showWarning(getResources().getString(R.string.warning_add_module_existing_name));
                    }
                } else {
                    showWarning(getResources().getString(R.string.warning_add_module_wrong_name));
                }
                if (wasSaved) {
                    displayModules();
                    closeAddModuleLayout();
                }
            }
        });

        addNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Module m = SaveVariables.getModule(myActivity, title.getText().toString());
                addNoteForModule(m);

            }
        });


    }


    public void startDrawingActivity(long noteName, String moduleName){
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra(NOTE_NAME_EXTRAS, noteName);
        intent.putExtra(MODULE_NAME_FOR_NOTE_EXTRAS, moduleName);
        startActivity(intent);
    }
    private void displayModules() {
        modules = SaveVariables.getSavedModules(this);
        noModulesNotesHint.setVisibility(View.GONE);


        modulesContainer.removeAllViews();
        if (modules != null) {
            for (Module m : modules) {
                modulesContainer.addView(generateModuleLayout(m));

            }
        } else {
            noModulesNotesHint.setVisibility(View.VISIBLE);
            noModulesNotesHint.setText(getResources().getString(R.string.no_modules_hint));
        }
    }


    private void displayNotesForModule(Module m) {
        modules = SaveVariables.getSavedModules(this);//update modules
        noModulesNotesHint.setVisibility(View.GONE);

        notesContainer.removeAllViews();
        if (m != null) {
            for (Note n : m.getNotes()) {
                notesContainer.addView(generateNoteLayout(n));

            }
        } else {
            noModulesNotesHint.setVisibility(View.VISIBLE);
            noModulesNotesHint.setText(getResources().getString(R.string.no_notes_hint));
        }


    }

    private Module addNoteForModule(Module m) {

        long currentTime = new Date().getTime();
        Note n = new Note(currentTime,m.getModuleName());

        String noteName = sdf.format(currentTime);


        SaveVariables.addNote(myActivity, m, n);//save note to the internal memory
        if (m != null) m.addNote(n);
        displayNotesForModule(m);
        return m;
    }

    private FrameLayout generateModuleLayout(Module m) {

        return generateTextView(m, null, true);
    }

    private FrameLayout generateNoteLayout(Note n) {
        return generateTextView(null, n, false);
    }

    /*
    * do not use this method directly
    * use generateModuleLayout(Module m) or generateNoteLayout(Note n), instead
    * isModule = true > generate layout for module
    * isModule = false > generate layout for note
    *
    * returns FrameLayout with design and full button functionality
    * */
    private FrameLayout generateTextView(Module m, Note n, final boolean isModule) {

        FrameLayout frameLayout = new FrameLayout(this);
        TextView textView = new TextView(this);
        LinearLayout linearLayout = new LinearLayout(this);
        Button rename = new Button(this);

        textView.setTypeface(typeKristen);

        //link components
        frameLayout.addView(textView);
        frameLayout.addView(linearLayout);
        rename.setBackgroundColor(ContextCompat.getColor(this, R.color.individualMenuBtnLight));

        String moduleName = "";
        long noteName = 0l;
        if (isModule) {//add button only if module layout is generated
            moduleName = m.getModuleName();

            Button addNote = new Button(this);
            addNote.setBackgroundColor(ContextCompat.getColor(this, R.color.individualMenuBtnLight));
            rename.setBackgroundColor(ContextCompat.getColor(this, R.color.individualMenuBtnDark));
            linearLayout.addView(addNote);
            addNote.setText(getResources().getString(R.string.add_note));

            LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            addNote.setPadding(6, 6, 6, 6);
            addNote.setLayoutParams(lllp);


            addNote.setOnClickListener(new MyOnClickListener(moduleName) {

                @Override
                public void onClick(View v) {

                    Module m = SaveVariables.getModule(myActivity, getLabel());
                    m = addNoteForModule(m);
                    showNotes(m);
                }
            });
        }
        linearLayout.addView(rename);

        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        frameLayout.setLayoutParams(flp);

        // set properties for textView
        if (isModule) textView.setText(m.getModuleName());
        else {
            noteName = n.getNoteName();
            textView.setText(sdf.format(n.getNoteName()));
        }


        textView.setTextSize(30);
        textView.setTextColor(ContextCompat.getColor(this, R.color.customDarkest));
        textView.setBackgroundColor(ContextCompat.getColor(this, R.color.customLight));
        textView.setPadding(15, 50, 15, 50);
        flp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        flp.setMargins(6, 6, 6, 6); // left, top, right, bottom
        flp.gravity = Gravity.START;
        textView.setLayoutParams(flp);

        //set properties for linearLayout
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        flp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);

        flp.gravity = Gravity.END;
        linearLayout.setLayoutParams(flp);
        linearLayout.setGravity(Gravity.RIGHT);
        linearLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.customDark));
        linearLayout.setVisibility(View.GONE);

        //set properties for buttons
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);

        //set properties for cancel button

        rename.setLayoutParams(llp);
        rename.setText("rename");
        rename.setPadding(6, 6, 6, 6);

        if(!isModule) moduleName = n.getNoteModule();
        //add listeners
        frameLayout.setOnTouchListener(new OnSwipeTouchListener(this, linearLayout, moduleName, noteName) {
            private CountDownTimer cdt;

            @Override
            public void onSwipeLeft() {

                this.linearLayout.setVisibility(View.VISIBLE);
                cdt = new CountDownTimer(INDIVIDUAL_MENU_DURATION, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        linearLayout.setVisibility(View.GONE);
                    }
                }.start();
            }

            public void onSwipeRight() {
                if (cdt != null) {
                    cdt.cancel();
                }
                this.linearLayout.setVisibility(View.GONE);
            }

            public void onClick() {
                if (isModule) {
                    Module m = SaveVariables.getModule(myActivity, moduleName);
                    showNotes(m);
                } else {

                    startDrawingActivity(noteName, moduleName);
                }

            }

        });

        return frameLayout;
    }

    /*
    * Hide Module Button
    * Hide Module scroll view
    * update title with the module name
    * display notes associated with the module
    * */
    private void showNotes(Module m) {
        title.setText(m.getModuleName());
        toggleNoteModuleBtns();
        toggleNoteModuleScrollViews();
        displayNotesForModule(m);
        backButton.setVisibility(View.VISIBLE);
    }


    private void toggleNoteModuleBtns() {

        if (addNoteBtn.getVisibility() == View.VISIBLE) {
            addNoteBtn.setVisibility(View.GONE);
            addModuleBtn.setVisibility(View.VISIBLE);
        } else {
            addNoteBtn.setVisibility(View.VISIBLE);
            addModuleBtn.setVisibility(View.GONE);
        }

    }

    private void toggleNoteModuleScrollViews() {

        if (notesScrollView.getVisibility() == View.VISIBLE) {
            notesScrollView.setVisibility(View.GONE);
            modulesScrollView.setVisibility(View.VISIBLE);
        } else {
            notesScrollView.setVisibility(View.VISIBLE);
            modulesScrollView.setVisibility(View.GONE);
        }

    }


    private void closeAddModuleLayout() {
        addModuleEditText.setText("");
        addModuleEditText.clearFocus();
        addModuleLayout.setVisibility(View.GONE);
        //hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(addModuleEditText.getApplicationWindowToken(), 0);
    }

    private void showWarning(String message) {
        warningAddModule.setText(message);
        warningAddModule.setVisibility(View.VISIBLE);
        new CountDownTimer(WARNING_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                warningAddModule.setVisibility(View.GONE);
            }
        }.start();
    }


    /**
     * Detects left and right swipes across a view.
     *
     * @author: Edward Brey
     * Date: Oct 21 '13 at 22:33
     * Open source code url: http://stackoverflow.com/questions/4139288/android-how-to-handle-right-to-left-swipe-gestures
     * date url was accessed: 29 February 2016
     */

    public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;
        public LinearLayout linearLayout;
        public String moduleName;
        public long noteName;

        public OnSwipeTouchListener(Context context, LinearLayout linearLayout, String moduleName, long noteName) {
            gestureDetector = new GestureDetector(context, new GestureListener());
            this.linearLayout = linearLayout;
            this.moduleName = moduleName;
            this.noteName = noteName;
        }

        public void onSwipeLeft() {
        }

        public void onSwipeRight() {
        }

        public void onClick() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
            //return false to allow onClickListener to listen to the event
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_DISTANCE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            public boolean onSingleTapConfirmed(MotionEvent e) {
                onClick();
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                float distanceX = e2.getX() - e1.getX();
                float distanceY = e2.getY() - e1.getY();
                if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (distanceX > 0)
                        onSwipeRight();
                    else
                        onSwipeLeft();
                    return true;
                }
                return false;
            }
        }
    }


    public abstract class MyOnClickListener implements View.OnClickListener {
        private String label;

        public MyOnClickListener(String label) {
            this.label = label;
        }

        public String getLabel() {
            return this.label;
        }
    }


}



