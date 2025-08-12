package com.example.pocketgrimoire.database.seeding;

import androidx.room.RoomDatabase;

import com.example.pocketgrimoire.database.ItemsDAO;
import com.example.pocketgrimoire.database.entities.Items;
import com.example.pocketgrimoire.network.loaders.ItemsNetworkLoader;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Seeds Items:
 * - Fetch network data first (outside any transaction)
 * - Inside one Room transaction, upsert each item by name:
 *     update(category, isEquippable) OR insert if no rows updated
 */
public final class ItemsSeeder {
    private final ItemsNetworkLoader loader;
    private final ItemsDAO itemsDao;
    private final RoomDatabase db;

    public ItemsSeeder(ItemsNetworkLoader loader, ItemsDAO itemsDao, RoomDatabase db) {
        this.loader = loader;
        this.itemsDao = itemsDao;
        this.db = db;
    }

    /**
     * Fetch all equipment and upsert them atomically.
     *
     * @return Completable that completes when seeding finishes
     */
    public Completable seed() {
        // 1) Do the network first, off the UI thread
        return loader.fetchAll() // Single<List<Items>>
                // 2) When we have the list, run the DB work inside a single transaction
                .flatMapCompletable(items ->
                        Completable.fromAction(() ->
                                db.runInTransaction(() -> {
                                    // IMPORTANT: Don't put subscribeOn(...) on DAO calls here
                                    // Execute on the current (transaction) thread and block until done
                                    Flowable.fromIterable(items)
                                            .concatMapCompletable(item ->
                                                    itemsDao.updateByName(
                                                                    item.getName(),
                                                                    item.getCategory(),
                                                                    item.isEquippable()
                                                            )
                                                            .flatMapCompletable(rows ->
                                                                    rows > 0
                                                                            ? Completable.complete()
                                                                            : itemsDao.insert(item)
                                                            )
                                            )
                                            .blockingAwait();
                                })
                        )
                )
                .subscribeOn(Schedulers.io());
    }
}
