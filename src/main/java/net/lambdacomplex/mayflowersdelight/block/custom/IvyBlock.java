package net.lambdacomplex.mayflowersdelight.block.custom;

import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.world.level.block.Mirror.FRONT_BACK;
import static net.minecraft.world.level.block.Mirror.LEFT_RIGHT;
import static net.minecraft.world.level.block.Rotation.CLOCKWISE_180;
import static net.minecraft.world.level.block.Rotation.COUNTERCLOCKWISE_90;

public class IvyBlock extends Block implements IForgeShearable {
    public static final BooleanProperty UP = PipeBlock.UP;
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((p_57886_) -> {
        return p_57886_.getKey() != Direction.DOWN;
    }).collect(Util.toMap());
    protected static final float AABB_OFFSET = 1.0F;
    private static final VoxelShape UP_AABB = Block.box(0.0D, 15.1D, 0.0D, 16.1D, 16.1D, 16.1D);
    private static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.1D, 16.1D, 16.1D);
    private static final VoxelShape EAST_AABB = Block.box(15.1D, 0.0D, 0.0D, 16.1D, 16.1D, 16.1D);
    private static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.1D, 16.1D, 1.1D);
    private static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 15.1D, 16.1D, 16.1D, 16.1D);
    private final Map<BlockState, VoxelShape> shapesCache;

    public IvyBlock(BlockBehaviour.Properties properties) {
        super(properties);
        properties.sound(SoundType.VINE);
        this.registerDefaultState(this.stateDefinition.any().setValue(UP, Boolean.valueOf(false)).setValue(NORTH, Boolean.valueOf(false)).setValue(EAST, Boolean.valueOf(false)).setValue(SOUTH, Boolean.valueOf(false)).setValue(WEST, Boolean.valueOf(false)));
        this.shapesCache = ImmutableMap.copyOf(this.stateDefinition.getPossibleStates().stream().collect(Collectors.toMap(Function.identity(), IvyBlock::calculateShape)));
    }

   @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        entity.makeStuckInBlock(state, new Vec3(0.92D, (double)0.92D, 0.92D));
    }

    @Override
    public boolean isShearable(@NotNull ItemStack item, Level level, BlockPos pos) {
        return true;
    }




    private static VoxelShape calculateShape(BlockState p_57906_) {
        VoxelShape voxelshape = Shapes.empty();
        if (p_57906_.getValue(UP)) {
            voxelshape = UP_AABB;
        }

        if (p_57906_.getValue(NORTH)) {
            voxelshape = Shapes.or(voxelshape, NORTH_AABB);
        }

        if (p_57906_.getValue(SOUTH)) {
            voxelshape = Shapes.or(voxelshape, SOUTH_AABB);
        }

        if (p_57906_.getValue(EAST)) {
            voxelshape = Shapes.or(voxelshape, EAST_AABB);
        }

        if (p_57906_.getValue(WEST)) {
            voxelshape = Shapes.or(voxelshape, WEST_AABB);
        }

        return voxelshape.isEmpty() ? Shapes.block() : voxelshape;
    }

    public VoxelShape getShape(BlockState p_57897_, BlockGetter p_57898_, BlockPos p_57899_, CollisionContext p_57900_) {
        return this.shapesCache.get(p_57897_);
    }

    public boolean propagatesSkylightDown(BlockState p_181239_, BlockGetter p_181240_, BlockPos p_181241_) {
        return true;
    }

    public boolean canSurvive(BlockState p_57861_, LevelReader p_57862_, BlockPos p_57863_) {
        return this.hasFaces(this.getUpdatedState(p_57861_, p_57862_, p_57863_));
    }

    private boolean hasFaces(BlockState p_57908_) {
        return this.countFaces(p_57908_) > 0;
    }

    private int countFaces(BlockState p_57910_) {
        int i = 0;

        for(BooleanProperty booleanproperty : PROPERTY_BY_DIRECTION.values()) {
            if (p_57910_.getValue(booleanproperty)) {
                ++i;
            }
        }

        return i;
    }

    private boolean canSupportAtFace(BlockGetter p_57888_, BlockPos p_57889_, Direction p_57890_) {
        if (p_57890_ == Direction.DOWN) {
            return false;
        } else {
            BlockPos blockpos = p_57889_.relative(p_57890_);
            if (isAcceptableNeighbour(p_57888_, blockpos, p_57890_)) {
                return true;
            } else if (p_57890_.getAxis() == Direction.Axis.Y) {
                return false;
            } else {
                BooleanProperty booleanproperty = PROPERTY_BY_DIRECTION.get(p_57890_);
                BlockState blockstate = p_57888_.getBlockState(p_57889_.above());
                return blockstate.is(this) && blockstate.getValue(booleanproperty);
            }
        }
    }

    public static boolean isAcceptableNeighbour(BlockGetter p_57854_, BlockPos p_57855_, Direction p_57856_) {
        return MultifaceBlock.canAttachTo(p_57854_, p_57856_, p_57855_, p_57854_.getBlockState(p_57855_));
    }

    private BlockState getUpdatedState(BlockState p_57902_, BlockGetter p_57903_, BlockPos p_57904_) {
        BlockPos blockpos = p_57904_.above();
        if (p_57902_.getValue(UP)) {
            p_57902_ = p_57902_.setValue(UP, Boolean.valueOf(isAcceptableNeighbour(p_57903_, blockpos, Direction.DOWN)));
        }

        BlockState blockstate = null;

        for(Direction direction : Direction.Plane.HORIZONTAL) {
            BooleanProperty booleanproperty = getPropertyForFace(direction);
            if (p_57902_.getValue(booleanproperty)) {
                boolean flag = this.canSupportAtFace(p_57903_, p_57904_, direction);
                if (!flag) {
                    if (blockstate == null) {
                        blockstate = p_57903_.getBlockState(blockpos);
                    }

                    flag = blockstate.is(this) && blockstate.getValue(booleanproperty);
                }

                p_57902_ = p_57902_.setValue(booleanproperty, Boolean.valueOf(flag));
            }
        }

        return p_57902_;
    }

    public BlockState updateShape(BlockState p_57875_, Direction p_57876_, BlockState p_57877_, LevelAccessor p_57878_, BlockPos p_57879_, BlockPos p_57880_) {
        if (p_57876_ == Direction.DOWN) {
            return super.updateShape(p_57875_, p_57876_, p_57877_, p_57878_, p_57879_, p_57880_);
        } else {
            BlockState blockstate = this.getUpdatedState(p_57875_, p_57878_, p_57879_);
            return !this.hasFaces(blockstate) ? Blocks.AIR.defaultBlockState() : blockstate;
        }
    }

    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.random.nextInt(4) == 0 && level.isAreaLoaded(pos, 4)) { // Forge: check area to prevent loading unloaded chunks
            Direction direction = Direction.getRandom(random);
            BlockPos blockpos = pos.above();
            if (direction.getAxis().isHorizontal() && !state.getValue(getPropertyForFace(direction))) {
                if (this.canSpread(level, pos)) {
                    BlockPos blockpos4 = pos.relative(direction);
                    BlockState blockstate4 = level.getBlockState(blockpos4);
                    if (blockstate4.isAir()) {
                        Direction direction3 = direction.getClockWise();
                        Direction direction4 = direction.getCounterClockWise();
                        boolean flag = state.getValue(getPropertyForFace(direction3));
                        boolean flag1 = state.getValue(getPropertyForFace(direction4));
                        BlockPos blockpos2 = blockpos4.relative(direction3);
                        BlockPos blockpos3 = blockpos4.relative(direction4);
                        if (flag && isAcceptableNeighbour(level, blockpos2, direction3)) {
                            level.setBlock(blockpos4, this.defaultBlockState().setValue(getPropertyForFace(direction3), Boolean.valueOf(true)), 2);
                        } else if (flag1 && isAcceptableNeighbour(level, blockpos3, direction4)) {
                            level.setBlock(blockpos4, this.defaultBlockState().setValue(getPropertyForFace(direction4), Boolean.valueOf(true)), 2);
                        } else {
                            Direction direction1 = direction.getOpposite();
                            if (flag && level.isEmptyBlock(blockpos2) && isAcceptableNeighbour(level, pos.relative(direction3), direction1)) {
                                level.setBlock(blockpos2, this.defaultBlockState().setValue(getPropertyForFace(direction1), Boolean.valueOf(true)), 2);
                            } else if (flag1 && level.isEmptyBlock(blockpos3) && isAcceptableNeighbour(level, pos.relative(direction4), direction1)) {
                                level.setBlock(blockpos3, this.defaultBlockState().setValue(getPropertyForFace(direction1), Boolean.valueOf(true)), 2);
                            } else if ((double)random.nextFloat() < 0.05D && isAcceptableNeighbour(level, blockpos4.above(), Direction.UP)) {
                                level.setBlock(blockpos4, this.defaultBlockState().setValue(UP, Boolean.valueOf(true)), 2);
                            }
                        }
                    } else if (isAcceptableNeighbour(level, blockpos4, direction)) {
                        level.setBlock(pos, state.setValue(getPropertyForFace(direction), Boolean.valueOf(true)), 2);
                    }

                }
            } else {
                if (direction == Direction.UP && pos.getY() < level.getMaxBuildHeight() - 1) {
                    if (this.canSupportAtFace(level, pos, direction)) {
                        level.setBlock(pos, state.setValue(UP, Boolean.valueOf(true)), 2);
                        return;
                    }

                    if (level.isEmptyBlock(blockpos)) {
                        if (!this.canSpread(level, pos)) {
                            return;
                        }

                        BlockState blockstate3 = state;

                        for(Direction direction2 : Direction.Plane.HORIZONTAL) {
                            if (random.nextBoolean() || !isAcceptableNeighbour(level, blockpos.relative(direction2), direction2)) {
                                blockstate3 = blockstate3.setValue(getPropertyForFace(direction2), Boolean.valueOf(false));
                            }
                        }

                        if (this.hasHorizontalConnection(blockstate3)) {
                            level.setBlock(blockpos, blockstate3, 2);
                        }

                        return;
                    }
                }

                if (pos.getY() > level.getMinBuildHeight()) {
                    BlockPos blockpos1 = pos.below();
                    BlockState blockstate = level.getBlockState(blockpos1);
                    if (blockstate.isAir() || blockstate.is(this)) {
                        BlockState blockstate1 = blockstate.isAir() ? this.defaultBlockState() : blockstate;
                        BlockState blockstate2 = this.copyRandomFaces(state, blockstate1, random);
                        if (blockstate1 != blockstate2 && this.hasHorizontalConnection(blockstate2)) {
                            level.setBlock(blockpos1, blockstate2, 2);
                        }
                    }
                }

            }
        }


        if (level.random.nextInt(4) == 0 && level.isAreaLoaded(pos, 4)) { // Forge: check area to prevent loading unloaded chunks
            // Iterate over all horizontal directions since your logic seems intended for horizontal facings
            for (Direction direction : Direction.values()) {
                // Ensure the direction is valid and the property exists before accessing it
                BooleanProperty directionProperty = PROPERTY_BY_DIRECTION.get(direction);
                if (directionProperty != null && state.getValue(directionProperty)) {
                    BlockPos targetPos = pos.relative(direction);
                    BlockState targetState = level.getBlockState(targetPos);
                    Block targetBlock = targetState.getBlock();

                    ResourceLocation blockResourceLocation = ForgeRegistries.BLOCKS.getKey(targetBlock);
                    if (blockResourceLocation != null) {
                        String mossyBlockName = "mossy_" + blockResourceLocation.getPath();
                        ResourceLocation mossyBlockResourceLocation = new ResourceLocation(blockResourceLocation.getNamespace(), mossyBlockName);

                        Block mossyVariant = ForgeRegistries.BLOCKS.getValue(mossyBlockResourceLocation);
                        if (mossyVariant != null && mossyVariant != Blocks.AIR) {
                            if (random.nextInt(4) == 0) {
                                level.setBlock(targetPos, mossyVariant.defaultBlockState(), 3);
                            }
                        }
                    }

                    // Since you want to change only one block per tick, exit the loop after the first change
                    break;
                }
            }
        }    }

    private BlockState copyRandomFaces(BlockState p_222651_, BlockState p_222652_, RandomSource p_222653_) {
        for(Direction direction : Direction.Plane.HORIZONTAL) {
            if (p_222653_.nextBoolean()) {
                BooleanProperty booleanproperty = getPropertyForFace(direction);
                if (p_222651_.getValue(booleanproperty)) {
                    p_222652_ = p_222652_.setValue(booleanproperty, Boolean.valueOf(true));
                }
            }
        }

        return p_222652_;
    }

    private boolean hasHorizontalConnection(BlockState p_57912_) {
        return p_57912_.getValue(NORTH) || p_57912_.getValue(EAST) || p_57912_.getValue(SOUTH) || p_57912_.getValue(WEST);
    }

    private boolean canSpread(BlockGetter p_57851_, BlockPos p_57852_) {
        int i = 4;
        Iterable<BlockPos> iterable = BlockPos.betweenClosed(p_57852_.getX() - 4, p_57852_.getY() - 1, p_57852_.getZ() - 4, p_57852_.getX() + 4, p_57852_.getY() + 1, p_57852_.getZ() + 4);
        int j = 5;

        for(BlockPos blockpos : iterable) {
            if (p_57851_.getBlockState(blockpos).is(this)) {
                --j;
                if (j <= 0) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean canBeReplaced(BlockState p_57858_, BlockPlaceContext p_57859_) {
        BlockState blockstate = p_57859_.getLevel().getBlockState(p_57859_.getClickedPos());
        if (blockstate.is(this)) {
            return this.countFaces(blockstate) < PROPERTY_BY_DIRECTION.size();
        } else {
            return super.canBeReplaced(p_57858_, p_57859_);
        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_57849_) {
        BlockState blockstate = p_57849_.getLevel().getBlockState(p_57849_.getClickedPos());
        boolean flag = blockstate.is(this);
        BlockState blockstate1 = flag ? blockstate : this.defaultBlockState();

        for(Direction direction : p_57849_.getNearestLookingDirections()) {
            if (direction != Direction.DOWN) {
                BooleanProperty booleanproperty = getPropertyForFace(direction);
                boolean flag1 = flag && blockstate.getValue(booleanproperty);
                if (!flag1 && this.canSupportAtFace(p_57849_.getLevel(), p_57849_.getClickedPos(), direction)) {
                    return blockstate1.setValue(booleanproperty, Boolean.valueOf(true));
                }
            }
        }

        return flag ? blockstate1 : null;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_57882_) {
        p_57882_.add(UP, NORTH, EAST, SOUTH, WEST);
    }

    public BlockState rotate(BlockState p_57868_, Rotation p_57869_) {
        switch (p_57869_) {
            case CLOCKWISE_180:
                return p_57868_.setValue(NORTH, p_57868_.getValue(SOUTH)).setValue(EAST, p_57868_.getValue(WEST)).setValue(SOUTH, p_57868_.getValue(NORTH)).setValue(WEST, p_57868_.getValue(EAST));
            case COUNTERCLOCKWISE_90:
                return p_57868_.setValue(NORTH, p_57868_.getValue(EAST)).setValue(EAST, p_57868_.getValue(SOUTH)).setValue(SOUTH, p_57868_.getValue(WEST)).setValue(WEST, p_57868_.getValue(NORTH));
            case CLOCKWISE_90:
                return p_57868_.setValue(NORTH, p_57868_.getValue(WEST)).setValue(EAST, p_57868_.getValue(NORTH)).setValue(SOUTH, p_57868_.getValue(EAST)).setValue(WEST, p_57868_.getValue(SOUTH));
            default:
                return p_57868_;
        }
    }

    public BlockState mirror(BlockState p_57865_, Mirror p_57866_) {
        switch (p_57866_) {
            case LEFT_RIGHT:
                return p_57865_.setValue(NORTH, p_57865_.getValue(SOUTH)).setValue(SOUTH, p_57865_.getValue(NORTH));
            case FRONT_BACK:
                return p_57865_.setValue(EAST, p_57865_.getValue(WEST)).setValue(WEST, p_57865_.getValue(EAST));
            default:
                return super.mirror(p_57865_, p_57866_);
        }
    }

    public static BooleanProperty getPropertyForFace(Direction p_57884_) {
        return PROPERTY_BY_DIRECTION.get(p_57884_);
    }
}
