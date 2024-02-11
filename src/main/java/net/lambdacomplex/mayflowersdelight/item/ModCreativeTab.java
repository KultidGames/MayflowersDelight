package net.lambdacomplex.mayflowersdelight.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeTab {
    public static final CreativeModeTab MOD_TAB = new CreativeModeTab("mayflowersdelight") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.LOGO.get());
        }
    };

}
