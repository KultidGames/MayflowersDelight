package net.lambdacomplex.mayflowersdelight.block;

import net.lambdacomplex.mayflowersdelight.MayflowersDelightMain;
import net.lambdacomplex.mayflowersdelight.block.custom.BasicCropBlock;
import net.lambdacomplex.mayflowersdelight.block.custom.DryingTableBlock;
import net.lambdacomplex.mayflowersdelight.block.custom.IvyBlock;
import net.lambdacomplex.mayflowersdelight.item.ModCreativeTab;
import net.lambdacomplex.mayflowersdelight.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {

    public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MayflowersDelightMain.MODID);

    public static final RegistryObject<Block> TEST_BLOCK = registerBlock("testblock", () -> new Block(BlockBehaviour.Properties.of(Material.CACTUS)), ModCreativeTab.MOD_TAB, true);
    public static final RegistryObject<Block> COMMON_IVY = registerBlock("common_ivy", () -> new IvyBlock(BlockBehaviour.Properties.of(Material.REPLACEABLE_PLANT).noCollission().noOcclusion().randomTicks().strength(0.2f).sound(SoundType.VINE)), ModCreativeTab.MOD_TAB, true);
    public static final RegistryObject<Block> DRYING_TABLE = registerBlock("drying_table", () -> new DryingTableBlock(BlockBehaviour.Properties.of(Material.WOOD)), ModCreativeTab.MOD_TAB, true);

    //Vegetables
    public static final RegistryObject<Block> GARLIC_CROP = registerBlock("garlic_crop", () -> new BasicCropBlock(BlockBehaviour.Properties.copy(Blocks.BEETROOTS), 6), ModCreativeTab.MOD_TAB, false);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, CreativeModeTab tab, boolean addToCreativeTab) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        if (addToCreativeTab) {
            registerBlockItem(name, toReturn, tab);
        }
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, CreativeModeTab tab) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
