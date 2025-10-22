package com.example.projek;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

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

        // load fragment default (Beranda) saat pertama kali aplikasi dibuka
        if (savedInstanceState == null) {
            loadFragment(new Beranda_Fragment());
            bottomNavigationView.setSelectedItemId(R.id.beranda);
        }

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.beranda) {
                    loadFragment(new Beranda_Fragment());
                } else if (itemId == R.id.jadwal) {
                    loadFragment(new Jadwal_Fragment());
                } else if (itemId == R.id.chat) {
                    loadFragment(new TestimoniFragment());
                } else if (itemId == R.id.profile) {
                    loadFragment(new AkunFragment());
                }
                return true;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flfragment, fragment);
        fragmentTransaction.commit();
    }
}
