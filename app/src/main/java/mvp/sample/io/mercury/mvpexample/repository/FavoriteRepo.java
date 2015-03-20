package mvp.sample.io.mercury.mvpexample.repository;

import java.util.Collection;

import mvp.sample.io.mercury.mvpexample.entity.Favorite;

public interface FavoriteRepo {
    void put(Favorite favorite);

    FavoritesResponse get(FavoritesRequest favoritesRequest);

    void remove(Favorite favorite);

    boolean exists(Favorite favorite);

    public static class FavoritesRequest {

        private final boolean skipCache;

        public FavoritesRequest(boolean skipCache) {
            this.skipCache = skipCache;
        }

        public boolean skipCache() {
            return skipCache;
        }
    }

    public static class FavoritesResponse {
        private Collection<Favorite> favorites;
        private boolean isCachedData;

        public FavoritesResponse(Collection<Favorite> favorites, boolean isCachedData) {
            this.favorites = favorites;
            this.isCachedData = isCachedData;
        }

        public boolean isCachedData() {
            return isCachedData;
        }

        public Collection<Favorite> getFavorites() {
            return favorites;
        }
    }
}
