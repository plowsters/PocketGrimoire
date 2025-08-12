package com.example.pocketgrimoire.database.seeding;

import com.example.pocketgrimoire.database.ItemsDAO;
import com.example.pocketgrimoire.network.loaders.ItemsNetworkLoader;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public final class ItemsSeeder {
    private final ItemsNetworkLoader loader;
    private final ItemsDAO itemsDao;

    public ItemsSeeder(ItemsNetworkLoader loader, ItemsDAO itemsDao) {
        this.loader = loader;
        this.itemsDao = itemsDao;
    }

    public Completable seed() {
        return loader.fetchAll() // Single<List<Items>>
                .flattenAsFlowable(list -> list)
                // Avoid backpressure issues if the loader emits a large list
                .onBackpressureBuffer()
                // Serialize DB writes to avoid connection pool contention
                .concatMapCompletable(item ->
                        itemsDao.insert(item)
                                .subscribeOn(Schedulers.io())
                )
                .subscribeOn(Schedulers.io());
    }
}