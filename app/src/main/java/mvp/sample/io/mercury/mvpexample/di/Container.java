package mvp.sample.io.mercury.mvpexample.di;

import mvp.sample.io.mercury.mvpexample.entity.Favorite;
import mvp.sample.io.mercury.mvpexample.interactor.FavoriteAdder;
import mvp.sample.io.mercury.mvpexample.interactor.FavoriteRemover;
import mvp.sample.io.mercury.mvpexample.interactor.FavoritesGetter;
import mvp.sample.io.mercury.mvpexample.repository.CachingFavoriteRepo;
import mvp.sample.io.mercury.mvpexample.repository.FavoriteReadThroughCache;
import mvp.sample.io.mercury.mvpexample.repository.FavoriteRepo;
import mvp.sample.io.mercury.mvpexample.repository.InMemoryFavoriteRepo;
import mvp.sample.io.mercury.mvpexample.repository.MockRemoteFavoriteRepo;
import mvp.sample.io.mercury.mvpexample.repository.SlowFavoriteRepoWrapper;

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
