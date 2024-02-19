package net.lambdacomplex.mayflowersdelight.item.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class MeadItem extends Item {
    public static final String EFFECTS_TAG = "Effects";
    public static final String EFFECT_ID_TAG = "EffectId";
    public static final String EFFECT_DURATION_TAG = "EffectDuration";

    public MeadItem(Item.Properties properties) {
        super(properties);
    }

    public static void saveMobEffect(ItemStack stack, MobEffect effect, int duration) {
        CompoundTag compoundTag = stack.getOrCreateTag();
        ListTag listTag = compoundTag.getList(EFFECTS_TAG, 9);
        CompoundTag effectTag = new CompoundTag();
        effectTag.putInt(EFFECT_ID_TAG, MobEffect.getId(effect));
        effectTag.putInt(EFFECT_DURATION_TAG, duration);
        listTag.add(effectTag);
        compoundTag.put(EFFECTS_TAG, listTag);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        CompoundTag compoundTag = stack.getTag();
        if (compoundTag != null && compoundTag.contains(EFFECTS_TAG, 9)) {
            ListTag listTag = compoundTag.getList(EFFECTS_TAG, 10);

            for (int i = 0; i < listTag.size(); ++i) {
                int duration = 160; // Default duration
                CompoundTag effectTag = listTag.getCompound(i);
                if (effectTag.contains(EFFECT_DURATION_TAG, 3)) {
                    duration = effectTag.getInt(EFFECT_DURATION_TAG);
                }

                MobEffect effect = MobEffect.byId(effectTag.getInt(EFFECT_ID_TAG));
                if (effect != null) {
                    entity.addEffect(new MobEffectInstance(effect, duration));
                }
            }
        }

        return entity instanceof Player && ((Player) entity).getAbilities().instabuild ? stack : new ItemStack(Items.GLASS_BOTTLE);
    }
}
