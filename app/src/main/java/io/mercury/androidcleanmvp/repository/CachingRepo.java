package io.mercury.androidcleanmvp.repository;

public interface CachingRepo {
    public boolean isStale();

    public void setIsStale();
}
