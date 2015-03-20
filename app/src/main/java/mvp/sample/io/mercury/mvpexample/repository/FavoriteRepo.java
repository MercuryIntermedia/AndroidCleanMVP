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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FavoritesRequest that = (FavoritesRequest) o;

            if (skipCache != that.skipCache) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return (skipCache ? 1 : 0);
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
