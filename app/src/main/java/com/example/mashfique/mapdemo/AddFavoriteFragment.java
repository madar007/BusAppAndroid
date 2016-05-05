package com.example.mashfique.mapdemo;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFavoriteFragment extends Fragment {

    private Toolbar toolbar;
    private String activityToolbarTitle;
    private AutoCompleteTextView favName = null;
    private Favorite newFav;
    private GooglePlacesAutocompleteAdapter mPlacesAdapter;
    private GooglePlacesPrediction building;
    private OnNewFavoritesCreationListener mListener;


    public AddFavoriteFragment() {
        // Required empty public constructor
    }

    public String getFavName(){
        if(favName == null ){
            return null;
        }
        return favName.getText().toString();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_favorite, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_fav);
        activityToolbarTitle = toolbar.getTitle().toString();
        toolbar.setTitle("New favorite");
        newFav = new Favorite();
        favName = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_fav, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final InputMethodManager inputMethodManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        favName = (AutoCompleteTextView) view.findViewById(R.id.autocomplete_building_name);
        mPlacesAdapter = new GooglePlacesAutocompleteAdapter(getContext(), R.layout.autocomplete_list_item);
        favName.setAdapter(mPlacesAdapter);
        favName.setThreshold(2);
        favName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                building = mPlacesAdapter.getItem(position);
                String selectedPlace = building.getBuildingName();
                favName.setText(selectedPlace);
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        switch (menuId) {
            case R.id.action_menu_fav_done:
                addFavorite();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addFavorite() {
        newFav = new Favorite();
        newFav.setFavoriteName(favName.getText().toString());
        mListener.onNewFavoritesCreation(newFav);
        toolbar.setTitle(activityToolbarTitle);
        getActivity().getSupportFragmentManager().popBackStack();
    }

    public interface OnNewFavoritesCreationListener {
        void onNewFavoritesCreation(Favorite newFavorite);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnNewFavoritesCreationListener) activity;
        } catch (ClassCastException e) {
            Log.e(AddFavoriteFragment.class.getSimpleName(),
                    activity.getClass().getSimpleName() + " must implement OnNewFavoriteCreationListener!");
        }
    }


}
