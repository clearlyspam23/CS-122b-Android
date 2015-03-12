package cs122b.moviequiz;

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

    public void show(MainActivity activity){
        this.activity = activity;
        onShow();
    }

    protected abstract void onShow();

    public void hide(MainActivity activity){
        onHide();
    }

    protected abstract void onHide();
}
