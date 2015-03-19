package mvp.sample.io.mercury.mvpexample.interactor;

import mvp.sample.io.mercury.mvpexample.entity.Favorite;
import mvp.sample.io.mercury.mvpexample.repository.FavoriteRepo;

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
