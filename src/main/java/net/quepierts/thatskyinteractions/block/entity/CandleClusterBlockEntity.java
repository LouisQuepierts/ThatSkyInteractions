package net.quepierts.thatskyinteractions.block.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.quepierts.thatskyinteractions.block.CandleClusterBlock;
import net.quepierts.thatskyinteractions.block.CandleType;
import net.quepierts.thatskyinteractions.registry.BlockEntities;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CandleClusterBlockEntity extends AbstractUpdatableBlockEntity {
    public static final int MAX_ROTATION = 8;
    public static final float UNIT_ROTATION_RAD = Mth.HALF_PI / MAX_ROTATION;
    public static final float UNIT_ROTATION_DEG = 90.0f / MAX_ROTATION;
    private static final int GRID_LENGTH = 256 / 32;
    private static final short LIT_FLAG = (short) 0x8000;
    private static final String TAG_CANDLES = "candles";
    private final ShortArrayList candles;
    private final ShortArrayList lightedCandles;
    private final int[] grid;
    private final ObjectList<VoxelShape> shapes;

    @NotNull
    private VoxelShape shape = Shapes.empty();

    public CandleClusterBlockEntity( BlockPos pos, BlockState blockState) {
        super(BlockEntities.CANDLE_CLUSTER.get(), pos, blockState);
        this.grid = new int[GRID_LENGTH];
        this.candles = new ShortArrayList();
        this.shapes = new ObjectArrayList<>();
        this.lightedCandles = new ShortArrayList();
    }

    @Override
    public void toNBT(@NotNull CompoundTag tag) {
        int[] array = new int[this.candles.size()];

        for (int i = 0; i < this.candles.size(); i++) {
            array[i] = this.candles.getShort(i);
        }

        tag.putIntArray(TAG_CANDLES, array);
    }

    @Override
    public void fromNBT(@NotNull CompoundTag tag) {
        if (tag.contains(TAG_CANDLES, Tag.TAG_INT_ARRAY)) {
            Arrays.fill(this.grid, 0);
            this.candles.clear();
            this.shapes.clear();
            this.lightedCandles.clear();

            int[] array = tag.getIntArray(TAG_CANDLES);
            for (int bits : array) {
                this.addCandle((short) bits);
            }

            this.markUpdate();
        }
    }

    @Override
    public void markUpdate() {
        this.setChanged();
        if (this.level != null) {
            this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(CandleClusterBlock.LIT, !this.lightedCandles.isEmpty()), 0);
            this.level.sendBlockUpdated(
                    this.getBlockPos(),
                    this.getBlockState(),
                    this.getBlockState(),
                    Block.UPDATE_ALL
            );
        }
    }

    public boolean tryAddCandle(int xi, int zi, CandleType type, int rotation) {
        int x = Mth.clamp(xi, 1, 15);
        int z = Mth.clamp(zi, 1, 15);
        int size = type.getSize();
        int half = size / 2;

        if (isPlacePositionInvalid(x, size) || isPlacePositionInvalid(z, size)) {
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

    public boolean tryRemoveCandle(int x, int z) {
        if (isPositionInvalid(x) || isPositionInvalid(z)) {
            return false;
        }

        if (this.removeCandle(x, z)) {
            if (this.candles.isEmpty() && this.level != null) {
                this.level.setBlock(this.getBlockPos(), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            } else {
                this.markUpdate();
            }
            return true;
        }

        return false;
    }

    public boolean tryLitCandle(int x, int z) {
        if (isPlacePositionInvalid(x) || isPlacePositionInvalid(z)) {
            return false;
        }

        for (int i = 0; i < this.candles.size(); i++) {
            short candle = this.candles.getShort(i);
            int candleX = getCandleX(candle);
            int candleZ = getCandleZ(candle);

            if (x >= candleX && x <= candleX + 2 && z >= candleZ && z <= candleZ + 2) {
                if (!getCandleLit(candle)) {
                    candle |= LIT_FLAG;
                    this.candles.set(i, candle);
                    this.lightedCandles.add(candle);

                    this.markUpdate();
                    return true;
                }

                break;
            }
        }
        return false;
    }

    public boolean tryExtinguishCandle(int x, int z) {
        if (isPlacePositionInvalid(x) || isPlacePositionInvalid(z)) {
            return false;
        }

        for (int i = 0; i < this.candles.size(); i++) {
            short candle = this.candles.getShort(i);
            int candleX = getCandleX(candle);
            int candleZ = getCandleZ(candle);

            if (x >= candleX && x <= candleX + 2 && z >= candleZ && z <= candleZ + 2) {
                if (getCandleLit(candle)) {
                    candle ^= LIT_FLAG;
                    this.candles.set(i, candle);
                    this.lightedCandles.rem(candle);
                    this.markUpdate();
                    return true;
                }
                break;
            }
        }
        return false;
    }

    private void addCandle(short bits) {
        int x = getCandleX(bits);
        int z = getCandleZ(bits);
        CandleType type = getCandleType(bits);
        int size = type.getSize();

        this.candles.add(bits);
        this.shapes.add(Block.box(
                x, 0, z,
                x + size, type.getHeight(),z + size
        ));

        if (getCandleLit(bits)) {
            this.lightedCandles.add(bits);
        }

        this.buildShape();

        for (int i = x; i < x + size; i++) {
            for (int j = z; j < z + size; j++) {
                this.setOccupy(i, j);
            }
        }
    }

    private boolean removeCandle(int x, int z) {
        if (this.candles.isEmpty()) {
            return false;
        }

        if (isPositionInvalid(x) || isPositionInvalid(z)) {
            return false;
        }

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
            return false;
        }

        short bits = this.candles.getShort(i);

        int candleX = getCandleX(bits);
        int candleZ = getCandleZ(bits);

        this.shapes.remove(i);
        this.candles.removeShort(i);

        if (getCandleLit(bits)) {
            this.lightedCandles.rem(bits);
        }

        final int size = getCandleType(bits).getSize();
        for (int k = candleX; k < candleX + size; k++) {
            for (int j = candleZ; j < candleZ + size; j++) {
                this.setEmpty(k, j);
            }
        }

        this.buildShape();
        return true;
    }

    private void buildShape() {
        if (this.shapes.isEmpty()) {
            this.shape = Shapes.empty();
        } else {
            this.shape = this.shapes.get(0);
            for (int i = 1; i < this.shapes.size(); i++) {
                this.shape = Shapes.or(this.shape, this.shapes.get(i));
            }
        }
    }

    private short saveGet(int index) {
        return index < this.candles.size() ? this.candles.getShort(index) : 0;
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
        return (bits >>> 12) & 0xb;
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

    private static boolean isPlacePositionInvalid(int p) {
        return p < 0 || p > 14;
    }

    private static boolean isPlacePositionInvalid(int p, int size) {
        int half = size / 2;
        return p < half || p > 16 - half;
    }

    private static boolean isPositionInvalid(int p) {
        return p < 0 || p > 15;
    }

    @NotNull
    public ShortArrayList getCandles() {
        return this.candles;
    }

    @NotNull
    public VoxelShape getShape() {
        return shape;
    }

    public ShortArrayList getLightedCandles() {
        return lightedCandles;
    }
}
