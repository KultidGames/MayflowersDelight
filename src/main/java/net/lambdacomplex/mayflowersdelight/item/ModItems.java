package net.lambdacomplex.mayflowersdelight.item;

import net.lambdacomplex.mayflowersdelight.MayflowersDelightMain;
import net.lambdacomplex.mayflowersdelight.block.ModBlocks;
import net.lambdacomplex.mayflowersdelight.block.custom.BasicCropBlock;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MayflowersDelightMain.MODID);

    // Basic Item
    public static final RegistryObject<Item> LOGO = registerBasicItem("logo");
    public static final RegistryObject<Item> BARLEY = registerBasicItem("barley");
    public static final RegistryObject<Item> CRUSHED_SUGARCANE = registerBasicItem("crushed_sugarcane");
    public static final RegistryObject<Item> DRIED_TOBACCO = registerBasicItem("dried_tobacco");


    // Food Items-- Crafting
    public static final RegistryObject<Item> MINCEDGARLIC = registerFoodItem("mincedgarlic", 2, 2);

    // Vegetables
    public static final RegistryObject<Item> GARLIC = registerFoodSeedItem("garlic", 2, 2,ModBlocks.GARLIC_CROP);
    // Meat
    public static final RegistryObject<Item> CHEVALINE = registerFoodItem("raw_chevaline", 2, 2);

    // Fruit
    public static final RegistryObject<Item> PEAR = registerFoodItem("pear", 2, 2);

    // Generic method for registering basic items
    private static RegistryObject<Item> registerBasicItem(String name) {
        return ITEMS.register(name, () ->
                new Item(new Item.Properties().tab(ModCreativeTab.MOD_TAB)));
    }


    // Generic method for registering food items
    private static RegistryObject<Item> registerFoodItem(String name, int nutrition, float saturation) {
        return ITEMS.register(name, () -> new Item(new Item.Properties().tab(ModCreativeTab.MOD_TAB).food(new FoodProperties.Builder().nutrition(nutrition).saturationMod(saturation).build())));
    }

    private static RegistryObject<Item> registerFoodSeedItem(String name, int nutrition, float saturation, Supplier<Block> blockSupplier) {
            return ITEMS.register(name, () ->
                    new ItemNameBlockItem(blockSupplier.get(), new Item.Properties()
                            .tab(ModCreativeTab.MOD_TAB)
                            .food(new FoodProperties.Builder()
                                    .nutrition(nutrition)
                                    .saturationMod(saturation)
                                    .build())));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
