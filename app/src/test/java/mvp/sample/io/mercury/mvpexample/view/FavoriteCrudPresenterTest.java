package mvp.sample.io.mercury.mvpexample.view;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executor;

import mvp.sample.io.mercury.mvpexample.concurrent.SynchronousExecutor;
import mvp.sample.io.mercury.mvpexample.entity.Favorite;
import mvp.sample.io.mercury.mvpexample.interactor.FavoriteAdder;
import mvp.sample.io.mercury.mvpexample.interactor.FavoriteRemover;
import mvp.sample.io.mercury.mvpexample.interactor.FavoritesGetter;
import mvp.sample.io.mercury.mvpexample.repository.FavoriteRepo;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FavoriteCrudPresenterTest {

    private Scheduler syncScheduler = Schedulers.immediate();
    private Executor syncExecutor = new SynchronousExecutor();

    @Mock private FavoriteCrudPresenter.FavoriteCrudView mockView;
    @Mock private FavoritesGetter mockGetter;
    @Mock private FavoriteAdder mockAdder;
    @Mock private FavoriteRemover mockRemover;

    private final Favorite fav1 = new Favorite(1);
    private final Favorite fav2 = new Favorite(2);

    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPresentWithNoCachedData() throws Exception {
        Collection<Favorite> favorites = new ArrayList<>();

        when(mockGetter.execute(Matchers.any(FavoriteRepo.FavoritesRequest.class))).thenReturn(new FavoriteRepo.FavoritesResponse(favorites, false));

        FavoriteCrudPresenter presenter = new FavoriteCrudPresenter(null, mockGetter, null, syncScheduler, syncScheduler, syncExecutor, syncExecutor);
        presenter.attachView(mockView);
        presenter.present();

        verify(mockView).disableAddControls();
        verify(mockView).showLoading();
        verify(mockGetter).execute(Matchers.any(FavoriteRepo.FavoritesRequest.class));
        verify(mockView).enableAddControls();
        verify(mockView).loadFavorites(favorites);
        verify(mockView).hideLoading();
    }

    @Test
    public void testPresentWithCachedData() throws Exception {
        Collection<Favorite> cachedFavorites = new ArrayList<>();
        cachedFavorites.add(fav1);

        Collection<Favorite> freshFavorites = new ArrayList<>();
        freshFavorites.add(fav1);
        freshFavorites.add(fav2);

        FavoriteRepo.FavoritesRequest favReqDontSkipCache = new FavoriteRepo.FavoritesRequest(false);
        FavoriteRepo.FavoritesRequest favReqSkipCache = new FavoriteRepo.FavoritesRequest(true);

        when(mockGetter.execute(favReqDontSkipCache)).thenReturn(new FavoriteRepo.FavoritesResponse(cachedFavorites, true));
        when(mockGetter.execute(favReqSkipCache)).thenReturn(new FavoriteRepo.FavoritesResponse(freshFavorites, false));

        FavoriteCrudPresenter presenter = new FavoriteCrudPresenter(null, mockGetter, null, syncScheduler, syncScheduler, syncExecutor, syncExecutor);
        presenter.attachView(mockView);
        presenter.present();

        verify(mockView).disableAddControls();
        verify(mockView).showLoading();
        verify(mockGetter).execute(favReqDontSkipCache);
        verify(mockView).loadFavorites(cachedFavorites);
        verify(mockGetter).execute(favReqSkipCache);
        verify(mockView).enableAddControls();
        verify(mockView).loadFavorites(freshFavorites);
        verify(mockView).hideLoading();
    }

    @Test
    public void testAddFavorite() throws Exception {
        Collection<Favorite> favoritesAfterAdd = new ArrayList<>();
        favoritesAfterAdd.add(fav1);

        when(mockAdder.execute(fav1)).thenReturn(new FavoriteAdder.Response(fav1, false));
        when(mockGetter.execute(Matchers.any(FavoriteRepo.FavoritesRequest.class))).thenReturn(new FavoriteRepo.FavoritesResponse(favoritesAfterAdd, false));

        FavoriteCrudPresenter presenter = new FavoriteCrudPresenter(mockAdder, mockGetter, null, syncScheduler, syncScheduler, syncExecutor, syncExecutor);
        presenter.attachView(mockView);
        presenter.addFavorite(fav1);

        verify(mockView).disableAddControls();
        verify(mockView).showLoading();
        verify(mockAdder).execute(fav1);
        verify(mockView).add(fav1);
        verify(mockView).enableAddControls();
        verify(mockView).loadFavorites(favoritesAfterAdd);
        verify(mockView).hideLoading();
        verify(mockView).notifyAddSuccessful(fav1);
    }

    @Test
    public void testAddFavoriteThatAlreadyExisted() throws Exception {
        Collection<Favorite> favoritesAfterAdd = new ArrayList<>();
        favoritesAfterAdd.add(fav1);

        when(mockAdder.execute(fav1)).thenReturn(new FavoriteAdder.Response(fav1, true));
        when(mockGetter.execute(Matchers.any(FavoriteRepo.FavoritesRequest.class))).thenReturn(new FavoriteRepo.FavoritesResponse(favoritesAfterAdd, false));

        FavoriteCrudPresenter presenter = new FavoriteCrudPresenter(mockAdder, mockGetter, null, syncScheduler, syncScheduler, syncExecutor, syncExecutor);
        presenter.attachView(mockView);
        presenter.addFavorite(fav1);

        verify(mockView).disableAddControls();
        verify(mockView).showLoading();
        verify(mockAdder).execute(fav1);
        verify(mockView).add(fav1);
        verify(mockView).enableAddControls();
        verify(mockView).loadFavorites(favoritesAfterAdd);
        verify(mockView).hideLoading();
        verify(mockView).notifyFavoriteAlreadyExists(fav1);
    }

    @Test
    public void testRemoveFavorite() throws Exception {
        Collection<Favorite> favoritesAfterAdd = new ArrayList<>();
        favoritesAfterAdd.add(fav1);

        when(mockRemover.execute(fav1)).thenReturn(fav1);
        when(mockGetter.execute(Matchers.any(FavoriteRepo.FavoritesRequest.class))).thenReturn(new FavoriteRepo.FavoritesResponse(favoritesAfterAdd, false));

        FavoriteCrudPresenter presenter = new FavoriteCrudPresenter(null, mockGetter, mockRemover, syncScheduler, syncScheduler, syncExecutor, syncExecutor);
        presenter.attachView(mockView);
        presenter.removeFavorite(fav1);

        verify(mockView).disableAddControls();
        verify(mockView).showLoading();
        verify(mockRemover).execute(fav1);
        verify(mockView).remove(fav1);
        verify(mockView).enableAddControls();
        verify(mockView).loadFavorites(favoritesAfterAdd);
        verify(mockView).hideLoading();
        verify(mockView).notifyRemoveSuccessful(fav1);
    }

}