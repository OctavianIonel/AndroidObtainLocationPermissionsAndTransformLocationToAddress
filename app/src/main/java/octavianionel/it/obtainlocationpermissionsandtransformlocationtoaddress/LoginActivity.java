package octavianionel.it.obtainlocationpermissionsandtransformlocationtoaddress;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.List;

/**
 * Created by Reply on 02/03/2018.
 */

public class LoginActivity extends AppCompatActivity {



    private static final String TAG = LoginActivity.class.getSimpleName();
    Button loginSub;
    EditText et_Email;
    EditText et_Pass;
    private Dialog mDialog;
    String emaidId;
    String password;

    String locationAddress;
    GPSTracker gps;

    private GoogleApiClient googleApiClient;
    final static int REQUEST_LOCATION = 199;

    private TextView mTextView;
    private LocationManager mLocationManager;
    private UnregisterFromLocationListener mUnregisterFromLocationListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTextView = (TextView) findViewById(R.id.tv);

        callLocation();

        // Todo Turn On GPS Automatically

        if(!hasGPSDevice(LoginActivity.this)){
            Toast.makeText(LoginActivity.this,"Gps not Supported", Toast.LENGTH_SHORT).show();
        }

        mLocationManager = (LocationManager) LoginActivity.this.getSystemService(Context.LOCATION_SERVICE);

        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(LoginActivity.this)) {
            Log.i(TAG,"Gps already enabled");
//            Toast.makeText(getActivity(),"Gps not enabled",Toast.LENGTH_SHORT).show();
            enableLoc();
        }else{
            Log.i(TAG,"Gps already enabled");
//            Toast.makeText(getActivity(),"Gps already enabled",Toast.LENGTH_SHORT).show();
        }

    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    private void enableLoc() {
        // TODO Add this Dependency
        // compile 'com.google.android.gms:play-services-location:7.+'

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {
                            Log.d(TAG,"Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();

                    Log.i(TAG,"status Called  -->" + status.getStatusCode());

                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.i(TAG,"LocationSettingsStatusCodes.RESOLUTION_REQUIRED Called ....");
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(LoginActivity.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }


    private void callLocation() {

        Log.i(TAG, "callLocation Called ... ");

        gps = new GPSTracker(LoginActivity.this);
        mUnregisterFromLocationListener = (UnregisterFromLocationListener) gps;

        // check if GPS enabled
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();



            Log.i("MainActivity", "latitude -> " + latitude);
            Log.i("MainActivity", "longitude -> " + longitude);

            GeocoderHandler geocoderHandler = new GeocoderHandler();
            LocationAddress locationAddress = new LocationAddress();
            locationAddress.getAddressFromLocation(latitude, longitude,
                    LoginActivity.this, geocoderHandler);

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings


//            gps.showSettingsAlert();
            enableLoc();
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
//            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    if (locationAddress.contains("Unable")) {
                        callLocation();
                    } else {
                        mTextView.setText(locationAddress);
                        if (mUnregisterFromLocationListener != null) {
                            mUnregisterFromLocationListener.unregisterListener();
                        }
                    }
                    break;
                default:
                    locationAddress = null;
            }

//            LoginPreferences.getActiveInstance(LoginActivity.this).setLocationAddress(locationAddress);

            Log.i(TAG, "addrees  in Main Activity -> " + locationAddress);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        enableLoc();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult()", Integer.toString(resultCode));

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode)
        {
            case REQUEST_LOCATION:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    {
                        // All required changes were successfully made
                        Toast.makeText(LoginActivity.this, "Location enabled by user!", Toast.LENGTH_LONG).show();
                        callLocation();
                        break;
                    }
                    case Activity.RESULT_CANCELED:
                    {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(LoginActivity.this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();

                        finish();

                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
                break;
        }
    }


    public interface UnregisterFromLocationListener {
        void unregisterListener();
    }


}
