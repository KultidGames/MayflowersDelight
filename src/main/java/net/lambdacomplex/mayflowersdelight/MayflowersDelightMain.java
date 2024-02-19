package net.lambdacomplex.mayflowersdelight;

import com.mojang.logging.LogUtils;
import net.lambdacomplex.mayflowersdelight.block.ModBlocks;
import net.lambdacomplex.mayflowersdelight.block.entity.ModBlockEntities;
import net.lambdacomplex.mayflowersdelight.item.ModItems;
import net.lambdacomplex.mayflowersdelight.networking.ModMessages;
import net.lambdacomplex.mayflowersdelight.recipe.ModRecipes;
import net.lambdacomplex.mayflowersdelight.screen.DryingTableScreen;
import net.lambdacomplex.mayflowersdelight.screen.FermentingBarrelScreen;
import net.lambdacomplex.mayflowersdelight.screen.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(MayflowersDelightMain.MODID)
public class MayflowersDelightMain {

    public static final String MODID = "mayflowersdelight";
    private static final Logger LOGGER = LogUtils.getLogger();

    public MayflowersDelightMain() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModMessages.register();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        ModRecipes.register(modEventBus);


        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            MenuScreens.register(ModMenuTypes.DRYING_TABLE_MENU.get(), DryingTableScreen::new);
            MenuScreens.register(ModMenuTypes.FERMENTING_BARREL_MENU.get(), FermentingBarrelScreen::new);
        }
    }
}

