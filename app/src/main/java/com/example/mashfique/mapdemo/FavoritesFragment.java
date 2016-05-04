package com.example.mashfique.mapdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class FavoritesFragment extends Fragment {

    private ArrayAdapter<String> mFavoritesAdapter;
    private String[] fakeFavorites = {"Keller Hall",
            "Coffman Memorial Union",
            "Willey Hall"
    };
    FloatingActionButton fab;
    AddFavoriteFragment favFrag;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        initFab();
        super.onCreate(savedInstanceState);
        favFrag = new AddFavoriteFragment();
    }

    private void initFab() {
        final Random rand = new Random();
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab_fav);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mFavoritesAdapter.add(fakeFavorites[rand.nextInt(3)]);
               // mFavoritesAdapter.add("adsfa");
                mFavoritesAdapter.notifyDataSetChanged();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_fav_activity, favFrag, null)
                        .addToBackStack(null)
                        .commit();
                fab.hide();
                //mFavoritesAdapter.add(favFrag.getFavName().getText().toString());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        if (mFavoritesAdapter == null) {
            mFavoritesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>());
        } else {
            mFavoritesAdapter.add(favFrag.getFavName());
            Snackbar.make(rootView, "Favorites added!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        ListView listView = (ListView) rootView.findViewById(R.id.listview_fav);
        listView.setAdapter(mFavoritesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*Snackbar.make(view, "Favorites clicked!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Log.d("Checkhere", "Did we get to item click?");
                Intent mainIntent = new Intent(view.getContext(), MainActivity.class);
                mainIntent.putExtra("favorite", favFrag.getFavName());
                startActivity(mainIntent);
            }
        });
        fab.show();
        return rootView;
    }

}
