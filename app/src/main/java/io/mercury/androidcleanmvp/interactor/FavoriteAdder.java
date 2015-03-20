package io.mercury.androidcleanmvp.interactor;

import io.mercury.androidcleanmvp.entity.Favorite;
import io.mercury.androidcleanmvp.repository.FavoriteRepo;

public class FavoriteAdder {

    private final FavoriteRepo repo;

    public FavoriteAdder(FavoriteRepo repo) {
        this.repo = repo;
    }

    public Response execute(Favorite favorite) {
        // Application specific logic regarding adding a favorite that was already added
        boolean alreadyExisted = repo.exists(favorite);
        if (!alreadyExisted) {
            repo.put(favorite);
        }

        return new Response(favorite, alreadyExisted);
    }

    public static class Response {
        private final Favorite favorite;

        private final boolean alreadyExisted;

        public Response(Favorite favorite, boolean alreadyExisted) {
            this.favorite = favorite;
            this.alreadyExisted = alreadyExisted;
        }

        public boolean alreadyExisted() {
            return alreadyExisted;
        }

        public Favorite getFavorite() {
            return favorite;
        }
    }
}
