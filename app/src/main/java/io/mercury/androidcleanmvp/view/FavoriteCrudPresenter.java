package io.mercury.androidcleanmvp.view;

import java.util.Collection;
import java.util.concurrent.Executor;

import io.mercury.androidcleanmvp.entity.Favorite;
import io.mercury.androidcleanmvp.interactor.FavoriteAdder;
import io.mercury.androidcleanmvp.interactor.FavoriteRemover;
import io.mercury.androidcleanmvp.interactor.FavoritesGetter;
import io.mercury.androidcleanmvp.repository.FavoriteRepo;
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
    private final Executor backgroundExecutor;
    private final Executor foregroundExecutor;

    private FavoriteCrudView view = NULL_VIEW;
    private State presenterState = State.WAITING;

    public FavoriteCrudPresenter(FavoriteAdder adder, FavoritesGetter getter, FavoriteRemover remover, Scheduler backgroundScheduler, Scheduler foregroundScheduler, Executor backgroundExecutor, Executor foregroundExecutor) {
        this.adder = adder;
        this.getter = getter;
        this.remover = remover;
        this.backgroundScheduler = backgroundScheduler;
        this.foregroundScheduler = foregroundScheduler;
        this.backgroundExecutor = backgroundExecutor;
        this.foregroundExecutor = foregroundExecutor;
    }

    /**
     * Attaches a view to this presenter
     *
     * @param v - The View (The 'V' of "MVP", not necessarily an Android View) that gets the presentation
     */
    public void attachView(FavoriteCrudView v) {
        this.view = v;
    }

    /**
     * Initializes the view based on the state of the presenter
     */
    public void present() {
        switch (presenterState) {
            case WAITING:
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

        backgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final FavoriteAdder.Response adderResponse = adder.execute(favorite);
                final Collection<Favorite> favorites = getter.execute(new FavoriteRepo.FavoritesRequest(false)).getFavorites();

                foregroundExecutor.execute(new Runnable() {
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
        });
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

        backgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                remover.execute(favorite);
                final Collection<Favorite> favorites = getter.execute(new FavoriteRepo.FavoritesRequest(false)).getFavorites();

                foregroundExecutor.execute(new Runnable() {
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
        });
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
