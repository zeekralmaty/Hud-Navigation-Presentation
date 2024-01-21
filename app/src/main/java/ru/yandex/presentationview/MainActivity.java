package ru.yandex.presentationview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.view.Display;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        displayManager =  (DisplayManager)getSystemService(Context.DISPLAY_SERVICE);
        if (displayManager!= null){
            presentationDisplays = displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
            if (presentationDisplays.length > 0){
                HudPresentation secondaryDisplay = new HudPresentation(MainActivity.this, presentationDisplays[0]);
                secondaryDisplay.show();
            }
        }*/

        Intent myintent = new Intent(MainActivity.this, HudPresentation.class);
        MainActivity.this.startActivity(myintent);
    }


}