package com.example.pocketgrimoire.database.seeding;

import com.example.pocketgrimoire.database.SpellsDAO;
import com.example.pocketgrimoire.network.loaders.SpellsNetworkLoader;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public final class SpellsSeeder {
    private final SpellsNetworkLoader loader;
    private final SpellsDAO spellsDao;

    public SpellsSeeder(SpellsNetworkLoader loader, SpellsDAO spellsDao) {
        this.loader = loader;
        this.spellsDao = spellsDao;
    }

    public Completable seed() {
        return loader.fetchAll() // Single<List<Spells>>
                .flattenAsFlowable(list -> list)
                .onBackpressureBuffer()
                .concatMapCompletable(spell ->
                        spellsDao.insert(spell)
                                .subscribeOn(Schedulers.io())
                )
                .subscribeOn(Schedulers.io());
    }
}
