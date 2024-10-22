package red.jackf.lenientdeath.preserveitems;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.jetbrains.annotations.Nullable;
import red.jackf.lenientdeath.LenientDeath;
import red.jackf.lenientdeath.compat.TrinketsCompat;
import red.jackf.lenientdeath.config.LenientDeathConfig.PreserveItemsOnDeath.ByItemType.TypeBehavior;

import java.util.Set;

public class ItemTypeChecker {
    public static ItemTypeChecker INSTANCE = new ItemTypeChecker();
    private static final Set<ItemUseAnimation> OTHER_TOOLS_ANIMS = Set.of(
            ItemUseAnimation.BRUSH,
            ItemUseAnimation.TOOT_HORN,
            ItemUseAnimation.SPYGLASS
    );
    private ItemTypeChecker() {}

    public @Nullable Boolean shouldKeep(@Nullable Player player, ItemStack stack) {
        var config = LenientDeath.CONFIG.instance().preserveItemsOnDeath.byItemType;
        if (!config.enabled) return null;

        Item item = stack.getItem();
        TypeBehavior result = TypeBehavior.ignore;

        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable != null) {
            boolean specificEquipment = false;
            if (item instanceof ArmorItem) {
                TypeBehavior mod = switch (equippable.slot()) {
                    case HEAD -> config.helmets;
                    case CHEST -> config.chestplates;
                    case LEGS -> config.leggings;
                    case FEET -> config.boots;
                    case BODY -> config.body;
                    default -> null;
                };

                if (mod != null) {
                    result = result.and(mod);
                    specificEquipment = true;
                }
            }
            if (stack.has(DataComponents.GLIDER)) {
                result = result.and(config.elytras);
                specificEquipment = true;
            }
            if (item instanceof ShieldItem) {
                result = result.and(config.shields);
                specificEquipment = true;
            }
            if (!specificEquipment) result = result.and(config.otherEquippables);
        }

        if (FabricLoader.getInstance().isModLoaded("trinkets") && TrinketsCompat.isTrinket(player, stack)) result = result.and(config.trinkets);

        if (item instanceof SwordItem) result = result.and(config.swords);
        if (item instanceof TridentItem) result = result.and(config.tridents);
        if (item instanceof MaceItem) result = result.and(config.maces);

        if (item instanceof ProjectileWeaponItem)
            if (item instanceof BowItem) result = result.and(config.bows);
            else if (item instanceof CrossbowItem) result = result.and(config.crossbows);
            else result = result.and(config.otherProjectileLaunchers);

        if (item instanceof DiggerItem)
            if (item instanceof PickaxeItem) result = result.and(config.pickaxes);
            else if (item instanceof ShovelItem) result = result.and(config.shovels);
            else if (item instanceof AxeItem) result = result.and(config.axes);
            else if (item instanceof HoeItem) result = result.and(config.hoes);
            else result = result.and(config.otherDiggingItems);
        else
            if (OTHER_TOOLS_ANIMS.contains(stack.getUseAnimation())) result = result.and(config.otherTools);

        if (item instanceof BucketItem) result = result.and(config.buckets);

        if (stack.has(DataComponents.FOOD)) result = result.and(config.food);

        if (item instanceof PotionItem) result = result.and(config.potions);

        if (item instanceof BlockItem blockItem)
            if (blockItem.getBlock() instanceof ShulkerBoxBlock) result = result.and(config.shulkerBoxes);

        return switch (result) {
            case drop -> false;
            case preserve -> true;
            case ignore -> null;
        };
    }
}
