package mvp.sample.io.mercury.mvpexample.repository;

import java.util.Collection;

import mvp.sample.io.mercury.mvpexample.entity.Favorite;

public interface FavoriteRepo {
    void put(Favorite favorite);

    Collection<Favorite> get();

    void remove(Favorite favorite);

    boolean exists(Favorite favorite);
}
