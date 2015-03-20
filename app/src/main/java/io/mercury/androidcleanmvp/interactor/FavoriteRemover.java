package io.mercury.androidcleanmvp.interactor;

import io.mercury.androidcleanmvp.entity.Favorite;
import io.mercury.androidcleanmvp.repository.FavoriteRepo;

public class FavoriteRemover {
    private final FavoriteRepo repo;

    public FavoriteRemover(FavoriteRepo repo) {
        this.repo = repo;
    }

    public Favorite execute(Favorite favorite) {
        repo.remove(favorite);
        return favorite;
    }
}
