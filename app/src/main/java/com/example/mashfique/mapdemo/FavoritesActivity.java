package com.example.mashfique.mapdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

public class FavoritesActivity extends AppCompatActivity
    implements AddFavoriteFragment.OnNewFavoritesCreationListener{

    private Toolbar toolbar;
    private FavoritesFragment favoritesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        if (savedInstanceState == null) {
            favoritesFragment = new FavoritesFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.container_fav_activity, favoritesFragment).commit();
        }


        toolbar = (Toolbar) findViewById(R.id.toolbar_fav);
        toolbar.setTitle("Favorites");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItem = item.getItemId();
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        switch (menuItem) {
            case android.R.id.home:
                if (backStackCount > 0) {
                    getSupportFragmentManager().popBackStack();
                    return true;
                } else {
                    return super.onOptionsItemSelected(item);
                }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewFavoritesCreation(Favorite newFavorite) {
        favoritesFragment.addFavorite(newFavorite);
    }
}
