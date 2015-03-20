package mvp.sample.io.mercury.mvpexample.repository;

public interface CachingRepo {
    public boolean isStale();

    public void setIsStale();
}
