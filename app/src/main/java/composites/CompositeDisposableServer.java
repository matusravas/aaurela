package composites;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Trieda, ktora precistuje (naplna) {@link CompositeDisposable} kontajner naplneny objektami triedy {@link Disposable} pri serverovych operaciach.
 */
public class CompositeDisposableServer {
    private static CompositeDisposable compositeDisposable;

    /**
     * Pridava do kontajneru {@link CompositeDisposable} jednotlive objekty {@link Disposable}.
     *
     * @param disposable objekt disposable
     */
    public static void add(Disposable disposable) {
        getCompositeDisposable().add(disposable);
    }

    /**
     * Metoda, ktora vycisti {@link CompositeDisposable} kontajner.
     */
    public static void clear() {
        getCompositeDisposable().dispose();
    }

    /**
     * Metoda, ktora vracia instanciu triedy {@link CompositeDisposableServer}.
     * Vyuzitie navrhoveho vzoru Singleton.
     *
     * @return instancia triedy {@link CompositeDisposableServer}
     */
    private static CompositeDisposable getCompositeDisposable() {
        if (compositeDisposable == null || compositeDisposable.size() == 0) {
            compositeDisposable = new CompositeDisposable();
        }
        return compositeDisposable;
    }

    private CompositeDisposableServer() {
    }
}
