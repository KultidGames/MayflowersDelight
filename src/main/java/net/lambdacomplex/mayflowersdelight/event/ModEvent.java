package net.lambdacomplex.mayflowersdelight.event;

import net.lambdacomplex.mayflowersdelight.MayflowersDelightMain;
import net.lambdacomplex.mayflowersdelight.item.ModItems;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

public class ModEvent {
    @Mod.EventBusSubscriber(modid = MayflowersDelightMain.MODID)
    public static class ForgeEvents {


        //==========Bat Logic==========

        //Bat follows player if holding Garlic
        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            Player player = event.player;
            // Check if the player is holding an apple
            if (player.getMainHandItem().getItem() == ModItems.GARLIC.get() || player.getOffhandItem().getItem() == ModItems.GARLIC.get()) {
                // Define search radius around the player
                double searchRadius = 10.0;
                List<Bat> bats = player.level.getEntitiesOfClass(Bat.class, player.getBoundingBox().inflate(searchRadius), bat -> true);
                // Make each bat within the radius move towards the player
                for (Bat bat : bats) {
                    Vec3 batPos = bat.position();
                    Vec3 playerPos = player.position();
                    Vec3 direction = playerPos.subtract(batPos).normalize().scale(0.1); // Scale controls the speed of the bat
                    bat.setDeltaMovement(direction);
                    bat.getMoveControl().setWantedPosition(playerPos.x, playerPos.y, playerPos.z, 1.0);
                }
            }
        }

        //==========Pig Logic==========


        //==========Bee Logic==========


        //==========Fish Logic==========
    }
}
