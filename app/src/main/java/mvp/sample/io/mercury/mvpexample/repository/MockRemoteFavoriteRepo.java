package mvp.sample.io.mercury.mvpexample.repository;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import mvp.sample.io.mercury.mvpexample.entity.Favorite;

public class MockRemoteFavoriteRepo implements FavoriteRepo {

    private Set<Favorite> favorites = new LinkedHashSet<>();

    @Override
    public void put(Favorite favorite) {
        favorites.add(favorite);
    }

    @Override
    public FavoritesResponse get(FavoritesRequest favoritesRequest) {
        return new FavoritesResponse(Collections.unmodifiableCollection(favorites), false);
    }

    @Override
    public void remove(Favorite favorite) {
        favorites.remove(favorite);
    }

    @Override
    public boolean exists(Favorite favorite) {
        return favorites.contains(favorite);
    }
}
