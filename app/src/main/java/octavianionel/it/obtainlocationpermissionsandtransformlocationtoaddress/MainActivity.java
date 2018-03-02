package octavianionel.it.obtainlocationpermissionsandtransformlocationtoaddress;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Activity mActivity;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1234;
    private Button btnSubmit;
    private Button checkForPermissionsBtn;
    private InternalMemManager internalMemManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;

        checkForPermissionsBtn = (Button) findViewById(R.id.checkForPermissionsBtn);
        btnSubmit =(Button) findViewById(R.id.btn_submit);

        internalMemManager = new InternalMemManager(mActivity);

        int permissionStatus = getPermissionStatus(mActivity, Manifest.permission.ACCESS_FINE_LOCATION);


        checkForPermissionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String permission = Manifest.permission.ACCESS_FINE_LOCATION;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int permissionResult = ActivityCompat.checkSelfPermission(mActivity, permission);

                    if (permissionResult != PackageManager.PERMISSION_GRANTED) {
                        if (mActivity.shouldShowRequestPermissionRationale(permission)) {
                            // Show an expanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                        } else {
                            if (internalMemManager.isFirstTimeAskingPermission()) {
                                internalMemManager.firstTimeAskingPermission(false);
                                // No explanation needed, we can request the permission.
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                            } else {
                                //Permission disable by device policy or user denied permanently. Show proper error message
                                Toast.makeText(mActivity, "User clicked on never ask again", Toast.LENGTH_SHORT).show();
                                showSettingsAlert();
                            }
                        }
                    }

                }  else {
                    Log.i(TAG, "Lower Than MarshMallow");
                }
            }
        });



        if (permissionStatus == GRANTED) {
            btnSubmit.setVisibility(View.VISIBLE);
        } else {
            btnSubmit.setVisibility(View.GONE);
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });

    }


    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);

        // Setting Dialog Title
        alertDialog.setTitle("Il permesso non e' stato accettato");

        // Setting Dialog Message
        alertDialog.setMessage("Per continuare devi abilitare il permesso di geolocalizzazione");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                mActivity.startActivityForResult(intent, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getPermissionStatus(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == GRANTED) {
            btnSubmit.setVisibility(View.VISIBLE);
        } else {
            btnSubmit.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (getPermissionStatus(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == GRANTED) {
                btnSubmit.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    btnSubmit.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(this, "Permission was denied, but I need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({GRANTED, DENIED, BLOCKED_OR_NEVER_ASKED })
    public @interface PermissionStatus {}

    public static final int GRANTED = 0;
    public static final int DENIED = 1;
    public static final int BLOCKED_OR_NEVER_ASKED = 2;

    @PermissionStatus
    public static int getPermissionStatus(Activity activity, String androidPermissionName) {
        if(ContextCompat.checkSelfPermission(activity, androidPermissionName) != PackageManager.PERMISSION_GRANTED) {
            if(!ActivityCompat.shouldShowRequestPermissionRationale(activity, androidPermissionName)){
                return BLOCKED_OR_NEVER_ASKED;
            }
            return DENIED;
        }
        return GRANTED;
    }
}
