package mvp.sample.io.mercury.mvpexample.view;

import android.os.AsyncTask;
import android.os.Handler;

import java.util.Collection;

import mvp.sample.io.mercury.mvpexample.entity.Favorite;
import mvp.sample.io.mercury.mvpexample.interactor.FavoriteAdder;
import mvp.sample.io.mercury.mvpexample.interactor.FavoriteRemover;
import mvp.sample.io.mercury.mvpexample.interactor.FavoritesGetter;
import mvp.sample.io.mercury.mvpexample.repository.FavoriteRepo;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action1;

public class FavoriteCrudPresenter {

    private final static NullFavoriteCrudView NULL_VIEW = new NullFavoriteCrudView();

    private final FavoriteAdder adder;
    private final FavoritesGetter getter;
    private final FavoriteRemover remover;
    private final Scheduler backgroundScheduler;
    private final Scheduler foregroundScheduler;

    private FavoriteCrudView view = NULL_VIEW;
    private State presenterState = State.WAITING;

    public FavoriteCrudPresenter(FavoriteAdder adder, FavoritesGetter getter, FavoriteRemover remover, Scheduler backgroundScheduler, Scheduler foregroundScheduler) {
        this.adder = adder;
        this.getter = getter;
        this.remover = remover;
        this.backgroundScheduler = backgroundScheduler;
        this.foregroundScheduler = foregroundScheduler;
    }

    /**
     * Attaches a view to this presenter and initializes the view based on the state of the presenter
     *
     * @param v - The View (The 'V' of "MVP", not necessarily an Android View) that gets the presentation
     */
    public void attachView(FavoriteCrudView v) {
        this.view = v;

        final Handler handler = new Handler();
        switch (presenterState) {
            case WAITING:
                view.disableAddControls();
                view.showLoading();

                Action1<FavoriteRepo.FavoritesResponse> favoritesGetterSubscriber = new Action1<FavoriteRepo.FavoritesResponse>() {
                    @Override
                    public void call(FavoriteRepo.FavoritesResponse favoriteResponse) {
                        view.loadFavorites(favoriteResponse.getFavorites());
                        if (!favoriteResponse.isCachedData()) {
                            view.hideLoading();
                            view.enableAddControls();
                        } else {
                            // Get the non-cached version
                            Observable.create(new Observable.OnSubscribe<FavoriteRepo.FavoritesResponse>() {
                                @Override
                                public void call(Subscriber<? super FavoriteRepo.FavoritesResponse> subscriber) {
                                    FavoriteRepo.FavoritesResponse favoritesResponse = getter.execute(new FavoriteRepo.FavoritesRequest(true)); // skip cache
                                    subscriber.onNext(favoritesResponse);
                                    subscriber.onCompleted();
                                }
                            }).subscribeOn(backgroundScheduler)
                                    .observeOn(foregroundScheduler)
                                    .subscribe(this);
                        }

                        presenterState = State.WAITING;
                    }
                };

                Observable.create(new Observable.OnSubscribe<FavoriteRepo.FavoritesResponse>() {
                    @Override
                    public void call(Subscriber<? super FavoriteRepo.FavoritesResponse> subscriber) {
                        FavoriteRepo.FavoritesResponse favoritesResponse = getter.execute(new FavoriteRepo.FavoritesRequest(false));
                        subscriber.onNext(favoritesResponse);
                        subscriber.onCompleted();
                    }
                }).subscribeOn(backgroundScheduler)
                .observeOn(foregroundScheduler)
                .subscribe(favoritesGetterSubscriber);

//                new Thread() {
//                    @Override
//                    public void run() {
//                        final Collection<Favorite> favorites = getter.execute();
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                view.hideLoading();
//                                view.enableAddControls();
//                                view.loadFavorites(favorites);
//                                presenterState = FavoriteCrudPresenter.State.WAITING;
//
//                            }
//                        });
//                    }
//                }.start();

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
     *
     * @param favorite - What favorite gets added
     */
    public void addFavorite(final Favorite favorite) {
        presenterState = FavoriteCrudPresenter.State.ADDING;
        view.disableAddControls();
        view.showLoading();
        view.add(favorite);

        final Handler handler = new Handler();
        new Thread() {
            @Override
            public void run() {
                final FavoriteAdder.Response adderResponse = adder.execute(favorite);
                final Collection<Favorite> favorites = getter.execute(new FavoriteRepo.FavoritesRequest(false)).getFavorites();

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        view.loadFavorites(favorites);
                        view.enableAddControls();
                        view.hideLoading();
                        if (adderResponse.alreadyExisted()) {
                            view.notifyFavoriteAlreadyExists(favorite);
                        } else {
                            view.notifyAddSuccessful(favorite);
                        }

                        presenterState = FavoriteCrudPresenter.State.WAITING;
                    }
                });
            }
        }.start();
    }

    /**
     * Remove a favorite from the collection and modify the presentation accordingly as that happens
     *
     * @param favorite - What favorite gets removed
     */
    public void removeFavorite(final Favorite favorite) {
        presenterState = State.REMOVING;
        view.showLoading();
        view.disableAddControls();
        view.remove(favorite);

        new AsyncTask<Favorite, Void, Collection<Favorite>>() {
            @Override
            protected Collection<Favorite> doInBackground(Favorite... params) {
                remover.execute(favorite);
                return getter.execute(new FavoriteRepo.FavoritesRequest(false)).getFavorites();
            }

            @Override
            protected void onPostExecute(Collection<Favorite> favorites) {
                view.notifyRemoveSuccessful(favorite);
                view.loadFavorites(favorites);
                view.hideLoading();
                view.enableAddControls();
                presenterState = FavoriteCrudPresenter.State.WAITING;
            }
        }.execute(favorite);
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

        void add(Favorite favorite);

        void remove(Favorite favorite);
    }

    public static class NullFavoriteCrudView implements FavoriteCrudView {

        @Override
        public void loadFavorites(Collection<Favorite> favorites) {
        }

        @Override
        public void notifyAddSuccessful(Favorite favorite) {
        }

        @Override
        public void notifyRemoveSuccessful(Favorite favorite) {
        }

        @Override
        public void notifyFavoriteAlreadyExists(Favorite favorite) {
        }

        @Override
        public void disableAddControls() {
        }

        @Override
        public void enableAddControls() {
        }

        @Override
        public void showLoading() {
        }

        @Override
        public void hideLoading() {
        }

        @Override
        public void add(Favorite favorite) {
        }

        @Override
        public void remove(Favorite favorite) {
        }
    }
}
