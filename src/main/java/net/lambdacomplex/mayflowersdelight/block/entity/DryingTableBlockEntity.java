package net.lambdacomplex.mayflowersdelight.block.entity;

import net.lambdacomplex.mayflowersdelight.item.ModItems;
import net.lambdacomplex.mayflowersdelight.networking.ModMessages;
import net.lambdacomplex.mayflowersdelight.networking.packet.ItemStackSyncS2CPacket;
import net.lambdacomplex.mayflowersdelight.screen.DryingTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DryingTableBlockEntity extends BlockEntity implements MenuProvider {



    private final ItemStackHandler itemHandler = new ItemStackHandler(2){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                ModMessages.sendToClients(new ItemStackSyncS2CPacket(this, worldPosition));
            }
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 2400; //Change me for length of crafting

    public DryingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRYING_TABLE.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0 -> DryingTableBlockEntity.this.progress;
                    case 1 -> DryingTableBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> DryingTableBlockEntity.this.progress = value;
                    case 1 -> DryingTableBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Drying Table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new DryingTableMenu(id, inventory, this, this.data);
    }

    //RENDERER CLASS

    public ItemStack getRenderStack() {
        ItemStack stack;

        if(!itemHandler.getStackInSlot(1).isEmpty()) {
            stack = itemHandler.getStackInSlot(1);
        } else {
            stack = itemHandler.getStackInSlot(0);
        }

        return stack;
    }

    public void setHandler(ItemStackHandler itemStackHandler) {
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            itemHandler.setStackInSlot(i, itemStackHandler.getStackInSlot(i));
        }
    }


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER){
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putInt("drying_table.progress", this.progress);

        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("drying_table.progress");
    }

    public void drops(){
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++){
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DryingTableBlockEntity pEntity) {
        if (level.isClientSide()) {
            return;
        }

        // Adjust maxProgress for two real-life minutes
        pEntity.maxProgress = 2400; // Two real-life minutes in ticks

        boolean isDaytime = level.getDayTime() % 24000 < 12000; // Check if it's daytime
        boolean isDirectlyUnderSky = level.canSeeSky(pos); // Check if the block is directly under the sky
        boolean isNotRaining = !level.isRaining(); // Check if it's not raining

        if (hasRecipe(pEntity) && isDaytime && isDirectlyUnderSky && isNotRaining) {
            pEntity.progress++;
            setChanged(level, pos, state);

            if (pEntity.progress >= pEntity.maxProgress) {
                craftItem(pEntity);
            }
        } else {
            if (pEntity.progress > 0) {
                // Optional: Reset progress if conditions are not met
             //   pEntity.resetProgress();
                setChanged(level, pos, state);
            }
        }
    }


    private void resetProgress() {
        this.progress = 0;
    }

    private static void craftItem(DryingTableBlockEntity pEntity) {
        if (hasRecipe(pEntity)) {


            pEntity.itemHandler.extractItem(0, 1, false);

            ItemStack outputStack = pEntity.itemHandler.getStackInSlot(1);


            if (!outputStack.isEmpty()) { //could be this line???
                outputStack.grow(1); // Grow the stack by one

            } else {
                pEntity.itemHandler.setStackInSlot(1, new ItemStack(Items.APPLE, 1));

            }



            pEntity.resetProgress();
        } else {

        }
    }




    private static boolean hasRecipe(DryingTableBlockEntity entity) {
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        boolean hasCraftableItemInFirstSlot = entity.itemHandler.getStackInSlot(0).getItem() == ModItems.BARLEY.get();

        return hasCraftableItemInFirstSlot && canInsertAmountIntoOutputSlot(inventory) &&
                canInsertItemIntoOutputSlot(inventory, new ItemStack(Items.APPLE, 1));
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack stack) {
        boolean canInsert = inventory.getItem(1).getItem() == stack.getItem() || inventory.getItem(1).isEmpty();
        return canInsert;
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory) {
        boolean canInsert = inventory.getItem(1).getMaxStackSize() > inventory.getItem(1).getCount();
        return canInsert;
    }

}
