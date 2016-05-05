package com.example.mashfique.mapdemo;

import android.app.Activity;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.daimajia.swipe.adapters.ArraySwipeAdapter;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;


public class FavoritesFragment extends Fragment {

    private FavoriteSwipeAdapter mFavoritesAdapter;
    private FloatingActionButton fab;
    private AddFavoriteFragment favFrag;
    private ListView favorites;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        initFab();
        super.onCreate(savedInstanceState);
        favFrag = new AddFavoriteFragment();
    }

    private void initFab() {
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab_fav);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFavoritesAdapter.notifyDataSetChanged();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_fav_activity, favFrag, null)
                        .addToBackStack(null)
                        .commit();
                fab.hide();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        initListFavorites(rootView);
        fab.show();
        return rootView;
    }

    private void initListFavorites(View view) {

        List<Favorite> savedFavorites = readSavedFavorites();
        if (mFavoritesAdapter == null) {
            if (savedFavorites != null && savedFavorites.size() > 0) {
                mFavoritesAdapter = new FavoriteSwipeAdapter(getContext(), savedFavorites);
            } else {
                mFavoritesAdapter = new FavoriteSwipeAdapter(getContext(), new ArrayList<Favorite>());
            }
        }
        favorites = (ListView) view.findViewById(R.id.listview_fav);
        favorites.setAdapter(mFavoritesAdapter);
        favorites.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mainIntent = new Intent(view.getContext(), MainActivity.class);
                mainIntent.putExtra("favorite", favFrag.getFavName());
                startActivity(mainIntent);
                return false;
            }
        });
    }

    private List<Favorite> readSavedFavorites() {
        ArrayList<Favorite> savedFavorites;
        FileInputStream inputStream;
        final String SAVEFILE = "favorites";
        try {
            savedFavorites = new ArrayList<>();
            inputStream = new FileInputStream(SAVEFILE);
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            while (true) {
                Favorite currentFavorite = (Favorite) objectInputStream.readObject();
                savedFavorites.add(currentFavorite);
            }
        } catch (IOException | ClassNotFoundException e) {
            savedFavorites = null;
        }
        return savedFavorites;
    }

    private void saveFavorites() {
        final String SAVEFILE = "favorites";
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(SAVEFILE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            for (int i = 0; i < mFavoritesAdapter.getCount(); i++) {
                Favorite currentFavorite = (Favorite) mFavoritesAdapter.getItem(i);
                objectOutputStream.writeObject(currentFavorite);
            }
        } catch (IOException e) {

        }
    }

    @Override
    public void onDestroy() {
        saveFavorites();
        super.onDestroy();
    }

    public void addFavorite(Favorite newFavorite) {
        Toast.makeText(getContext(), "Favorite Added!", Toast.LENGTH_SHORT).show();
        newFavorite = new Favorite("This is a test");
        mFavoritesAdapter.add(newFavorite);
        mFavoritesAdapter.notifyDataSetChanged();
    }

    public static class FavoriteSwipeAdapter extends BaseSwipeAdapter {

        private Context mContext;
        private List<Favorite> mFavorites;

        public FavoriteSwipeAdapter(Context context, List<Favorite> favorites) {
            mContext = context;
            mFavorites = favorites;
        }

        @Override
        public int getSwipeLayoutResourceId(int i) {
            return R.id.swipe_favorite;
        }

        @Override
        public View generateView(int i, ViewGroup viewGroup) {
            return LayoutInflater.from(mContext).inflate(R.layout.favorites_list_item, null);
        }

        @Override
        public void fillValues(final int position, View view) {
            Favorite currentFavorite = mFavorites.get(position);
            TextView textView = (TextView) view.findViewById(R.id.text_data_fav);
            textView.setText(currentFavorite.toString());

            view.findViewById(R.id.button_favorite_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFavorites.remove(position);
                    closeAllItems();
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getCount() {
            return mFavorites.size();
        }

        @Override
        public Object getItem(int position) {
            return mFavorites.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void add(Favorite newFavorite) {
            mFavorites.add(newFavorite);
            closeItem(getCount()-1);
            notifyDataSetChanged();
        }
    }
}
