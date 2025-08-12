package com.example.pocketgrimoire.database.seeding;

import androidx.room.RoomDatabase;

import com.example.pocketgrimoire.database.AbilitiesDAO;
import com.example.pocketgrimoire.database.entities.Abilities;
import com.example.pocketgrimoire.network.loaders.AbilitiesNetworkLoader;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Seeds Abilities (Traits & Features).
 *
 * Strategy:
 * - Fetch the full, de-duplicated ability roster + availability (class/race) via the loader
 *   BEFORE opening a DB transaction.
 * - Inside one Room transaction:
 *   1) Upsert the row identity with name/flag (traitOrFeat).
 *   2) Update availability-for-class OR availability-for-race, based on the flag.
 *   (If the identity update affects 0 rows, insert; then write availability.)
 */
public final class AbilitiesSeeder {
    private final AbilitiesNetworkLoader loader;
    private final AbilitiesDAO abilitiesDao;
    private final RoomDatabase db;
    private final Map<String, String> classes;
    private final Map<String, String> races;

    public AbilitiesSeeder(AbilitiesNetworkLoader loader,
                           AbilitiesDAO abilitiesDao,
                           RoomDatabase db,
                           Map<String, String> classes,
                           Map<String, String> races) {
        this.loader = loader;
        this.abilitiesDao = abilitiesDao;
        this.db = db;
        this.classes = classes;
        this.races = races;
    }

    /**
     * Fetch abilities and upsert them atomically, including availability.
     *
     * @return Completable that completes when seeding finishes
     */
    public Completable seed() {
        // 1) Network first: build a full list with flags + availability (no DB yet).
        return loader.fetchAll(classes, races) // Single<List<Abilities>>
                // 2) Do DB-only work inside one transaction and block until done.
                .flatMapCompletable(all ->
                        Completable.fromAction(() ->
                                db.runInTransaction(() -> {
                                    Flowable.fromIterable(all)
                                            .concatMapCompletable(a -> {
                                                // Step A: ensure the row exists with the correct name/flag
                                                Completable upsertIdentity =
                                                        abilitiesDao.updateNameAndFlagByName(
                                                                        a.getName(), // search by same name
                                                                        a.getName(), // keep/normalize name
                                                                        a.isTraitOrFeat()
                                                                )
                                                                .flatMapCompletable(rows ->
                                                                        rows > 0
                                                                                ? Completable.complete()
                                                                                : abilitiesDao.insert(a)
                                                                );

                                                // Step B: write availability by type
                                                Completable writeAvailability =
                                                        a.isTraitOrFeat()
                                                                // traitOrFeat == true -> TRAIT -> availableToRace
                                                                ? abilitiesDao
                                                                .updateAvailableToRace(a.getName(), a.getAvailableToRace())
                                                                .ignoreElement()
                                                                // traitOrFeat == false -> FEATURE -> availableToClass
                                                                : abilitiesDao
                                                                .updateAvailableToClass(a.getName(), a.getAvailableToClass())
                                                                .ignoreElement();

                                                return upsertIdentity.andThen(writeAvailability);
                                            })
                                            .blockingAwait();
                                })
                        )
                )
                .subscribeOn(Schedulers.io());
    }
}
