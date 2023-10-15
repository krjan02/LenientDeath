package red.jackf.lenientdeath.filtering;

import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import red.jackf.lenientdeath.LenientDeath;
import red.jackf.lenientdeath.config.LenientDeathConfig;

import java.util.HashSet;
import java.util.Set;

public class ItemFiltering {
    private static final Logger LOGGER = LenientDeath.getLogger("Item Filtering");
    public static final ItemFiltering INSTANCE = new ItemFiltering();
    private ItemFiltering() {}


    @Nullable
    private Registry<Item> itemRegistry = null;

    private final Set<Item> alwaysPreserved = new HashSet<>();
    private final Set<Item> alwaysDroppedItems = new HashSet<>();

    public void setup() {
        CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> {
            if (client) return;
            this.itemRegistry = registries.registryOrThrow(Registries.ITEM);
            this.refreshItems();
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            this.itemRegistry = null;
            this.alwaysPreserved.clear();
            this.alwaysDroppedItems.clear();
        });
    }

    public void refreshItems() {
        this.alwaysPreserved.clear();
        this.alwaysDroppedItems.clear();
        if (this.itemRegistry == null) return;
        LenientDeathConfig.PreserveItemsOnDeath config = LenientDeathConfig.INSTANCE.get().preserveItemsOnDeath;

        LOGGER.debug("Creating always preserved list");

        for (var itemId : config.alwaysPreserved.items) {
            Item item = this.itemRegistry.get(itemId);
            if (item == null) {
                LOGGER.warn("Unknown item ID: {}", itemId);
                continue;
            }
            LOGGER.debug("Adding item {}", itemId);
            this.alwaysPreserved.add(item);
        }

        for (var tagId : config.alwaysPreserved.tags) {
            var tag = this.itemRegistry.getTag(TagKey.create(Registries.ITEM, tagId));
            if (tag.isEmpty()) {
                LOGGER.warn("Unknown tag ID: {} ", tagId);
                continue;
            }
            LOGGER.debug("Adding tag {}", tagId);

            for (var item : tag.get()) {
                this.alwaysPreserved.add(item.value());
                LOGGER.debug("Adding tag {} item {}", tagId, this.itemRegistry.getKey(item.value()));
            }
        }

        LOGGER.debug("Total for always preserved: {}", this.alwaysPreserved.size());

        LOGGER.debug("Creating always dropped list");

        for (var itemId : config.alwaysDropped.items) {
            Item item = this.itemRegistry.get(itemId);
            if (item == null) {
                LOGGER.warn("Unknown item ID: {}", itemId);
                continue;
            }
            LOGGER.debug("Adding item {}", itemId);
            this.alwaysDroppedItems.add(item);
        }

        for (var tagId : config.alwaysDropped.tags) {
            var tag = this.itemRegistry.getTag(TagKey.create(Registries.ITEM, tagId));
            if (tag.isEmpty()) {
                LOGGER.warn("Unknown tag ID: {} ", tagId);
                continue;
            }
            LOGGER.debug("Adding tag {}", tagId);

            for (var item : tag.get()) {
                this.alwaysDroppedItems.add(item.value());
                LOGGER.debug("Adding tag {} item {}", tagId, this.itemRegistry.getKey(item.value()));
            }
        }

        LOGGER.debug("Total for always dropped: {}", this.alwaysDroppedItems.size());
    }
}
