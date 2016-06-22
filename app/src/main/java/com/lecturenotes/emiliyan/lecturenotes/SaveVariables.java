package com.lecturenotes.emiliyan.lecturenotes;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Emiliyan on 2/29/2016.
 */
public class SaveVariables {
    /*file names*/
    private final static String FILE_NAME_SAVED_MODULES = "saved_modules.txt";//save PIN_LOCKED state
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy_mm_dd_hhmmss");
    private static final int REQUEST_TAKE_PHOTO = 1;





    protected static boolean saveImage(Activity activity,Bitmap bitmapImage, String fileName){
        FileOutputStream fos = null;
        try {
            fos = activity.openFileOutput(fileName, Context.MODE_PRIVATE);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            System.out.println("Drawing should now be saved fileName: "+ fileName);
            fos.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    //use on separate thread AsyncTask
    protected static Bitmap loadImage(Activity activity, String fileName)
    {
        Bitmap b = null;
        try {
            FileInputStream fis = activity.openFileInput(fileName);
            b= BitmapFactory.decodeStream(fis);
            fis.close();
            return b;
        }
        catch (Exception e)
        {System.out.println(" problem loading the image ");
            e.printStackTrace();
        }
        return null;
    }


    protected static boolean deleteFile(Activity activity, String fileName){
        File file = new File(activity.getFilesDir(), fileName);
        System.out.println("file was deleted");
        return file.delete();
    }





    protected static ArrayList<Module> getSavedModules(Activity activity){
        ArrayList<Module> modules = null;
        try{




            ObjectInputStream ois = new ObjectInputStream(activity.openFileInput(FILE_NAME_SAVED_MODULES));//open input stream
            modules = (ArrayList<Module>) ois.readObject();//get saved modules

            //close stream
            ois.close();
        } catch (Exception e) {
            //File not found
            Log.d("save module >>",e.getMessage());
        }
        return modules;
    }

    protected static boolean saveModule(Activity activity,String moduleName){
        ArrayList<Module> modules=  getSavedModules(activity);//get saved modules

        if(modules!=null){
            for(Module m : modules){//check if module with this name was saved previously
                Log.d("module: ",m.getModuleName());
                if( m.getModuleName().equalsIgnoreCase(moduleName)) return false; //if yes then return false;
            }
        }else{
            modules = new ArrayList<Module>();
        }

        Module myModule = new Module(moduleName);//create new module
        modules.add(myModule);//add it to the module list

        if(saveModules(activity, modules)) return true;
        else return false;
    }




    protected static boolean addNote(Activity activity, Module module,Note note){
        ArrayList<Module> modules=  getSavedModules(activity);//get saved modules
        if(modules!=null){
            boolean moduleFound= false;
            for(int i=0; i<modules.size();i++){

                if(modules.get(i).getModuleName().equals(module.getModuleName())){//if module exists
                    moduleFound = true;
                    modules.get(i).addNote(note);//add note
                    break;
                }
            }
            if(moduleFound){
                saveModules(activity, modules);//save modules if module was updated
                return true;
            }
        }
        return false;
    }

    protected static boolean updateNote(Activity activity, String module,long noteName, ArrayList<NoteElement> stringNotes){
        ArrayList<Module> modules=  getSavedModules(activity);//get saved modules
        if(modules!=null){
            boolean noteFound= false;

            for(int i=0; i<modules.size();i++){
               if(modules.get(i).getModuleName().equals(module)){//if module exists

                    Module m = modules.get(i);//add note
                    ArrayList<Note> notes = m.getNotes();
                    for(int j=0;j<notes.size();j++){
                        if(notes.get(j).getNoteName()==noteName){
                            Note n = notes.get(j);
                            n.setStrings(stringNotes);
                            noteFound = true;
                            break;
                        }
                    }
                }
            }
            if(noteFound){
                saveModules(activity, modules);//save modules if module was updated
                return true;
            }
        }
        return false;
    }


    protected static Note getNote(Activity activity,long noteName, String moduleName){
        ArrayList<Module> modules=  getSavedModules(activity);//get saved modules

        if(modules!=null){
            for(Module m: modules){
                if(m.getModuleName().equals(moduleName)) {
                    ArrayList<Note> notes = m.getNotes();
                    for(Note n: notes){
                        if(n.getNoteName()==noteName) return n;
                    }
                }
            }
        }

        return null;
    }

    protected static Module getModule(Activity activity,String name){
        ArrayList<Module> modules=  getSavedModules(activity);//get saved modules
        boolean checkModules = modules==null;

        if(modules!=null){
            for(Module m: modules){


                if(m.getModuleName().equals(name)) {
                    return m;
                }
            }
        }

        return null;
    }


    private static boolean saveModules(Activity activity,ArrayList<Module> modules){
        try{

            FileOutputStream fos = activity.openFileOutput(FILE_NAME_SAVED_MODULES, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);

            out.writeObject(modules);
            out.flush();
            fos.close();
            }catch(Exception e) {
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }



}
