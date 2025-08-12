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
        return loader.fetchAll() // Single<List<Items>> from the network
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(items ->
                        Completable.fromAction(() ->
                                db.runInTransaction(() -> {
                                    for (Items item : items) {
                                        int updated = itemsDao.updateByName(
                                                item.getName(), item.getCategory(), item.isEquippable());
                                        if (updated == 0) {
                                            itemsDao.insertSync(item); // same thread, same connection
                                        }
                                    }
                                })
                        )
                );
    }
}
