package net.quepierts.thatskyinteractions.block.entity;

import it.unimi.dsi.fastutil.shorts.AbstractShort2IntFunction;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.CandleClusterBlock;
import net.quepierts.thatskyinteractions.block.CandleType;
import net.quepierts.thatskyinteractions.client.gui.component.w2s.PickupCandleW2SButton;
import net.quepierts.thatskyinteractions.client.gui.component.w2s.World2ScreenButton;
import net.quepierts.thatskyinteractions.client.gui.component.w2s.World2ScreenWidget;
import net.quepierts.thatskyinteractions.registry.BlockEntities;
import net.quepierts.thatskyinteractions.registry.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class CandleClusterBlockEntity extends AbstractW2SWidgetProviderBlockEntity implements IPickable {
    public static final ResourceLocation TYPE = ThatSkyInteractions.getLocation("candle_cluster");
    public static final int MAX_ROTATION = 8;
    public static final float UNIT_ROTATION_RAD = Mth.HALF_PI / MAX_ROTATION;
    public static final float UNIT_ROTATION_DEG = 90.0f / MAX_ROTATION;
    private static final int GRID_LENGTH = 256 / 32;
    private static final short LIT_FLAG = (short) 0x8000;
    private static final String TAG_CANDLES = "candles";
    private final ShortArrayList candles;
    private final ShortArrayList lightedCandles;
    private final int[] grid;

    @NotNull
    private VoxelShape lowerShape = Shapes.empty();

    public CandleClusterBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntities.CANDLE_CLUSTER.get(), pos, blockState);
        this.grid = new int[GRID_LENGTH];
        this.candles = new ShortArrayList();
        this.lightedCandles = new ShortArrayList();
    }

    @Override
    public void toNBT(@NotNull CompoundTag tag) {
        super.toNBT(tag);
        int[] array = new int[this.candles.size()];

        for (int i = 0; i < this.candles.size(); i++) {
            array[i] = this.candles.getShort(i);
        }

        tag.putIntArray(TAG_CANDLES, array);
    }

    @Override
    public void fromNBT(@NotNull CompoundTag tag) {
        super.fromNBT(tag);
        if (tag.contains(TAG_CANDLES)) {
            Arrays.fill(this.grid, 0);
            this.candles.clear();
            this.lightedCandles.clear();

            int[] array = tag.getIntArray(TAG_CANDLES);

            for (int bits : array) {
                this.addCandle((short) bits);
            }

            if (this.level != null) {
                this.update(level);
            }
        }
    }

    @Override
    public ResourceLocation type() {
        return null;
    }

    @Override
    public void markUpdate() {
        BlockState current = this.getBlockState();
        BlockState state = current.setValue(CandleClusterBlock.LEVEL, this.calculateLightLevel());
        BlockPos pos = this.getBlockPos();

        if (level != null) {
            level.setBlock(
                    pos,
                    state,
                    Block.UPDATE_CLIENTS
            );
        }
        super.markUpdate();
    }

    private void update(@NotNull Level level) {
        BlockState current = this.getBlockState();
        BlockPos pos = this.getBlockPos();

        level.setBlock(pos, current, Block.UPDATE_CLIENTS);
        level.sendBlockUpdated(pos, current, current, Block.UPDATE_CLIENTS);
    }

    private int calculateLightLevel() {
        if (this.lightedCandles.isEmpty()) {
            return 0;
        }

        int level = 4;
        for (Short candle : this.lightedCandles) {
            CandleType type = getCandleType(candle);
            level += type.getSize() / 2;

            if (level > 14) {
                level = 15;
                break;
            }
        }

        return level;
    }

    public boolean tryAddCandle(int xi, int zi, @NotNull CandleType type, int rotation) {
        if (this.candles.size() > 31) {
            return false;
        }

        if (this.level != null && type.getHeight() > 16) {
            BlockState above = this.level.getBlockState(this.getBlockPos().above());

            if (!above.isAir() || !above.canBeReplaced()) {
                return false;
            }
        }

        int x = Mth.clamp(xi, 1, 15);
        int z = Mth.clamp(zi, 1, 15);
        int size = type.getSize();
        int half = size / 2;

        if (isPlacePositionInvalid(x, z, size)) {
            return false;
        }

        for (int i = x - half; i < x - half + size; i++) {
            for (int j = z - half; j < z - half + size; j++) {
                if (this.isOccupied(i, j)) {
                    return false;
                }
            }
        }

        if (this.level != null && !this.level.isClientSide()) {
            short data = makeCandleData(x - half, z - half, type, rotation, false);
            this.addCandle(data);
            this.markUpdate();
        }
        return true;
    }

    public boolean tryRemoveCandle(int x, int z, @NotNull Player player) {
        int ix = Mth.clamp(x, 0, 15);
        int iz = Mth.clamp(z, 0, 15);

        if (this.removeCandle(ix, iz)) {
            if (this.level != null) {
                this.level.levelEvent(
                        player,
                        LevelEvent.PARTICLES_DESTROY_BLOCK,
                        this.getBlockPos(),
                        Block.getId(Blocks.CANDLE_CLUSTER.get().defaultBlockState())
                );
            }
            if (this.candles.isEmpty() && this.level != null) {
                this.level.removeBlock(this.getBlockPos(), false);
                this.level.levelEvent(
                        player,
                        LevelEvent.PARTICLES_DESTROY_BLOCK,
                        this.getBlockPos(),
                        Block.getId(Blocks.CANDLE_CLUSTER.get().defaultBlockState())
                );
            } else {
                this.markUpdate();
            }
            return true;
        }

        return false;
    }

    public boolean tryLitCandle(int x, int z) {
        int ix = Mth.clamp(x, 0, 15);
        int iz = Mth.clamp(z, 0, 15);

        int index = this.indexOf(ix, iz);
        if (index == -1) {
            return false;
        }

        short candle = this.candles.getShort(index);

        if (getCandleLit(candle)) {
            return false;
        }

        candle |= LIT_FLAG;
        this.candles.set(index, candle);
        this.lightedCandles.add(candle);
        this.markUpdate();

        if (this.level != null) {
            level.playSound(null, this.getBlockPos(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return true;
    }

    public boolean tryLitAny() {
        if (this.candles.size() == this.lightedCandles.size()) {
            return false;
        }

        for (int i = 0; i < this.candles.size(); i++) {
            short candle = this.candles.getShort(i);
            if (getCandleLit(candle)) {
                continue;
            }

            candle |= LIT_FLAG;
            this.candles.set(i, candle);
            this.lightedCandles.add(candle);

            this.markUpdate();
            if (this.level != null) {
                level.playSound(null, this.getBlockPos(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            return true;
        }

        return false;
    }

    public boolean tryExtinguishCandle(int x, int z) {
        int ix = Mth.clamp(x, 0, 15);
        int iz = Mth.clamp(z, 0, 15);

        int index = this.indexOf(ix, iz);
        if (index == -1) {
            return false;
        }

        short candle = this.candles.getShort(index);

        if (!getCandleLit(candle)) {
            return false;
        }

        this.lightedCandles.rem(candle);

        candle ^= LIT_FLAG;
        this.candles.set(index, candle);

        this.markUpdate();
        if (this.level != null) {
            level.playSound(null, this.getBlockPos(), SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return true;
    }

    public boolean tryExtinguishAll() {
        if (this.lightedCandles.isEmpty()) {
            return false;
        }

        this.lightedCandles.clear();
        for (int i = 0; i < this.candles.size(); i++) {
            short candle = this.candles.getShort(i);
            this.candles.set(i, (short) (candle ^ LIT_FLAG));
        }

        this.markUpdate();
        if (this.level != null) {
            level.playSound(null, this.getBlockPos(), SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return true;
    }

    public int indexOf(int x, int z) {
        int i;
        for (i = 0; i < this.candles.size(); i++) {
            short candle = this.candles.getShort(i);
            int candleX = getCandleX(candle);
            int candleZ = getCandleZ(candle);
            int size = getCandleType(candle).getSize();

            if (x >= candleX && x <= candleX + size && z >= candleZ && z <= candleZ + size) {
                break;
            }
        }

        if (i == this.candles.size()) {
            return -1;
        }

        return i;
    }

    public short getCandle(int x, int z) {
        int index = this.indexOf(x, z);
        if (index == -1) {
            return 0;
        } else {
            return this.candles.getShort(index);
        }
    }

    private void addCandle(short bits) {
        int x = getCandleX(bits);
        int z = getCandleZ(bits);
        CandleType type = getCandleType(bits);
        int size = type.getSize();

        this.candles.add(bits);

        if (getCandleLit(bits)) {
            this.lightedCandles.add(bits);
        }

        for (int i = x; i < Math.min(x + size, 15); i++) {
            for (int j = z; j < Math.min(z + size, 15); j++) {
                this.setOccupy(i, j);
            }
        }

        this.buildShape();
    }

    private boolean removeCandle(int x, int z) {
        if (this.candles.isEmpty()) {
            return false;
        }

        if (isPositionInvalid(x) || isPositionInvalid(z)) {
            return false;
        }

        int i = this.indexOf(x, z);

        if (i == -1) {
            return false;
        }

        short bits = this.candles.getShort(i);

        int candleX = getCandleX(bits);
        int candleZ = getCandleZ(bits);

        this.candles.removeShort(i);

        if (getCandleLit(bits)) {
            this.lightedCandles.rem(bits);
        }

        CandleType type = getCandleType(bits);
        final int size = type.getSize();
        for (int k = candleX; k < candleX + size; k++) {
            for (int j = candleZ; j < candleZ + size; j++) {
                this.setEmpty(k, j);
            }
        }

        this.buildShape();
        return true;
    }

    private void buildShape() {
        if (this.candles.isEmpty()) {
            this.lowerShape = Shapes.empty();
        } else {
            this.lowerShape = this.candles.intStream()
                    .skip(1)
                    .mapToObj(CandleClusterBlockEntity::getLowerCandleShape)
                    .reduce(getLowerCandleShape(this.candles.getShort(0)), Shapes::or);
        }
    }

    private static VoxelShape getLowerCandleShape(int i) {
        return getLowerCandleShape((short) i);
    }

    private static VoxelShape getLowerCandleShape(final short candle) {
        final int x = getCandleX(candle);
        final int z = getCandleZ(candle);
        final CandleType type = getCandleType(candle);
        final int size = type.getSize();
        return Block.box(
                x, 0, z,
                x + size, type.getHeight(),z + size
        );
    }

    /** unchecked **/
    private void setOccupy(int x, int z) {
        int index = z / 2;
        int bit = (z % 2) * 16 + x;
        this.grid[index] |= (1 << bit);
    }

    /** unchecked **/
    private void setEmpty(int x, int z) {
        int index = z / 2;
        int bit = (z % 2) * 16 + x;
        this.grid[index] ^= (1 << bit);
    }
    public boolean isOccupied(int x, int z) {
        if (isPositionInvalid(x) || isPositionInvalid(z)) {
            return false;
        }

        int index = z / 2;
        int bit = (z % 2) * 16 + x;

        return (this.grid[index] & (1 << bit)) != 0;
    }

    public static short makeCandleData(int x, int z, CandleType type, int rotation, boolean lit) {
        return (short) ((lit ? LIT_FLAG : 0) | (clampRotation(rotation) << 12) | (type.ordinal() << 8) | (x << 4) | z);
    }

    public static int getCandleX(short bits) {
        return (bits >>> 4) & 0xf;
    }

    public static int getCandleZ(short bits) {
        return bits & 0xf;
    }

    public static CandleType getCandleType(short bits) {
        return CandleType.values()[(bits >> 8) & 0xf];
    }

    public static int getCandleRotation(short bits) {
        return (bits >>> 12) & 0x7;
    }

    public static boolean getCandleLit(short bits) {
        return (bits & LIT_FLAG) != 0;
    }

    public static int clampRotation(int rotation) {
        if (rotation < 0) {
            return rotation - (rotation / MAX_ROTATION - 1) * MAX_ROTATION;
        } else {
            return rotation % MAX_ROTATION;
        }
    }

    private static boolean isPositionInvalid(int p) {
        return p < 0 || p > 15;
    }

    public static boolean isPlacePositionInvalid(int x, int z, int size) {
        int half = size / 2;
        int right = 16 - half;
        return x < half || x > right || z < half || z > right;
    }

    @NotNull
    public ShortArrayList getCandles() {
        return this.candles;
    }

    public ShortArrayList getLightedCandles() {
        return lightedCandles;
    }

    @NotNull
    public VoxelShape getShape() {
        return lowerShape;
    }

    @Override
    public void onPickup(ServerPlayer player) {
        int sum = this.candles.intStream()
                .map(new AbstractShort2IntFunction() {
                    @Override
                    public int get(short key) {
                        return CandleClusterBlockEntity.getCandleType(key).getSize();
                    }
                })
                .sum();

        player.addItem(new ItemStack(Items.CANDLE, sum / 4));
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    @Override
    protected World2ScreenButton createButton() {
        return new PickupCandleW2SButton(this);
    }

    @Override
    public boolean isDailyRefresh() {
        return true;
    }

    public boolean isLighted() {
        return !this.lightedCandles.isEmpty();
    }
}
