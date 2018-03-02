package octavianionel.it.obtainlocationpermissionsandtransformlocationtoaddress;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by o.ionel on 08/02/2018.
 */

public class InternalMemManager {

    private Context myContext;
    private SharedPreferences sharedPref;

    private final static String keyIsFirstTime = "keyIsFirstTime";
    private final static String keyAllowedPermission = "keyAllowedPermission";

    public InternalMemManager(Context myContext) {
        this.myContext = myContext;
        sharedPref = myContext.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
    }

    public void firstTimeAskingPermission(boolean isFirstTime){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(keyIsFirstTime, isFirstTime);
        editor.apply();
    }

    public boolean isFirstTimeAskingPermission(){
        return sharedPref.getBoolean(keyIsFirstTime, true);
    }

}
