package mvp.sample.io.mercury.mvpexample.interactor;

import mvp.sample.io.mercury.mvpexample.repository.FavoriteRepo;

public class FavoritesGetter {
    private final FavoriteRepo repo;

    public FavoritesGetter(FavoriteRepo repo) {
        this.repo = repo;
    }

    public FavoriteRepo.FavoritesResponse execute(FavoriteRepo.FavoritesRequest favoritesRequest) {
        return repo.get(favoritesRequest);
    }
}
