package com.example.projek;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity  {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // init bottom nav
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        frameLayout = findViewById(R.id.flfragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int itemId = item.getItemId();

            if (itemId == R.id.beranda){
                loadFragment(new Beranda_Fragment(),false);
            } else if (itemId == R.id.jadwal) {
                loadFragment(new Jadwal_Fragment(),false);
            } else if (itemId == R.id.chat) {
                loadFragment(new Chat_Fragment(), false);
            } else if (itemId == R.id.testimoni) {
                loadFragment(new TestimoniFragment(), false);
            }else{
                loadFragment(new AkunFragment(),false);
            }
                loadFragment(new Beranda_Fragment(), true);
            return true;
            }
        });
    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInitialized){
            fragmentTransaction.add(R.id.flfragment, fragment);
        } else {
            fragmentTransaction.add(R.id.flfragment, fragment);
        }


        fragmentTransaction.commit();
    }
    }