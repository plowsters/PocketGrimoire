package com.example.pocketgrimoire.database.seeding;

import androidx.room.RoomDatabase;

import com.example.pocketgrimoire.database.SpellsDAO;
import com.example.pocketgrimoire.database.entities.Spells;
import com.example.pocketgrimoire.network.loaders.SpellsNetworkLoader;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Seeds Spells:
 * - Fetch network data first (outside any transaction)
 * - Inside one Room transaction, upsert each spell by name:
 *     update(level, school, availableToClass) OR insert if no rows updated
 */
public final class SpellsSeeder {
    private final SpellsNetworkLoader loader;
    private final SpellsDAO spellsDao;
    private final RoomDatabase db;
    private final Map<String, String> classes;

    public SpellsSeeder(SpellsNetworkLoader loader,
                        SpellsDAO spellsDao,
                        RoomDatabase db,
                        Map<String, String> classes) {
        this.loader = loader;
        this.spellsDao = spellsDao;
        this.db = db;
        this.classes = classes;
    }

    /**
     * Fetch all spells and upsert them atomically.
     *
     * @return Completable that completes when seeding finishes
     */
    public Completable seed() {
        // 1) Network fetch happens BEFORE opening a DB transaction.
        return loader.fetchAll(classes) // Single<List<Spells>>
                // 2) Upsert all rows inside one transaction (DB-only work).
                .flatMapCompletable(spells ->
                        Completable.fromAction(() ->
                                db.runInTransaction(() -> {
                                    Flowable.fromIterable(spells)
                                            .concatMapCompletable(spell ->
                                                    spellsDao.updateByName(
                                                                    spell.getName(),
                                                                    spell.getLevel(),
                                                                    spell.getSchool(),
                                                                    spell.getAvailableToClass()
                                                            )
                                                            .flatMapCompletable(rows ->
                                                                    rows > 0
                                                                            ? Completable.complete()
                                                                            : spellsDao.insert(spell)
                                                            )
                                            )
                                            .blockingAwait();
                                })
                        )
                )
                .subscribeOn(Schedulers.io());
    }
}
