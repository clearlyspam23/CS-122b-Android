package cs122b.moviequiz;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by john on 3/12/2015.
 */
public abstract class Controller {

    private int view;
    private MainActivity activity;

    public Controller(int view){
        this.view = view;
    }

    public int getView(){
        return view;
    }

    protected void setView(int view){
        this.view = view;
    }

    protected MainActivity getActivity(){
        return activity;
    }

    public void setActivity(MainActivity activity){
        this.activity = activity;
    }

    public void show(MainActivity activity){
        this.activity = activity;
        activity.setContentView(getView());
        onShow();
    }

    protected abstract void onShow();

    public void hide(MainActivity activity){
        onHide();
    }

    protected abstract void onHide();

    protected Button getButton(int id){
        return getView(id, Button.class);
    }

    protected TextView getTextView(int id){
        return getView(id, TextView.class);
    }

    protected <T> T getView(int id, Class<T> widgetClass){
        return widgetClass.cast(activity.findViewById(id));
    }

    public void goToController(Controller controller){
        activity.setCurrentController(controller);
    }

    public void back(){
        activity.back();
    }

    public void onPause(){
        onHide();
    }

    public void onResume(){
        onShow();
    }

    public void onRestoreInstanceState(Bundle inState){

    }

    public void onSaveInstanceState(Bundle outState){

    }
}
