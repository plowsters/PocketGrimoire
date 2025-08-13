package com.example.pocketgrimoire.database.remote;

import com.example.pocketgrimoire.database.AbilitiesDAO;
import com.example.pocketgrimoire.database.ItemsDAO;
import com.example.pocketgrimoire.database.SpellsDAO;
import com.example.pocketgrimoire.database.entities.Abilities;
import com.example.pocketgrimoire.database.entities.CharacterAbilities;
import com.example.pocketgrimoire.database.entities.CharacterItems;
import com.example.pocketgrimoire.database.entities.CharacterSpells;
import com.example.pocketgrimoire.database.entities.Items;
import com.example.pocketgrimoire.database.entities.Spells;
import com.example.pocketgrimoire.database.remote.DndApiService;
import com.example.pocketgrimoire.database.remote.dto.ApiRef;
import com.example.pocketgrimoire.database.remote.dto.ClassRequestDto;
import com.example.pocketgrimoire.database.remote.dto.OptionDto;
import com.example.pocketgrimoire.database.remote.dto.ResourceRefDto;
import com.example.pocketgrimoire.database.remote.dto.StartingEquipmentDto;
import com.example.pocketgrimoire.database.remote.dto.StartingEquipmentOptionDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

/**
 * A service class to handle fetching and processing on-demand data for a specific character,
 * such as starting equipment, spells, and abilities upon creation and level-up.
 */
public final class CharacterDataService {
    private final DndApiService api;
    private final ItemsDAO itemsDao;
    private final SpellsDAO spellsDao;
    private final AbilitiesDAO abilitiesDao;
    private final Random random = new Random();

    public CharacterDataService(DndApiService api, ItemsDAO itemsDao, SpellsDAO spellsDao, AbilitiesDAO abilitiesDao) {
        this.api = api;
        this.itemsDao = itemsDao;
        this.spellsDao = spellsDao;
        this.abilitiesDao = abilitiesDao;
    }

    public Single<List<CharacterItems>> fetchStartingEquipmentForClass(String classIndex, final int characterId) {
        Single<ClassRequestDto> classDetailsSingle = api.getClassDetail(classIndex);
        Single<Map<String, Integer>> itemMapSingle = itemsDao.getAllItems()
                .firstOrError()
                .map(this::createItemLookupMap);

        return Single.zip(classDetailsSingle, itemMapSingle, (classDto, itemMap) -> {
            List<StartingEquipmentDto> resolvedEquipment = processEquipmentChoices(classDto).blockingGet();
            return mapDtosToCharacterItems(resolvedEquipment, itemMap, characterId);
        });
    }

    /**
     * Fetches the list of starting spells for a given class at level 1.
     * @param classIndex The index of the class (e.g., "wizard").
     * @param characterId The ID of the character to associate the spells with.
     * @return A Single that emits a list of CharacterSpells ready for database insertion.
     */
    public Single<List<CharacterSpells>> fetchStartingSpells(String classIndex, final int characterId) {
        Single<List<ResourceRefDto>> spellsFromApi = api.listClassSpellsAtOrBelow(classIndex, 1)
                .map(dto -> dto.results != null ? dto.results : Collections.emptyList());

        Single<Map<String, Integer>> spellMapFromDb = spellsDao.getAllSpells()
                .firstOrError()
                .map(this::createSpellLookupMap);

        return Single.zip(spellsFromApi, spellMapFromDb, (spellRefs, spellMap) -> {
            List<CharacterSpells> characterSpells = new ArrayList<>();
            for (ResourceRefDto spellRef : spellRefs) {
                Integer spellId = spellMap.get(spellRef.name);
                if (spellId != null) {
                    characterSpells.add(new CharacterSpells(characterId, spellId, false));
                }
            }
            return characterSpells;
        });
    }

    /**
     * Fetches all starting abilities (level 1 class features + all racial traits).
     * @param classIndex The index of the class.
     * @param raceIndex The index of the race.
     * @param characterId The ID of the character.
     * @return A Single emitting a list of CharacterAbilities for insertion.
     */
    public Single<List<CharacterAbilities>> fetchStartingAbilities(String classIndex, String raceIndex, final int characterId) {
        Single<List<ResourceRefDto>> classFeatures = api.listClassFeaturesByLevel(classIndex, 1)
                .map(dto -> dto.results != null ? dto.results : Collections.emptyList());

        Single<List<ResourceRefDto>> raceTraits = api.listRaceTraits(raceIndex)
                .map(dto -> dto.results != null ? dto.results : Collections.emptyList());

        Single<Map<String, Integer>> abilityMapFromDb = abilitiesDao.getAllAbilities()
                .firstOrError()
                .map(this::createAbilityLookupMap);

        return Single.zip(classFeatures, raceTraits, abilityMapFromDb, (features, traits, abilityMap) -> {
            List<CharacterAbilities> characterAbilities = new ArrayList<>();
            List<ResourceRefDto> allAbilityRefs = new ArrayList<>(features);
            allAbilityRefs.addAll(traits);

            for (ResourceRefDto abilityRef : allAbilityRefs) {
                Integer abilityId = abilityMap.get(abilityRef.name);
                if (abilityId != null) {
                    characterAbilities.add(new CharacterAbilities(characterId, abilityId));
                }
            }
            return characterAbilities;
        });
    }

