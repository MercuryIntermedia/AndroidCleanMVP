package mvp.sample.io.mercury.mvpexample.repository;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import mvp.sample.io.mercury.mvpexample.entity.Favorite;

public class InMemoryFavoriteRepo implements CachingFavoriteRepo {
    private Set<Favorite> favorites = new LinkedHashSet<>();
    private boolean isStale;

    @Override
    public void cache(Collection<Favorite> favorites) {
        this.favorites = new LinkedHashSet<>(favorites);
        isStale = false;
    }

    @Override
    public FavoritesResponse get(FavoritesRequest favoritesRequest) {
        return new FavoritesResponse(Collections.unmodifiableCollection(favorites), true);
    }

    @Override
    public void put(Favorite favorite) {
        throw new RuntimeException("Not applicable");
    }

    @Override
    public void remove(Favorite favorite) {
        throw new RuntimeException("Not applicable");
    }

    @Override
    public boolean exists(Favorite favorite) {
        return favorites.contains(favorite);
    }

    @Override
    public boolean isStale() {
        return isStale;
    }

    @Override
    public void setIsStale() {
        isStale = true;
    }
}
