package ng.dat.ar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.opengl.Matrix;
import android.view.View;


import java.util.ArrayList;
import java.util.List;

import ng.dat.ar.helper.LocationHelper;
import ng.dat.ar.model.ARPoint;

/**
 * Created by ntdat on 1/13/17.
 */

public class AROverlayView extends View {

    Context context;
    private float[] rotatedProjectionMatrix = new float[16];
    private Location currentLocation;
    private List<ARPoint> arPoints;
    private ARPoint arPoint;


    public AROverlayView(Context context) {
        super(context);

        this.context = context;

        //Demo points
//        arPoints = new ArrayList<ARPoint>() {{
//            add(new ARPoint("Point1", 38.93067783, -77.24157307, 98));
//            add(new ARPoint("Point2", 38.93067783, -77.24157307, 78));
//        }};
    }

    public void updateRotatedProjectionMatrix(float[] rotatedProjectionMatrix) {
        this.rotatedProjectionMatrix = rotatedProjectionMatrix;
        this.invalidate();
    }

    public void updateCurrentLocation(Location currentLocation){
        this.currentLocation = currentLocation;
        this.invalidate();
        // 1 meter offset test, the offset is an estimation at latitude 40 degree earth surface
        // One degree of latitude = 111.03 km
        // One degree of longitude = 85.39 km
        double lat = currentLocation.getLatitude() + 1.0/111030;
        double lon = currentLocation.getLongitude() + 1.0/85390;
        double alt = currentLocation.getAltitude();
        arPoint = new ARPoint("Point3", lat,lon,alt);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (currentLocation == null) {
            return;
        }

        final int radius = 30;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(60);
//
//        for (int i = 0; i < arPoints.size(); i ++) {
//            float[] currentLocationInECEF = LocationHelper.WSG84toECEF(currentLocation);
//            float[] pointInECEF = LocationHelper.WSG84toECEF(arPoints.get(i).getLocation());
//            float[] pointInENU = LocationHelper.ECEFtoENU(currentLocation, currentLocationInECEF, pointInECEF);
//
//            float[] cameraCoordinateVector = new float[4];
//            Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedProjectionMatrix, 0, pointInENU, 0);
//
//            // cameraCoordinateVector[2] is z, that always less than 0 to display on right position
//            // if z > 0, the point will display on the opposite
//            if (cameraCoordinateVector[2] < 0) {
//                float x = (0.5f + cameraCoordinateVector[0] / cameraCoordinateVector[3]) * canvas.getWidth();
//                float y = (0.5f - cameraCoordinateVector[1] / cameraCoordinateVector[3]) * canvas.getHeight();
//
//                canvas.drawCircle(x, y, radius, paint);
//                canvas.drawText(arPoints.get(i).getName(), x - (30 * arPoints.get(i).getName().length() / 2), y - 80, paint);
//            }
//        }
        float[] currentLocationInECEF = LocationHelper.WSG84toECEF(currentLocation);
        float[] pointInECEF = LocationHelper.WSG84toECEF(arPoint.getLocation());
        //float[] pointInECEF = LocationHelper.WSG84toECEF(currentLocation);
        float[] pointInENU = LocationHelper.ECEFtoENU(currentLocation, currentLocationInECEF, pointInECEF);

        float[] cameraCoordinateVector = new float[4];
        Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedProjectionMatrix, 0, pointInENU, 0);

        // cameraCoordinateVector[2] is z, that always less than 0 to display on right position
        // if z > 0, the point will display on the opposite
        float x = (0.5f + cameraCoordinateVector[0] / cameraCoordinateVector[3]) * canvas.getWidth();
        float y = (0.5f - cameraCoordinateVector[1] / cameraCoordinateVector[3]) * canvas.getHeight();

        canvas.drawCircle(x, y, radius, paint);
        canvas.drawText(arPoint.getName(), x - (30 * arPoint.getName().length() / 2), y - 80, paint);

    }
}
