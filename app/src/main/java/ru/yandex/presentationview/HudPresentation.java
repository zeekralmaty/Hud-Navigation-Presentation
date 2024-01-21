package ru.yandex.presentationview;

import android.app.Presentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.media.MediaRouter;
import android.os.Bundle;
import android.view.Display;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.directions.driving.DrivingRouterType;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.navigation.automotive.Navigation;
import com.yandex.mapkit.navigation.automotive.NavigationFactory;
import com.yandex.mapkit.navigation.automotive.layer.NavigationLayer;
import com.yandex.mapkit.navigation.automotive.layer.NavigationLayerFactory;
import com.yandex.mapkit.navigation.automotive.layer.styling.AutomotiveNavigationStyleProvider;
import com.yandex.mapkit.navigation.automotive.layer.styling.BalloonImageProvider;
import com.yandex.mapkit.navigation.automotive.layer.styling.NavigationStyleProvider;
import com.yandex.mapkit.navigation.automotive.layer.styling.RequestPointStyleProvider;
import com.yandex.mapkit.navigation.automotive.layer.styling.RoutePinsStyleProvider;
import com.yandex.mapkit.navigation.automotive.layer.styling.RouteViewStyleProvider;
import com.yandex.mapkit.navigation.automotive.layer.styling.UserPlacemarkStyleProvider;
import com.yandex.mapkit.navigation.guidance_camera.CameraMode;
import com.yandex.mapkit.road_events_layer.HighlightCircleStyle;
import com.yandex.mapkit.road_events_layer.HighlightMode;
import com.yandex.mapkit.road_events_layer.RoadEventStyle;
import com.yandex.mapkit.road_events_layer.RoadEventStylingProperties;
import com.yandex.mapkit.road_events_layer.RoadEventsLayer;
import com.yandex.mapkit.road_events_layer.StyleProvider;
import com.yandex.mapkit.styling.PlacemarkStyle;
import com.yandex.mapkit.styling.internal.PlacemarkStyleBinding;
import com.yandex.runtime.image.AnimatedImageProvider;
import com.yandex.runtime.image.ImageProvider;

import java.util.List;

public class HudPresentation extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;
    private MapView mapView;
    private NavigationLayer navigationLayer;
    private RoadEventsLayer roadEventsLayer;
    //private NavigationStyleProvider navigationStyleProvider = new NavigationStyleProviderImpl(this);
    private StyleProvider styleProvider;
    private Presentation presentation;
    private Navigation navigation;
    private AutomotiveNavigationStyleProvider navigationStyleProvider;
    private final String MAPKIT_API_KEY = ""; //ENTER YOUR OWN API KEY

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setLocale("ru_RU");
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);


        super.onCreate(savedInstanceState);
        requestLocationPermission();
        MediaRouter mediaRouter = (MediaRouter) getSystemService(Context.MEDIA_ROUTER_SERVICE);
        MediaRouter.RouteInfo route = mediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        if (route != null) {
            Display presentationDisplay = route.getPresentationDisplay();
            presentation = new Presentation(this,presentationDisplay);
        }
        presentation.setContentView(R.layout.presentation_activity);
        presentation.show();
        mapView = (MapView) presentation.findViewById(R.id.mapview);
        String style = "[" +
                "        {" +

                "            \"tags\": {" +
                "                \"any\": [" +
                "                    \"poi\"," +
                "                    \"structure\"," +
                "                    \"landscape\"," +
                "                    \"transit\"," +
                "                    \"path\"," +
                "                    \"admin\"" +
                "                ]" +
                "            }," +
                "            \"stylers\": {" +
                "                \"visibility\": \"off\"" +
                "            }" +
                "        }" +
                "    ]";
        mapView.getMapWindow().getMap().setMapStyle(style);
        mapView.getMapWindow().getMap().setNightModeEnabled(true);
        MapKit mapKit = MapKitFactory.getInstance();
        mapKit.resetLocationManagerToDefault();
        navigation = NavigationFactory.createNavigation(DrivingRouterType.COMBINED);
        styleProvider = new StyleProvider() {
            @Override
            public boolean provideStyle(@NonNull RoadEventStylingProperties roadEventStylingProperties, boolean b, float v, @NonNull RoadEventStyle roadEventStyle) {
                return false;
            }

            @Nullable
            @Override
            public HighlightCircleStyle provideHighlightCircleStyle(boolean b, @NonNull HighlightMode highlightMode) {
                return null;
            }
        };
        roadEventsLayer = mapKit.createRouteRoadEventsLayer(mapView.getMapWindow(), styleProvider);
        navigationStyleProvider = new AutomotiveNavigationStyleProvider(this);
        navigationLayer = NavigationLayerFactory.createNavigationLayer(mapView.getMapWindow(), roadEventsLayer, navigationStyleProvider, navigation);
        navigationLayer.getCamera().setCameraMode(CameraMode.FOLLOWING,null);
        navigationLayer.getCamera().setFollowingModeZoomOffset(2,null);
        navigationLayer.getNavigation().resume();
        navigationLayer.getNavigation().startGuidance(null);
        navigationLayer.refreshStyle();
    }

     @Override
    protected void onStop() {
        navigation.resume();
        navigationLayer.getNavigation().resume();
        super.onStop();

    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
        startService();
        navigation.resume();
        navigationLayer.getNavigation().resume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        this.presentation=null;
        stopService();
    }
    @Override
    protected void onResume(){
        super.onResume();
        navigation.resume();
        navigationLayer.getNavigation().resume();
    }
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                "android.permission.ACCESS_FINE_LOCATION")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{"android.permission.ACCESS_FINE_LOCATION"},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }
    public void startService() {
        Intent serviceIntent = new Intent(this, NavigationService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        ContextCompat.startForegroundService(this, serviceIntent);
    }
    public void stopService() {
        Intent serviceIntent = new Intent(this, NavigationService.class);
        stopService(serviceIntent);
    }



}