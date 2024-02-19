package net.lambdacomplex.mayflowersdelight.integration;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.lambdacomplex.mayflowersdelight.MayflowersDelightMain;
import net.lambdacomplex.mayflowersdelight.block.ModBlocks;
import net.lambdacomplex.mayflowersdelight.recipe.DryingTableRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class DryingTableRecipeCategory implements IRecipeCategory<DryingTableRecipe> {
    public final static ResourceLocation UID = new ResourceLocation(MayflowersDelightMain.MODID, "drying_table");
    public final static ResourceLocation TEXTURE =
            new ResourceLocation(MayflowersDelightMain.MODID, "textures/gui/drying_table_gui.png");

    private final IDrawable background;
    private final IDrawable icon;

    public DryingTableRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.DRYING_TABLE.get()));
    }

    @Override
    public RecipeType<DryingTableRecipe> getRecipeType() {
        return JEITutorialModPlugin.DRYINGTABLE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Drying Table");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DryingTableRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 45, 38).addIngredients(recipe.getIngredients().get(0));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 115, 38).addItemStack(recipe.getResultItem());
    }
}