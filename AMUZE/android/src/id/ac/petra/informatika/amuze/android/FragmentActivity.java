package id.ac.petra.informatika.amuze.android;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import id.ac.petra.informatika.amuze.android.data.MuseumContract;

//import android.support.v4.view.ViewPager;

public class FragmentActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    public static int mMuseumId;
    private int[] tabIcons = {
            R.drawable.navbar_home,
            R.drawable.navbar_maps,
            R.drawable.navbar_scan,
            R.drawable.navbar_games,
            R.drawable.navbar_favorites
    };
    private int[] activeTabIcons = {
            R.drawable.navbar_home_active,
            R.drawable.navbar_maps_active,
            R.drawable.navbar_scan_active,
            R.drawable.navbar_games_active,
            R.drawable.navbar_favorites_active
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mMuseumId = extras.getInt("id");
        }

        viewPager = (CustomViewPager) findViewById(R.id.viewPager);
        viewPager.setPagingEnabled(false);//custom
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();
        //setupTabIcons();
        //Set Listener For tabLayout
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int selected = tab.getPosition();
                tab.setIcon(activeTabIcons[selected]);
                viewPager.setCurrentItem(selected);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int selected = tab.getPosition();
                tab.setIcon(tabIcons[selected]);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void setupTabIcons() {
        for (int i = 1; i < 5; i++) {
            tabLayout.getTabAt(i).setIcon(tabIcons[i]);
        }
        tabLayout.getTabAt(0).setIcon(activeTabIcons[0]);
    }

    public void setupViewPager(CustomViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Bundle args = new Bundle();
        args.putString("id", ""+mMuseumId);
        GeneralFragment generalFragment = new GeneralFragment();
        generalFragment.setArguments(args);
        adapter.addFragment(generalFragment, "");
        adapter.addFragment(new MapFragment(), "");
        adapter.addFragment(new QRCodeFragment(), "");
        adapter.addFragment(new GameFragment(), "");
        adapter.addFragment(new FavoritesFragment(), "");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:// Respond to the action bar's Up/Home button
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