    /**
     * Fetches the list of proficiencies for a given class.
     * @param classIndex The index of the class.
     * @return A Single emitting a list of proficiency names (e.g., "Light Armor", "Saving Throw: WIS").
     */
    public Single<List<String>> fetchProficienciesForClass(String classIndex) {
        return api.listClassProficiencies(classIndex)
                .map(dto -> {
                    List<String> proficiencyNames = new ArrayList<>();
                    if (dto.results != null) {
                        for (ResourceRefDto ref : dto.results) {
                            proficiencyNames.add(ref.name);
                        }
                    }
                    return proficiencyNames;
                });
    }

    // --- Private Helper Methods for Lookups ---

    private Map<String, Integer> createItemLookupMap(List<Items> itemsList) {
        Map<String, Integer> map = new HashMap<>();
        for (Items item : itemsList) {
            map.put(item.getName(), item.getItemID());
        }
        return map;
    }

    private Map<String, Integer> createSpellLookupMap(List<Spells> spellsList) {
        Map<String, Integer> map = new HashMap<>();
        for (Spells spell : spellsList) {
            map.put(spell.getName(), spell.getSpellID());
        }
        return map;
    }

    private Map<String, Integer> createAbilityLookupMap(List<Abilities> abilitiesList) {
        Map<String, Integer> map = new HashMap<>();
        for (Abilities ability : abilitiesList) {
            map.put(ability.getName(), ability.getAbilityID());
        }
        return map;
    }

    private Single<List<StartingEquipmentDto>> processEquipmentChoices(ClassRequestDto classDto) {
        List<StartingEquipmentDto> guaranteedEquipment = classDto.startingEquipment != null ? classDto.startingEquipment : Collections.emptyList();

        return Flowable.fromIterable(classDto.startingEquipmentOptions)
                .concatMapSingle(this::resolveSingleOption)
                .reduce(new ArrayList<StartingEquipmentDto>(), (allChoices, currentChoiceList) -> {
                    allChoices.addAll(currentChoiceList);
                    return allChoices;
                })
                .map(optionalEquipment -> {
                    List<StartingEquipmentDto> finalEquipmentList = new ArrayList<>(guaranteedEquipment);
                    finalEquipmentList.addAll(optionalEquipment);
                    return finalEquipmentList;
                });
    }

    private Single<List<StartingEquipmentDto>> resolveSingleOption(StartingEquipmentOptionDto option) {
        if (option.from == null) {
            return Single.just(Collections.emptyList());
        }

        if ("equipment_category".equals(option.from.optionSetType)) {
            return api.getEquipmentCategory(option.from.equipmentCategory.index)
                    .map(categoryDto -> {
                        List<StartingEquipmentDto> choices = new ArrayList<>();
                        if (categoryDto.equipment != null && !categoryDto.equipment.isEmpty()) {
                            ApiRef chosenRef = categoryDto.equipment.get(random.nextInt(categoryDto.equipment.size()));
                            choices.add(new StartingEquipmentDto(chosenRef, 1));
                        }
                        return choices;
                    });
        }

        if ("options_array".equals(option.from.optionSetType)) {
            return Single.fromCallable(() -> {
                List<StartingEquipmentDto> choices = new ArrayList<>();
                List<OptionDto> availableOptions = new ArrayList<>(option.from.options);
                Collections.shuffle(availableOptions);

                int chooseCount = option.choose != null ? option.choose : 0;
                for (int i = 0; i < chooseCount && i < availableOptions.size(); i++) {
                    OptionDto chosenOption = availableOptions.get(i);
                    choices.addAll(mapOptionToEquipmentDtos(chosenOption));
                }
                return choices;
            });
        }

        return Single.just(Collections.emptyList());
    }

    private List<StartingEquipmentDto> mapOptionToEquipmentDtos(OptionDto option) {
        List<StartingEquipmentDto> equipmentList = new ArrayList<>();
        if ("counted_reference".equals(option.optionType) && option.of != null) {
            equipmentList.add(new StartingEquipmentDto(option.of, option.count));
        } else if ("multiple".equals(option.optionType) && option.items != null) {
            for (OptionDto subOption : option.items) {
                equipmentList.addAll(mapOptionToEquipmentDtos(subOption));
            }
        }
        return equipmentList;
    }

    private List<CharacterItems> mapDtosToCharacterItems(List<StartingEquipmentDto> dtoList, Map<String, Integer> itemMap, int characterId) {
        List<CharacterItems> characterItemsList = new ArrayList<>();
        for (StartingEquipmentDto dto : dtoList) {
            if (dto.equipment == null || dto.equipment.name == null) continue;

            Integer itemId = itemMap.get(dto.equipment.name);
            if (itemId != null) {
                characterItemsList.add(new CharacterItems(characterId, itemId, dto.quantity, false));
            }
        }
        return characterItemsList;
    }
}