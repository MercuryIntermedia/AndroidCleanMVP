package io.mercury.androidcleanmvp.di;

import io.mercury.androidcleanmvp.interactor.FavoriteAdder;
import io.mercury.androidcleanmvp.interactor.FavoriteRemover;
import io.mercury.androidcleanmvp.interactor.FavoritesGetter;
import io.mercury.androidcleanmvp.repository.CachingFavoriteRepo;
import io.mercury.androidcleanmvp.repository.FavoriteReadThroughCache;
import io.mercury.androidcleanmvp.repository.FavoriteRepo;
import io.mercury.androidcleanmvp.repository.InMemoryFavoriteRepo;
import io.mercury.androidcleanmvp.repository.MockRemoteFavoriteRepo;
import io.mercury.androidcleanmvp.repository.SlowFavoriteRepoWrapper;

public class Container {

    private static Container instance;
    private FavoriteAdder favoriteAdder;
    private FavoriteRepo favoriteRepo;
    private FavoritesGetter favoritesGetter;
    private FavoriteRemover favoritesRemover;

    private Container() {}

    public static Container getInstance() {
        if (instance == null) {
            instance = new Container();
        }
        return instance;
    }

    public FavoriteAdder getFavoriteAdder() {
        if (favoriteAdder == null) {
            favoriteAdder = new FavoriteAdder(getFavoriteRepo());
        }
        return favoriteAdder;
    }

    public FavoritesGetter getFavoritesGetter() {
        if (favoritesGetter == null) {
            favoritesGetter = new FavoritesGetter(getFavoriteRepo());
        }
        return favoritesGetter;
    }

    public FavoriteRemover getFavoritesRemover() {
        if (favoritesRemover == null) {
            favoritesRemover = new FavoriteRemover(getFavoriteRepo());
        }
        return favoritesRemover;
    }

    public FavoriteRepo getFavoriteRepo() {
        if (favoriteRepo == null) {
            CachingFavoriteRepo inMemoryRepo = new InMemoryFavoriteRepo();
            FavoriteRepo remoteRepo = new SlowFavoriteRepoWrapper(new MockRemoteFavoriteRepo(), 1000);
            favoriteRepo = new FavoriteReadThroughCache(inMemoryRepo, remoteRepo);
        }
        return favoriteRepo;
    }
}
