package mvp.sample.io.mercury.mvpexample.view;

import android.os.Handler;

import java.util.Collection;

import mvp.sample.io.mercury.mvpexample.entity.Favorite;
import mvp.sample.io.mercury.mvpexample.interactor.FavoriteAdder;
import mvp.sample.io.mercury.mvpexample.interactor.FavoriteRemover;
import mvp.sample.io.mercury.mvpexample.interactor.FavoritesGetter;

public class FavoriteCrudPresenter {

    private final static NullFavoriteCrudView NULL_VIEW = new NullFavoriteCrudView();

    private final FavoriteAdder adder;
    private final FavoritesGetter getter;
    private final FavoriteRemover remover;

    private FavoriteCrudView view = NULL_VIEW;
    private State presenterState = State.WAITING;

    public FavoriteCrudPresenter(FavoriteAdder adder, FavoritesGetter getter, FavoriteRemover remover) {
        this.adder = adder;
        this.getter = getter;
        this.remover = remover;
    }

    /**
     * Attaches a view to this presenter and initializes the view based on the state of the presenter
     * @param v - The View (The 'V' of "MVP", not necessarily an Android View) that gets the presentation
     */
    public void attachView(FavoriteCrudView v) {
        this.view = v;

        final Handler handler = new Handler();
        switch (presenterState) {
            case WAITING:
                new Thread() {
                    @Override
                    public void run() {
                        final Collection<Favorite> favorites = getter.execute();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                view.hideLoading();
                                view.enableAddControls();
                                view.loadFavorites(favorites);
                                presenterState = FavoriteCrudPresenter.State.WAITING;

                            }
                        });
                    }
                }.start();
                // Fall through
            case LOADING:
            case ADDING:
            case REMOVING:
                view.showLoading();
                view.disableAddControls();
                break;
        }
    }

    public void detachView() {
        view = NULL_VIEW;
    }

    /**
     * Add a favorite to the collection and modify the presentation accordingly as that happens
     * @param favorite - What favorite gets added
     */
    public void addFavorite(final Favorite favorite) {
        view.disableAddControls();
        view.showLoading();

        final Handler handler = new Handler();
        new Thread() {
            @Override
            public void run() {
                presenterState = FavoriteCrudPresenter.State.ADDING;
                FavoriteAdder.Response adderResponse = adder.execute(favorite);
                if (adderResponse.alreadyExisted()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            view.notifyFavoriteAlreadyExists(favorite);
                            view.enableAddControls();
                            view.hideLoading();
                            presenterState = FavoriteCrudPresenter.State.WAITING;
                        }
                    });
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            final Collection<Favorite> favorites = getter.execute();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    view.notifyAddSuccessful(favorite);
                                    view.loadFavorites(favorites);
                                    view.enableAddControls();
                                    view.hideLoading();
                                    presenterState = FavoriteCrudPresenter.State.WAITING;
                                }
                            });
                        }
                    }.start();
                }
            }
        }.start();
    }

    /**
     * Remove a favorite from the collection and modify the presentation accordingly as that happens
     * @param favorite - What favorite gets removed
     */
    public void removeFavorite(final Favorite favorite) {
        presenterState = State.REMOVING;
        view.showLoading();
        view.disableAddControls();

        final Handler handler = new Handler();
        new Thread() {
            @Override
            public void run() {
                remover.execute(favorite);
                final Collection<Favorite> favorites = getter.execute();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        view.notifyRemoveSuccessful(favorite);
                        view.loadFavorites(favorites);
                        view.hideLoading();
                        view.enableAddControls();
                        presenterState = FavoriteCrudPresenter.State.WAITING;
                    }
                });
            }
        }.start();
    }

    private enum State { WAITING, ADDING, REMOVING, LOADING }

    public interface FavoriteCrudView {
        void loadFavorites(Collection<Favorite> favorites);

        void notifyAddSuccessful(Favorite favorite);

        void notifyRemoveSuccessful(Favorite favorite);

        void notifyFavoriteAlreadyExists(Favorite favorite);

        void disableAddControls();

        void enableAddControls();

        void showLoading();

        void hideLoading();
    }

    public static class NullFavoriteCrudView implements FavoriteCrudView {

        @Override
        public void loadFavorites(Collection<Favorite> favorites) {}

        @Override
        public void notifyAddSuccessful(Favorite favorite) {}

        @Override
        public void notifyRemoveSuccessful(Favorite favorite) {}

        @Override
        public void notifyFavoriteAlreadyExists(Favorite favorite) {}

        @Override
        public void disableAddControls() {}

        @Override
        public void enableAddControls() {}

        @Override
        public void showLoading() {}

        @Override
        public void hideLoading() {}
    }
}
