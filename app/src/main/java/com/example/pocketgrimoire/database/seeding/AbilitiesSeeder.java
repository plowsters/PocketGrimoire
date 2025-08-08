package com.example.pocketgrimoire.database.seeding;

import androidx.room.RoomDatabase;

import com.example.pocketgrimoire.database.AbilitiesDAO;
import com.example.pocketgrimoire.database.entities.Abilities;
import com.example.pocketgrimoire.network.loaders.AbilitiesNetworkLoader;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public final class AbilitiesSeeder {
    private final AbilitiesNetworkLoader loader;
    private final AbilitiesDAO abilitiesDao;
    private final RoomDatabase db;

    // Supply class/race maps (index -> display name).
    private final Map<String,String> classIndexToDisplay;
    private final Map<String,String> raceIndexToDisplay;

    public AbilitiesSeeder(AbilitiesNetworkLoader loader,
                           AbilitiesDAO abilitiesDao,
                           RoomDatabase db,
                           Map<String,String> classIndexToDisplay,
                           Map<String,String> raceIndexToDisplay) {
        this.loader = loader; this.abilitiesDao = abilitiesDao; this.db = db;
        this.classIndexToDisplay = classIndexToDisplay;
        this.raceIndexToDisplay = raceIndexToDisplay;
    }

    /** Seed base abilities, then enrich availability by class/race. */
    public Completable seed() {
        // Base rows (names + traitOrFeat flag, empty lists)
        Single<List<Abilities>> baseSingle = loader.loadBaseAbilities();

        // Persist base rows
        Completable persistBase = baseSingle
                .flattenAsFlowable(list -> list)
                .concatMapCompletable(ab ->
                        abilitiesDao.updateByNameAndFlag(
                                        ab.getName(),
                                        ab.isTraitOrFeat()
                                )
                                .subscribeOn(Schedulers.io())
                                .flatMapCompletable(rows ->
                                        rows > 0
                                                ? Completable.complete()
                                                : abilitiesDao.insert(ab)
                                                .subscribeOn(Schedulers.io())
                                )
                );

        // Add class and race availability
        Single<List<Abilities>> enrichedSingle = baseSingle
                .flatMap(base -> loader.enrichAvailability(base, classIndexToDisplay, raceIndexToDisplay));

        // Persist availability lists
        Completable persistAvail = enrichedSingle
                .flattenAsFlowable(list -> list)
                .concatMapCompletable(ab ->
                        abilitiesDao.updateAvailabilityByNameAndFlag(
                                        ab.getName(),
                                        ab.isTraitOrFeat(),
                                        ab.getAvailableToClass(),
                                        ab.getAvailableToRace()
                                )
                                .subscribeOn(Schedulers.io())
                                // If row didn’t exist (should not happen if base row step ran), insert full row
                                .flatMapCompletable(rows ->
                                        rows > 0
                                                ? Completable.complete()
                                                : abilitiesDao.insert(ab).subscribeOn(Schedulers.io())
                                )
                );

        // Wrap in a transaction to keep the table consistent on failure: base rows first, then availability
        return Completable.fromAction(() ->
                db.runInTransaction(() -> {
                    persistBase.blockingAwait();
                    persistAvail.blockingAwait();
                })
        ).subscribeOn(Schedulers.io());
    }
}
