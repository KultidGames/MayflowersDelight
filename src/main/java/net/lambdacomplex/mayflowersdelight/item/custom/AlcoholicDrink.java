package net.lambdacomplex.mayflowersdelight.item.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class AlcoholicDrink extends Item {

    public AlcoholicDrink(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entityLiving) {
        if (!world.isClientSide) {
            // Check if the item has the "Enhanced" tag indicating it was crafted with Redstone
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.getBoolean("Enhanced")) {
                entityLiving.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 300, 1)); // 15s Regeneration II
            }
        }
        return super.finishUsingItem(stack, world, entityLiving);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack p_41358_) {
        return UseAnim.DRINK;
    }
}
