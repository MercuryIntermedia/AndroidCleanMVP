package mvp.sample.io.mercury.mvpexample;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Collection;

import mvp.sample.io.mercury.mvpexample.di.Container;
import mvp.sample.io.mercury.mvpexample.entity.Favorite;
import mvp.sample.io.mercury.mvpexample.interactor.FavoriteAdder;
import mvp.sample.io.mercury.mvpexample.interactor.FavoriteRemover;
import mvp.sample.io.mercury.mvpexample.interactor.FavoritesGetter;
import mvp.sample.io.mercury.mvpexample.view.FavoriteCrudItem;
import mvp.sample.io.mercury.mvpexample.view.FavoriteCrudPresenter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class FavoriteCrudActivity extends ActionBarActivity implements FavoriteCrudPresenter.FavoriteCrudView {

    private static final String FRAG_TAG_PRESENTER_HOLDER = "presenterHolder";

    private FavoriteAdder favoriteAdder = Container.getInstance().getFavoriteAdder();
    private FavoritesGetter favoritesGetter = Container.getInstance().getFavoritesGetter();
    private FavoriteRemover favoritesRemover = Container.getInstance().getFavoritesRemover();

    private FavoriteCrudPresenter presenter;

    private ArrayAdapter<Favorite> listAdapter;
    private View add1Btn;
    private View add2Btn;
    private View add3Btn;
    private View loading;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        PresenterHolderFragment phf = (PresenterHolderFragment) fm.findFragmentByTag(FRAG_TAG_PRESENTER_HOLDER);

        if (phf == null) {
            presenter = new FavoriteCrudPresenter(favoriteAdder, favoritesGetter, favoritesRemover, Schedulers.io(), AndroidSchedulers.mainThread());
            fm.beginTransaction().add(new PresenterHolderFragment(presenter), FRAG_TAG_PRESENTER_HOLDER).commit();
        } else {
            presenter = phf.presenter;
        }

        final FavoriteCrudItem.OnRemoveClickListener removeClickListener = new FavoriteCrudItem.OnRemoveClickListener() {
            @Override
            public void onRemoveClicked(Favorite favorite) {
                presenter.removeFavorite(favorite);
            }
        };

        list = (ListView) findViewById(R.id.list);
        listAdapter = new ArrayAdapter<Favorite>(this, -1) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                FavoriteCrudItem view = (FavoriteCrudItem) convertView;
                if (view == null) {
                    view = (FavoriteCrudItem) LayoutInflater.from(getContext()).inflate(R.layout.favorite_item, parent, false);
                }

                view.bind(getItem(position));
                view.setOnRemoveClickListener(removeClickListener);

                return view;
            }
        };

        list.setAdapter(listAdapter);

        add1Btn = findViewById(R.id.add_1_btn);
        add1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.addFavorite(new Favorite(1));
            }
        });

        add2Btn = findViewById(R.id.add_2_btn);
        add2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.addFavorite(new Favorite(2));
            }
        });

        add3Btn = findViewById(R.id.add_3_btn);
        add3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.addFavorite(new Favorite(3));
            }
        });

        loading = findViewById(R.id.loading);

        presenter.attachView(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void loadFavorites(Collection<Favorite> favorites) {
        listAdapter.clear();
        listAdapter.addAll(favorites);
    }

    @Override
    public void notifyAddSuccessful(Favorite favorite) {
        Toast.makeText(this, String.format("%s was added", favorite.getId()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void notifyRemoveSuccessful(Favorite favorite) {
        Toast.makeText(this, String.format("%s was removed", favorite.getId()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void notifyFavoriteAlreadyExists(Favorite favorite) {
        Toast.makeText(this, String.format("%s was already added", favorite.getId()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void disableAddControls() {
        add1Btn.setEnabled(false);
        add2Btn.setEnabled(false);
        add3Btn.setEnabled(false);
    }

    @Override
    public void enableAddControls() {
        add1Btn.setEnabled(true);
        add2Btn.setEnabled(true);
        add3Btn.setEnabled(true);
    }

    @Override
    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        loading.setVisibility(View.GONE);
    }

    @SuppressLint("ValidFragment")
    private class PresenterHolderFragment extends Fragment {
        final FavoriteCrudPresenter presenter;

        public PresenterHolderFragment(FavoriteCrudPresenter presenter) {
            this.presenter = presenter;
            setRetainInstance(true);
        }
    }
}