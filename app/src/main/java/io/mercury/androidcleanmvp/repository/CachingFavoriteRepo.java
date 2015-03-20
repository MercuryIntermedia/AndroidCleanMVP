package io.mercury.androidcleanmvp.repository;

import java.util.Collection;

import io.mercury.androidcleanmvp.entity.Favorite;

public interface CachingFavoriteRepo extends FavoriteRepo, CachingRepo {
    public void cache(Collection<Favorite> favorites);
}
