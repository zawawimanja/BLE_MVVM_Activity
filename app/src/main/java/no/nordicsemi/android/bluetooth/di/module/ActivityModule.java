package no.nordicsemi.android.bluetooth.di.module;




import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import no.nordicsemi.android.bluetooth.view.BlinkyActivity;
import no.nordicsemi.android.bluetooth.view.Main2Activity;
import no.nordicsemi.android.bluetooth.view.Main3Activity;
import no.nordicsemi.android.bluetooth.view.MainActivity;

@Module
public abstract class ActivityModule {

    @ContributesAndroidInjector()
    abstract BlinkyActivity contributeMainActivity();

    @ContributesAndroidInjector()
    abstract MainActivity contributeMainActivity1();

    @ContributesAndroidInjector()
    abstract Main2Activity contributeMainActivity2();

    @ContributesAndroidInjector()
    abstract Main3Activity contributeMainActivity3();
}