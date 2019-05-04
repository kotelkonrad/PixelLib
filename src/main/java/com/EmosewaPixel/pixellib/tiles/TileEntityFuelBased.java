package com.EmosewaPixel.pixellib.tiles;

import com.EmosewaPixel.pixellib.blocks.BlockMachineFuelBased;
import com.EmosewaPixel.pixellib.recipes.MachineRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public class TileEntityFuelBased extends TileEntityRecipeBased {
    private int burnTime = 0;
    private int maxBurnTime = 0;

    public void setBurnTime(int i) {
        burnTime = i;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public void setMaxBurnTime(int i) {
        maxBurnTime = i;
    }

    public int getMaxBurnTime() {
        return maxBurnTime;
    }

    public TileEntityFuelBased(TileEntityType type, int inputCount, int outputCount, ArrayList<MachineRecipe> recipes) {
        super(type, inputCount, outputCount, recipes);

        fuel_input = new ItemStackHandler(1) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return getItemBurnTime(stack) > 0;
            }

            @Override
            protected void onContentsChanged(int slot) {
                TileEntityFuelBased.this.markDirty();
            }
        };

        combinedHandler = new CombinedInvWrapper(input, fuel_input, output);
    }


    public static ItemStackHandler fuel_input;

    @Override
    public void tick() {
        if (!world.isRemote) {
            if (burnTime > 0) {
                burnTime--;
                world.setBlockState(pos, world.getBlockState(pos).with(BlockMachineFuelBased.LIT, true));
                super.tick();
            } else {
                if (!fuel_input.getStackInSlot(0).isEmpty())
                    consumeFuel();
                else
                    world.setBlockState(pos, world.getBlockState(pos).with(BlockMachineFuelBased.LIT, false));
            }
            markDirty();
        }
    }

    protected int getItemBurnTime(ItemStack stack) {
        if (stack.isEmpty())
            return 0;
        int rec = stack.getBurnTime();
        return ForgeEventFactory.getItemBurnTime(stack, rec == -1 ? TileEntityFurnace.getBurnTimes().getOrDefault(stack.getItem(), 0) : rec);
    }

    protected void consumeFuel() {
        if (!getCurrentRecipe().isEmpty() && canOutput(getCurrentRecipe(), true)) {
            burnTime = maxBurnTime = getItemBurnTime(fuel_input.getStackInSlot(0));
            if (burnTime > 0) {
                if (fuel_input.getStackInSlot(0).getItem().hasContainerItem())
                    fuel_input.setStackInSlot(0, new ItemStack(fuel_input.getStackInSlot(0).getItem().getContainerItem()));
                else
                    fuel_input.extractItem(0, 1, false);
            }
        } else
            world.setBlockState(pos, world.getBlockState(pos).with(BlockMachineFuelBased.LIT, false));
    }

    @Override
    public void read(NBTTagCompound compound) {
        super.read(compound);
        if (compound.hasKey("FuelItems"))
            fuel_input.deserializeNBT((NBTTagCompound) compound.getTag("FuelItems"));
        burnTime = compound.getInt("BurnTime");
        maxBurnTime = compound.getInt("MaxBurnTime");
    }

    @Override
    public NBTTagCompound write(NBTTagCompound compound) {
        super.write(compound);
        compound.setTag("FuelItems", fuel_input.serializeNBT());
        compound.setInt("BurnTime", burnTime);
        compound.setInt("MaxBurnTime", maxBurnTime);
        return compound;
    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        return playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            if (side == EnumFacing.EAST || side == EnumFacing.WEST || side == EnumFacing.NORTH || side == EnumFacing.SOUTH)
                return LazyOptional.of(() -> this.fuel_input).cast();
        return super.getCapability(cap, side);
    }
}