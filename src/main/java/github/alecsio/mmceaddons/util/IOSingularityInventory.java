package github.alecsio.mmceaddons.util;

import hellfirepvp.modularmachinery.common.tiles.base.TileEntitySynchronized;
import hellfirepvp.modularmachinery.common.util.IOInventory;
import hellfirepvp.modularmachinery.common.util.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class IOSingularityInventory extends IOInventory {
    public IOSingularityInventory(
            TileEntitySynchronized owner,
            int[] inSlots,
            int[] outSlots,
            int maxSize
    ) {
        super(owner, inSlots, outSlots);
        Arrays.fill(this.slotLimits, maxSize);
    }

    public IOSingularityInventory(
            TileEntitySynchronized owner,
            int[] inSlots,
            int[] outSlots,
            EnumFacing... accessibleFrom
    ) {
        super(owner, inSlots, outSlots, accessibleFrom);
    }

    protected ItemStack insertItemInternal(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!this.allowAnySlots && !arrayContains(this.inSlots, slot)) {
            return stack;
        } else {
            SlotStackHolder holder = this.inventory[slot];
            if (holder == null) {
                return stack;
            } else {
                ItemStack toInsert = ItemUtils.copyStackWithSize(stack, stack.getCount());
                if (!((ItemStack)holder.itemStack.get()).isEmpty()) {
                    ItemStack existing = ItemUtils.copyStackWithSize((ItemStack)holder.itemStack.get(), ((ItemStack)holder.itemStack.get()).getCount());
                    int max = this.getSlotLimit(slot);
                    if (existing.getCount() < max && canMergeItemStacks(existing, toInsert)) {
                        int movable = Math.min(max - existing.getCount(), stack.getCount());
                        if (!simulate) {
                            ((ItemStack)holder.itemStack.get()).grow(movable);
                        }

                        if (movable >= stack.getCount()) {
                            return ItemStack.EMPTY;
                        } else {
                            ItemStack copy = stack.copy();
                            copy.shrink(movable);
                            return copy;
                        }
                    } else {
                        return stack;
                    }
                } else {
                    int max = this.getSlotLimit(slot);
                    if (max >= stack.getCount()) {
                        if (!simulate) {
                            holder.itemStack.set(stack.copy());
                        }

                        return ItemStack.EMPTY;
                    } else {
                        ItemStack copy = stack.copy();
                        copy.setCount(max);
                        if (!simulate) {
                            holder.itemStack.set(copy);
                        }

                        copy = stack.copy();
                        copy.shrink(max);
                        return copy;
                    }
                }
            }
        }
    }
}
