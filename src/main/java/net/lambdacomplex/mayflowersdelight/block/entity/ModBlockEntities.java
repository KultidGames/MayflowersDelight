package net.lambdacomplex.mayflowersdelight.block.entity;

import net.lambdacomplex.mayflowersdelight.MayflowersDelightMain;
import net.lambdacomplex.mayflowersdelight.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MayflowersDelightMain.MODID);

    public static final RegistryObject<BlockEntityType<DryingTableBlockEntity>> DRYING_TABLE = BLOCK_ENTITIES.register("drying_table", () -> BlockEntityType.Builder.of(DryingTableBlockEntity::new, ModBlocks.DRYING_TABLE.get()).build(null));

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }

}
