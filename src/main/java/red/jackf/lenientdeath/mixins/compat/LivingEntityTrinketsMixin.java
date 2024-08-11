package red.jackf.lenientdeath.mixins.compat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.lenientdeath.api.LenientDeathAPI;

// TODO solve cleaner, make it handle @Unique or ask for a callback or smth
@Mixin(value = LivingEntity.class, priority = 1200)
@Pseudo
public class LivingEntityTrinketsMixin {
    @SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget"})
    @WrapOperation(
            method = "dropFromEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;"),
            require = 0)
    private ItemEntity lenientdeath$handleTrinketsItemEntities(Player player, ItemStack stack, boolean dropAround, boolean throwerName, Operation<ItemEntity> original) {
        ItemEntity entity = original.call(player, stack, dropAround, throwerName);

        if (player instanceof ServerPlayer serverPlayer && entity != null)
            LenientDeathAPI.INSTANCE.markDeathItem(serverPlayer, entity, null);

        return entity;
    }
}