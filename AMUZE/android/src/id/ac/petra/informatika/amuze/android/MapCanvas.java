package id.ac.petra.informatika.amuze.android;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import id.ac.petra.informatika.amuze.android.data.MuseumContract;

/**
 * Created by josephnw on 11/4/2015.
 */
public class MapCanvas extends View {
    //PINCH ZOOM
    private static float MIN_ZOOM = 1f;
    private static float MAX_ZOOM = 5f;
    private float scaleFactor = 1.f;
    private ScaleGestureDetector detector;

    private float startX = 0f;
    private float startY = 0f;

    private float translateX = 0f;
    private float translateY = 0f;

    private float previousTranslateX = 0f;
    private float previousTranslateY = 0f;

    private static int NONE = 0;
    private static int DRAG = 1;
    private static int ZOOM = 2;

    private int mode;
    private boolean dragged = true;
    //----------

    Context canvasCurrentContext;
    Paint paint = new Paint();

    Drawable imageMap, imageItem ;

    //Map Tiles
    int screenWidth, screenHeight;
    String JSONGrid;
    JSONObject itemsJSON;
    QRItem[] items;

    int[] itemStatus;
    Drawable[] itemStatusImages = new Drawable[4];
    //ItemStatus
    // 0 = Not scanned
    // 1 = Scanned
    // 2 = Faved & not scanned
    // 3 = Faved & Scanned
    private static final int STATUS_NONE = 0;
    private static final int STATUS_SCAN = 1;
    private static final int STATUS_FAV = 2;
    private static final int STATUS_SCAN_FAV = 3;


