package com.example.pocketgrimoire.database.seeding;

import com.example.pocketgrimoire.database.AbilitiesDAO;
import com.example.pocketgrimoire.database.entities.Abilities;
import com.example.pocketgrimoire.network.loaders.AbilitiesNetworkLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public final class AbilitiesSeeder {
    private final AbilitiesNetworkLoader loader;
    private final AbilitiesDAO abilitiesDao;

    public AbilitiesSeeder(AbilitiesNetworkLoader loader, AbilitiesDAO abilitiesDao) {
        this.loader = loader;
        this.abilitiesDao = abilitiesDao;
    }

    public Completable seed() {
        return loader.fetchAll() // Single<List<Abilities>> (features + traits)
                // Merge duplicates by (name + isTrait) so DB gets one row per ability
                .map(list -> {
                    Map<String, Abilities> byKey = new LinkedHashMap<>();
                    for (Abilities a : list) {
                        if (a == null || a.getName() == null) continue;

                        String key = a.getName() + "|" + (a.isTraitOrFeat() ? "T" : "F");
                        Abilities existing = byKey.get(key);

                        if (existing == null) {
                            // Normalize to new instance so we can safely mutate during merge
                            Abilities copy = new Abilities();
                            copy.setName(a.getName());
                            copy.setTraitOrFeat(a.isTraitOrFeat());
                            copy.setAvailableToClass(dedupePreserveOrder(a.getAvailableToClass()));
                            copy.setAvailableToRace(dedupePreserveOrder(a.getAvailableToRace()));
                            byKey.put(key, copy);
                        } else {
                            // Union lists (preserve order, no duplicates)
                            if (a.getAvailableToClass() != null && !a.getAvailableToClass().isEmpty()) {
                                List<String> merged = union(existing.getAvailableToClass(), a.getAvailableToClass());
                                existing.setAvailableToClass(merged);
                            }
                            if (a.getAvailableToRace() != null && !a.getAvailableToRace().isEmpty()) {
                                List<String> merged = union(existing.getAvailableToRace(), a.getAvailableToRace());
                                existing.setAvailableToRace(merged);
                            }
                        }
                    }
                    return new ArrayList<>(byKey.values());
                })
                .flattenAsFlowable(list -> list)
                .onBackpressureBuffer()
                // Serialize DB writes. With @Insert(IGNORE) on DAO, dupes are quietly ignored.
                .concatMapCompletable(ability ->
                        abilitiesDao.insert(ability)
                                .subscribeOn(Schedulers.io())
                )
                .subscribeOn(Schedulers.io());
    }

    private static List<String> union(List<String> a, List<String> b) {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        if (a != null) set.addAll(a);
        if (b != null) set.addAll(b);
        return new ArrayList<>(set);
    }

    private static List<String> dedupePreserveOrder(List<String> in) {
        if (in == null) return new ArrayList<>();
        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (String s : in) {
            if (s == null) continue;
            String t = s.trim().replaceAll("\\s+", " ");
            if (!t.isEmpty()) set.add(t);
        }
        return new ArrayList<>(set);
    }
}
