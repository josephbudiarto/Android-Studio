package id.ac.petra.informatika.amuze.android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import id.ac.petra.informatika.amuze.android.data.MuseumContract;

public class GeneralFragment extends Fragment {
    private String mId;
    private String mName, mDescription, mPhoto;

    private static final String[] COL = {
            MuseumContract.MuseumEntry.TABLE_NAME + "." + MuseumContract.MuseumEntry._ID,
            MuseumContract.MuseumEntry.COLUMN_MUSEUM_NAME,
            MuseumContract.MuseumEntry.COLUMN_DESCRIPTION,
            MuseumContract.MuseumEntry.COLUMN_PHOTO
    };
    static final int COL_ID = 0;
    static final int COL_NAME = 1;
    static final int COL_DESC = 2;
    static final int COL_PHOTO = 3;
    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(packageName);
        if (i == null) {
            return false;
            //throw new PackageManager.NameNotFoundException();
        }
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);
        return true;
    }
    public GeneralFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mId = args.getString("id");
        Uri museumUri = MuseumContract.MuseumEntry.buildUriById(mId + "");
        Cursor cursor = getActivity().getContentResolver().query(museumUri, COL, MuseumContract.MuseumEntry._ID + "=?", new String[]{mId + ""}, null);
        mName = "Museum";
        if (cursor != null && cursor.moveToFirst()) {
            mName = cursor.getString(COL_NAME);
            mDescription = cursor.getString(COL_DESC);
            mPhoto = "";
            if(!cursor.isNull(COL_PHOTO))
                mPhoto = cursor.getString(COL_PHOTO);
            //getSupportActionBar().setTitle(name);
            //getSupportActionBar().setWindowTitle(name);
            //getSupportActionBar().setDisplayShowTitleEnabled(true);
            cursor.close();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_general, container, false);


        TextView title = (TextView)rootView.findViewById(R.id.title_textview);
        TextView description = (TextView)rootView.findViewById(R.id.description_textview);
        ImageView image = (ImageView)rootView.findViewById(R.id.museum_imageview);
        Button btn = (Button) rootView.findViewById(R.id.openMusia);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.putExtra("MuseumID",mId+"");
                intent.putExtra("from", "-2");
                //intent.setClassName("com.manpro.test.testmanpro","com.manpro.test.testmanpro.MainActivity");
                //intent.setClassName("com.manpro.musia","com.manpro.musia.MuseumDetail");
                intent.setClassName("com.example.android.manprotrial3","com.example.android.manprotrial3.MuseumDetail");
                startActivity(intent);
            }
        });
        title.setText(mName);
        description.setText(mDescription);
        if(mPhoto.isEmpty()==false) {
            image.setImageBitmap(Utility.loadBitmap(getActivity(), mPhoto));
        }
        return rootView;
    }
    public void openMusia(View v){
        Intent musiaIntent = new Intent();
        musiaIntent.setComponent(new ComponentName("com.example.android.app_no1","com.example.android.app_no1.MainActivity"));
        startActivity(musiaIntent);
    }
}
