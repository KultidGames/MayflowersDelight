package net.lambdacomplex.mayflowersdelight.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.lambdacomplex.mayflowersdelight.MayflowersDelightMain;
import net.lambdacomplex.mayflowersdelight.recipe.DryingTableRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class JEITutorialModPlugin implements IModPlugin {
    public static RecipeType<DryingTableRecipe> DRYINGTABLE_TYPE =
            new RecipeType<>(DryingTableRecipeCategory.UID, DryingTableRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(MayflowersDelightMain.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new
                DryingTableRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

        List<DryingTableRecipe> recipesInfusing = rm.getAllRecipesFor(DryingTableRecipe.Type.INSTANCE);
        registration.addRecipes(DRYINGTABLE_TYPE, recipesInfusing);
    }
}