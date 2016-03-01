package innatemobile.storymakerevents.Utils;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by rphovley on 1/25/2016. (Not really, AndroidHive tutorial http://www.androidhive.info/2014/05/android-working-with-volley-library-1/)
 */
public class AppController extends Application {
    public static final String TAG = AppController.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public static boolean checkDatabaseForContent(Context c){
        DatabaseHandler dh = new DatabaseHandler(c);
        if(dh.getAllSchedule() == null || dh.getAllSchedule().size() == 0){
            return false;
        }
        if(dh.getAllPresentations() == null || dh.getAllPresentations().size() == 0){
            return false;
        }
        if(dh.getAllBreakouts() == null || dh.getAllBreakouts().size() == 0){
            return false;
        }
        if(dh.getAllSpeakers() == null || dh.getAllSpeakers().size() == 0){
            return false;
        }

        return true;
    }

}
