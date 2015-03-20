package mvp.sample.io.mercury.mvpexample.repository;

import mvp.sample.io.mercury.mvpexample.entity.Favorite;

public class FavoriteReadThroughCache implements FavoriteRepo {

    private final CachingFavoriteRepo firstLevelRepo;
    private final FavoriteRepo secondLevelRepo;

    public FavoriteReadThroughCache(CachingFavoriteRepo firstLevelRepo, FavoriteRepo secondLevelRepo) {
        this.firstLevelRepo = firstLevelRepo;
        this.secondLevelRepo = secondLevelRepo;
    }

    @Override
    public void put(Favorite favorite) {
        firstLevelRepo.setIsStale();
        secondLevelRepo.put(favorite);
    }

    @Override
    public FavoritesResponse get(FavoritesRequest favoritesRequest) {
        FavoritesResponse retVal = null;
        if (!favoritesRequest.skipCache() && !firstLevelRepo.isStale()) {
            retVal = firstLevelRepo.get(favoritesRequest);
        }

        if (retVal == null) {
            retVal = secondLevelRepo.get(favoritesRequest);
            firstLevelRepo.cache(retVal.getFavorites());
        }
        return retVal;
    }

    @Override
    public void remove(Favorite favorite) {
        firstLevelRepo.setIsStale();
        secondLevelRepo.remove(favorite);
    }

    @Override
    public boolean exists(Favorite favorite) {
        if (!firstLevelRepo.isStale()) {
            return firstLevelRepo.exists(favorite);
        }
        return secondLevelRepo.exists(favorite);
    }
}
