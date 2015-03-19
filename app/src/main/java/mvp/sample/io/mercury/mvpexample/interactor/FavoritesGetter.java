package mvp.sample.io.mercury.mvpexample.interactor;

import java.util.Collection;

import mvp.sample.io.mercury.mvpexample.entity.Favorite;
import mvp.sample.io.mercury.mvpexample.repository.FavoriteRepo;

public class FavoritesGetter {
    private final FavoriteRepo repo;

    public FavoritesGetter(FavoriteRepo repo) {
        this.repo = repo;
    }

    public Collection<Favorite> execute() {
        return repo.get();
    }
}
