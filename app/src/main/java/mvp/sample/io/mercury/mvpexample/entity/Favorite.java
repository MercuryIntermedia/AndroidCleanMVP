package mvp.sample.io.mercury.mvpexample.entity;

public class Favorite {
    private final int id;

    public Favorite(int i) {
        id = i;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Favorite favorite = (Favorite) o;

        return id == favorite.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
