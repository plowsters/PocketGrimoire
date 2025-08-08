package com.example.pocketgrimoire.database.seeding;

import androidx.room.RoomDatabase;

import com.example.pocketgrimoire.database.ItemsDAO;
import com.example.pocketgrimoire.database.entities.Items;
import com.example.pocketgrimoire.network.loaders.ItemsNetworkLoader;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public final class ItemsSeeder {
    private final ItemsNetworkLoader loader;
    private final ItemsDAO itemsDao;
    private final RoomDatabase db;

    public ItemsSeeder(ItemsNetworkLoader loader, ItemsDAO itemsDao, RoomDatabase db) {
        this.loader = loader; this.itemsDao = itemsDao; this.db = db;
    }

    /**
     * Populate Items table from the API (idempotent: update-by-name then insert).
     * */
    public Completable seed() {
        // Build category map, load and map items
        Single<List<Items>> itemsSingle = loader.buildEquipmentIndexToCategoryMap()
                .flatMap(loader::loadAllItemsWithCategory);

        // Persist with update-then-insert
        Completable persist = itemsSingle
                .flattenAsFlowable(list -> list)
                .concatMapCompletable(item ->
                        itemsDao.updateByName(item.getName(), item.getCategory(), item.isEquippable())
                                .subscribeOn(Schedulers.io())
                                .flatMapCompletable(rows ->
                                        rows > 0
                                                ? Completable.complete()
                                                : itemsDao.insert(item)
                                                .subscribeOn(Schedulers.io())
                                )
                );

        // Wrap in a Room transaction to keep the table consistent on failure
        return Completable.fromAction(() ->
                db.runInTransaction(() -> persist.blockingAwait())
        ).subscribeOn(Schedulers.io());
    }
}
