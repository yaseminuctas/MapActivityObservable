package com.example.mapview;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.observers.DisposableObserver;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    ArrayList<Marker> markers = new ArrayList<>();

    private final static int REQUEST_lOCATION = 90;
    EditText edtLat, edtLng;
    Button button;
    String lat, lng;
    Double latitude, longitude;
    Observable<Boolean> observable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_maps );


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );


        edtLng = (EditText) findViewById( R.id.edt_lng );
        edtLat = (EditText) findViewById( R.id.edt_lat );
        button = (Button) findViewById( R.id.btn_ekle );


        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lat = edtLat.getText().toString();
                latitude = Double.valueOf( lat ).doubleValue();

                lng = edtLng.getText().toString();
                longitude = Double.valueOf( lng ).doubleValue();

                LatLng latLng = new LatLng( latitude, longitude );

                if (markers.size() > 0) {

                    mMap.addPolyline(
                            new PolylineOptions()
                                    .add( markers.get( markers.size() - 1 ).getPosition(), latLng )
                                    .width( 8f )
                                    .color( Color.RED ) );

                }

                Marker marker = mMap.addMarker( new MarkerOptions().position( new LatLng( latitude, longitude ) ) );
                markers.add( marker );
                mMap.animateCamera( CameraUpdateFactory.newLatLng( latLng ) );
            }


        } );


        Observable<String> edtLngObservable = RxTextView.textChanges( edtLng ).map( new io.reactivex.functions.Function<CharSequence, String>() {
            @Override
            public String apply(CharSequence charSequence) throws Exception {
                return charSequence.toString();
            }
        } );


        Observable<String> edtLatObservable = RxTextView.textChanges( edtLat ).map( new io.reactivex.functions.Function<CharSequence, String>() {
            @Override
            public String apply(CharSequence charSequence) throws Exception {
                return charSequence.toString();
            }
        } );


        observable = Observable.combineLatest( edtLngObservable, edtLatObservable, new BiFunction<String, String, Boolean>() {
            @Override
            public Boolean apply(String s, String s2) throws Exception {
                return isValidForm( s, s2 );
            }
        } );


        observable.subscribe( new DisposableObserver<Boolean>() {


            @Override
            public void onNext(Boolean aBoolean) {
                updateButton( aBoolean );
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        } );
    }


    public void updateButton(boolean valid) {

        button.setEnabled( valid );
    }

    public boolean isValidForm(String lat, String lng) {
        boolean validLat = !lat.isEmpty();
        boolean validLng = !lng.isEmpty();

        if (!validLat) {
            edtLat.setError( "Please Enter Your 'LAT' value " );
        }

        if (!validLng) {
            edtLng.setError( "Please Enter Your 'LNG' value " );
        }

        return validLat && validLng;
    }


    private boolean ShouldAddMarker(double latA, double lngA, double latB, double lngB) {


        Location locationA = new Location( "point A" );

        locationA.setLatitude( latA );
        locationA.setLongitude( lngA );

        Location locationB = new Location( "point B" );

        locationB.setLatitude( latB );
        locationB.setLongitude( lngB );


        float distance = locationA.distanceTo( locationB );
        return distance > 1000;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            mMap.setMyLocationEnabled( true );
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions( new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_lOCATION );
            }

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );


    }
}
