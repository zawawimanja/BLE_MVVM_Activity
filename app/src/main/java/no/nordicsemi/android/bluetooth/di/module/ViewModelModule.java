package no.nordicsemi.android.bluetooth.di.module;



import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import no.nordicsemi.android.bluetooth.di.ViewModelKey;
import no.nordicsemi.android.bluetooth.factory.ViewModelFactory;
import no.nordicsemi.android.bluetooth.viewmodels.BlinkyViewModel;

@Module
public abstract class ViewModelModule {

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);


    /*
     * This method basically says
     * inject this object into a Map using the @IntoMap annotation,
     * with the  MovieListViewModel.class as key,
     * and a Provider that will build a MovieListViewModel
     * object.
     *
     * */

    @Binds
    @IntoMap
    @ViewModelKey(BlinkyViewModel.class)
    protected abstract ViewModel movieListViewModel(BlinkyViewModel moviesListViewModel);
}