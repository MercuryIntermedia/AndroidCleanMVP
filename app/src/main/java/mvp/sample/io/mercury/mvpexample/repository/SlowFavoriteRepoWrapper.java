package mvp.sample.io.mercury.mvpexample.repository;

import java.util.Collection;

import mvp.sample.io.mercury.mvpexample.entity.Favorite;

public class SlowFavoriteRepoWrapper implements FavoriteRepo {
    final FavoriteRepo wrappedRepo;
    private final long delayInMillis;

    public SlowFavoriteRepoWrapper(FavoriteRepo wrappedRepo, long delayInMillis) {
        this.wrappedRepo = wrappedRepo;
        this.delayInMillis = delayInMillis;
    }

    @Override
    public void put(Favorite favorite) {
        try {
            Thread.sleep(delayInMillis);
            wrappedRepo.put(favorite);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FavoritesResponse get(FavoritesRequest favoritesRequest) {
        try {
            Thread.sleep(delayInMillis);
            return wrappedRepo.get(favoritesRequest);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(Favorite favorite) {
        try {
            Thread.sleep(delayInMillis);
            wrappedRepo.remove(favorite);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(Favorite favorite) {
        try {
            Thread.sleep(delayInMillis);
            return wrappedRepo.exists(favorite);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
