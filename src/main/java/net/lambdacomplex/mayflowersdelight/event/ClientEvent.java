package net.lambdacomplex.mayflowersdelight.event;

import net.lambdacomplex.mayflowersdelight.MayflowersDelightMain;
import net.lambdacomplex.mayflowersdelight.block.entity.ModBlockEntities;
import net.lambdacomplex.mayflowersdelight.block.entity.renderer.DryingTableBlockEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ClientEvent {

    @Mod.EventBusSubscriber(modid = MayflowersDelightMain.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntities.DRYING_TABLE.get(),
                    DryingTableBlockEntityRenderer::new);
        }
    }

}
