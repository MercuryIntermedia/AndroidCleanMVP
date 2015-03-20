package io.mercury.androidcleanmvp.interactor;

import io.mercury.androidcleanmvp.repository.FavoriteRepo;

public class FavoritesGetter {
    private final FavoriteRepo repo;

    public FavoritesGetter(FavoriteRepo repo) {
        this.repo = repo;
    }

    public FavoriteRepo.FavoritesResponse execute(FavoriteRepo.FavoritesRequest favoritesRequest) {
        return repo.get(favoritesRequest);
    }
}
