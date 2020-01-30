package no.nordicsemi.android.bluetooth.di.component;

import android.app.Application;



import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;
import no.nordicsemi.android.bluetooth.AppController;
import no.nordicsemi.android.bluetooth.di.module.ActivityModule;
import no.nordicsemi.android.bluetooth.di.module.DbModule;
import no.nordicsemi.android.bluetooth.di.module.ViewModelModule;


/*
 * We mark this interface with the @Component annotation.
 * And we define all the modules that can be injected.
 * Note that we provide AndroidSupportInjectionModule.class
 * here. This class was not created by us.
 * It is an internal class in Dagger 2.10.
 * Provides our activities and fragments with given module.
 * */
@Component(modules = {

                DbModule.class,
                ViewModelModule.class,
                ActivityModule.class,
                AndroidSupportInjectionModule.class})
@Singleton
public interface AppComponent {


    /* We will call this builder interface from our custom Application class.
     * This will set our application object to the AppComponent.
     * So inside the AppComponent the application instance is available.
     * So this application instance can be accessed by our modules
     * such as ApiModule when needed
     * */
    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }


    /*
     * This is our custom Application class
     * */
    void inject(AppController appController);
}