    int tileWidth, tileHeight, gridWidth, gridHeight;
    public void Toast(String val){
        Toast.makeText(canvasCurrentContext, val, Toast.LENGTH_SHORT).show();
    }
    void setData(String json, String photo){
        imageMap = new BitmapDrawable(getResources(),Utility.loadBitmap(getContext(),photo));

        JSONGrid = json;
        try {
            itemsJSON = new JSONObject(JSONGrid);
            int numItems = itemsJSON.length();
            items = new QRItem[numItems];
            itemStatus = new int[numItems];
            for(int i=0;i<numItems;i++){
                int r = Integer.parseInt(itemsJSON.getJSONObject(String.valueOf(i)).getString("r"));
                int c = Integer.parseInt(itemsJSON.getJSONObject(String.valueOf(i)).getString("c"));
                int s = Integer.parseInt(itemsJSON.getJSONObject(String.valueOf(i)).getString("s"));
                String q = itemsJSON.getJSONObject(String.valueOf(i)).getString("q");
                items[i] = new QRItem(r,c,s,q);
            }
            fillItemStatus();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        reloadGrid();
    }
    private static final String[] ITEM_COLUMNS = {
            MuseumContract.ItemEntry.COLUMN_SCAN,
            MuseumContract.ItemEntry.COLUMN_FAV
    };
    static final int COL_ITEM_SCAN = 0;
    static final int COL_ITEM_FAV = 1;

    public void fillItemStatus(){
        for(int i = 0; i < items.length; i++){
            String id = items[i].qrCode();
            Uri itemUri = MuseumContract.ItemEntry.buildUriById(id);
            Cursor cursor = canvasCurrentContext.getContentResolver().query(itemUri, ITEM_COLUMNS, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int scan = cursor.getInt(COL_ITEM_SCAN);
                int fav = cursor.getInt(COL_ITEM_FAV);
                if(scan == 0){
                    if(fav == 0){
                        itemStatus[i] = STATUS_NONE;
                    }
                    else{
                        itemStatus[i] = STATUS_FAV;
                    }
                }
                else{
                    if(fav == 0){
                        itemStatus[i] = STATUS_SCAN;
                    }
                    else{
                        itemStatus[i] = STATUS_SCAN_FAV;
                    }
                }
                cursor.close();
            }
        }
        invalidate();
    }
    public void reloadGrid(){
        DisplayMetrics metrics = canvasCurrentContext.getResources().getDisplayMetrics();
        screenWidth = (int) (metrics.widthPixels * 6/6.0);
        screenHeight = (int) (metrics.heightPixels *3/6.0);

        imageMap.setBounds(0, 0, screenWidth, screenHeight);

        gridWidth = 80;
        gridHeight = 60;
        tileWidth = screenWidth/gridWidth;
        tileHeight = screenHeight/gridHeight;
    }
    public MapCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        // this constructor used when creating view through XML
        detector = new ScaleGestureDetector(getContext(), new ScaleListener());
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        screenWidth = (int) (metrics.widthPixels * 6/6.0);
        screenHeight = (int) (metrics.heightPixels *4/6.0);
        imageMap = getResources().getDrawable(R.drawable.main_map);
        items = new QRItem[0];
        imageItem = getResources().getDrawable(R.drawable.item_qrcode);
        itemStatusImages[0] = getResources().getDrawable(R.drawable.not_scanned);
        itemStatusImages[1] = getResources().getDrawable(R.drawable.scanned);
        itemStatusImages[2] = getResources().getDrawable(R.drawable.notscanned_faved);
        itemStatusImages[3] = getResources().getDrawable(R.drawable.faved);
        imageMap.setBounds(0, 0, screenWidth, screenHeight);

        gridWidth = 80;
        gridHeight = 60;
        tileWidth = screenWidth/gridWidth;
        tileHeight = screenHeight/gridHeight;
        canvasCurrentContext = context;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                startX = event.getX();
                startY = event.getY();
                translateX = 0;
                translateY = 0;
                previousTranslateX = 0;
                previousTranslateY = 0;
                int row = (int) (event.getY()/tileHeight);
                int col = (int) (event.getX()/tileWidth);
                Log.v("CANVAS", row + " | " + col);
                for(int i=0;i<items.length;i++){
                    boolean rowInside = row >= items[i].row() && row <= items[i].row()+items[i].size();
                    boolean colInside = col >= items[i].col() && col <= items[i].col()+items[i].size();
                    if(rowInside && colInside)
                    {
                        //Toast("An item "+i+" is clicked, qrCode: "+items[i].qrCode());
                        Log.e("MapCanvas", "An item "+i+" is clicked, qrCode: "+items[i].qrCode());
                        Intent intent = new Intent(getContext(), ItemActivity.class)
                                .putExtra("id", items[i].qrCode());
                        getContext().startActivity(intent);
                        //itemStatus[i]++;
                        //itemStatus[i]%=4;
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                translateX = event.getX() - startX;
                translateY = event.getY() - startY;

                //We cannot use startX and startY directly because we have adjusted their values using the previous translation values.
                //This is why we need to add those values to startX and startY so that we can get the actual coordinates of the finger.
                double distance = Math.sqrt(Math.pow(event.getX() - (startX), 2) +
                                Math.pow(event.getY() - (startY), 2)
                );

                if(distance > 0 && translateX < 100 && translateY < 100) {
                    dragged = true;
                    //Log.v("CANVAS - OnActionMove",startX + " " + startY + "|" + translateX +" "+translateY);
                    startX = event.getX();
                    startY = event.getY();
                    previousTranslateX = startX;
                    previousTranslateY = startY;
                    invalidate();
                }

                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                break;

            case MotionEvent.ACTION_UP:
                mode = NONE;
                dragged = false;
                //All fingers went up, so let's save the value of translateX and translateY into previousTranslateX and
                //previousTranslate
                previousTranslateX = event.getX();//translateX;
                previousTranslateY = event.getY();//translateY;
                startY = event.getY();
                startX = event.getX();
                translateY = event.getY();
                translateX = event.getX();
                break;

            case MotionEvent.ACTION_POINTER_UP:
                mode = DRAG;

                //This is not strictly necessary; we save the value of translateX and translateY into previousTranslateX
                //and previousTranslateY when the second finger goes up
                previousTranslateX = event.getX();//translateX;
                previousTranslateY = event.getY();//translateY;
                startY = event.getY();
                startX = event.getX();
                translateY = event.getY();
                translateX = event.getX();
                break;
        }
        detector.onTouchEvent(event);
        if ((mode == DRAG && scaleFactor != 1f) || mode == ZOOM) {
            //invalidate();
        }
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.scale(this.scaleFactor, this.scaleFactor, this.detector.getFocusX(), this.detector.getFocusY());

        if((translateX * -1) < 0) {
            translateX = 0;
        }
        else if((translateX * -1) > (scaleFactor - 1) * screenWidth) {
            translateX = (1 - scaleFactor) * screenWidth;
        }

        if(translateY * -1 < 0) {
            translateY = 0;
        }
        else if((translateY * -1) > (scaleFactor - 1) * screenHeight) {
            translateY = (1 - scaleFactor) * screenHeight;
        }

        //Log.v("CANVAS",startX + " " + startY + "|" + translateX +" "+translateY);
        //We need to divide by the scale factor here, otherwise we end up with excessive panning based on our zoom level
        //because the translation amount also gets scaled according to how much we've zoomed into the canvas.
        canvas.translate(-translateX / scaleFactor, -translateY / scaleFactor);
        imageMap.draw(canvas);
        for(int i=0;i<items.length;i++){
            int r = items[i].row();
            int c = items[i].col();
            int s = items[i].size();
            itemStatusImages[itemStatus[i]].setBounds(c * tileWidth, r * tileHeight, c * tileWidth + s * tileWidth, r * tileHeight + s * tileHeight);
            itemStatusImages[itemStatus[i]].draw(canvas);
        }
        canvas.restore();

    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
            invalidate();
            return true;
        }
    }
}
