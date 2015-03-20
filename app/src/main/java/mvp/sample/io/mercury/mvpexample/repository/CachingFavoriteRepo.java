package mvp.sample.io.mercury.mvpexample.repository;

import java.util.Collection;

import mvp.sample.io.mercury.mvpexample.entity.Favorite;

public interface CachingFavoriteRepo extends FavoriteRepo, CachingRepo {
    public void cache(Collection<Favorite> favorites);
}
