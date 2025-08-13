package com.example.pocketgrimoire.database.seeding;

import com.example.pocketgrimoire.database.AbilitiesDAO;
import com.example.pocketgrimoire.network.loaders.AbilitiesNetworkLoader;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public final class AbilitiesSeeder {
    private final AbilitiesNetworkLoader loader;
    private final AbilitiesDAO abilitiesDao;

    public AbilitiesSeeder(AbilitiesNetworkLoader loader, AbilitiesDAO abilitiesDao) {
        this.loader = loader;
        this.abilitiesDao = abilitiesDao;
    }

    /**
     * Fetches a fully populated list of abilities from the loader and inserts
     * them into the database. The DAO's OnConflictStrategy.IGNORE handles any
     * potential duplicates gracefully.
     */
    public Completable seed() {
        return loader.fetchAll() // Single<List<Abilities>>
                .flattenAsFlowable(list -> list)
                .onBackpressureBuffer()
                // Serialize DB writes. With @Insert(IGNORE) on DAO, dupes are quietly ignored.
                .concatMapCompletable(ability ->
                        abilitiesDao.insert(ability)
                                .subscribeOn(Schedulers.io())
                )
                .subscribeOn(Schedulers.io());
    }
}
