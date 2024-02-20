package net.lambdacomplex.mayflowersdelight.recipe;

import net.lambdacomplex.mayflowersdelight.item.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.ArrayList;
import java.util.List;

public class BrewingRecipeList {
    public static final List<Recipe> RECIPES = new ArrayList<>();

    static {
        // Existing example recipes
        RECIPES.add(new Recipe(new ItemStack(Items.APPLE), new ItemStack(Items.GOLDEN_APPLE), 100));
        RECIPES.add(new Recipe(new ItemStack(Items.WHEAT_SEEDS), new ItemStack(Items.BREAD), 200));
        // Rum recipe (sugar to rum, 150 ticks as an example)
        RECIPES.add(new Recipe(new ItemStack(Items.SUGAR), new ItemStack(ModItems.RUM.get()), 24000));
    }

    public static class Recipe {
        private final ItemStack input;
        private final ItemStack output;
        private final int maxProgress;

        public Recipe(ItemStack input, ItemStack output, int maxProgress) {
            this.input = input;
            this.output = output;
            this.maxProgress = maxProgress;
        }

        public ItemStack getInput() {
            return input;
        }

        public ItemStack getOutput() {
            return output;
        }

        public int getMaxProgress() {
            return maxProgress;
        }
    }
}
