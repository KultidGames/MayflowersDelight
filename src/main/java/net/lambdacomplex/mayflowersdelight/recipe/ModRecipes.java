package net.lambdacomplex.mayflowersdelight.recipe;

import net.lambdacomplex.mayflowersdelight.MayflowersDelightMain;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MayflowersDelightMain.MODID);

    public static final RegistryObject<RecipeSerializer<DryingTableRecipe>> DRYING_TABLE_SERIALIZER = SERIALIZERS.register("drying_table", () -> DryingTableRecipe.Serializer.INSTANCE);

    public static void register(IEventBus bus) {
        SERIALIZERS.register(bus);
    }
}
