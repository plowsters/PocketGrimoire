package com.example.pocketgrimoire.database.seeding;

import androidx.room.RoomDatabase;

import com.example.pocketgrimoire.database.SpellsDAO;
import com.example.pocketgrimoire.database.entities.Spells;
import com.example.pocketgrimoire.network.loaders.SpellsNetworkLoader;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public final class SpellsSeeder {
    private final SpellsNetworkLoader loader;
    private final SpellsDAO spellsDao;
    private final RoomDatabase db;

    public SpellsSeeder(SpellsNetworkLoader loader, SpellsDAO spellsDao, RoomDatabase db) {
        this.loader = loader; this.spellsDao = spellsDao; this.db = db;
    }

    /**
     * Populate Spells table (name, level, school, availableToClass).
     * */
    public Completable seed() {
        Single<List<Spells>> spellsSingle = loader.loadAllSpells();

        Completable persist = spellsSingle
                .flattenAsFlowable(list -> list)
                .concatMapCompletable(spell ->
                        spellsDao.updateByName(
                                        spell.getName(),
                                        spell.getLevel(),
                                        spell.getSchool(),
                                        spell.getAvailableToClass()
                                )
                                .subscribeOn(Schedulers.io())
                                .flatMapCompletable(rows ->
                                        rows > 0
                                                ? Completable.complete()
                                                : spellsDao.insert(spell)
                                                .subscribeOn(Schedulers.io())
                                )
                );

        return Completable.fromAction(() ->
                db.runInTransaction(() -> persist.blockingAwait())
        ).subscribeOn(Schedulers.io());
    }
}
