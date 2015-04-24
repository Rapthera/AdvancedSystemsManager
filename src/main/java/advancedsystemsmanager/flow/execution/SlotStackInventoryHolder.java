package advancedsystemsmanager.flow.execution;


import advancedsystemsmanager.api.execution.IItemBufferSubElement;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotStackInventoryHolder implements IItemBufferSubElement
{
    public ItemStack itemStack;
    public IInventory inventory;
    public int slot;
    public int sizeLeft;

    public SlotStackInventoryHolder(ItemStack itemStack, IInventory inventory, int slot)
    {
        this.itemStack = itemStack;
        this.inventory = inventory;
        this.slot = slot;
        this.sizeLeft = itemStack.stackSize;
    }

    public ItemStack getItemStack()
    {
        return itemStack;
    }


    public IInventory getInventory()
    {
        return inventory;
    }


    public int getSlot()
    {
        return slot;
    }

    @Override
    public void remove()
    {
        if (itemStack.stackSize == 0)
        {
            getInventory().setInventorySlotContents(getSlot(), null);
        }
    }

    @Override
    public void onUpdate()
    {
        getInventory().markDirty();
    }

    public int getSizeLeft()
    {
        return Math.min(itemStack.stackSize, sizeLeft);
    }

    @Override
    public void reduceBufferAmount(int amount)
    {
        itemStack.stackSize -= amount;
        sizeLeft -= amount;
    }

    @Override
    public void reduceContainerAmount(int amount)
    {
        getContainer().decrStackSize(getSlot(), amount);
        onUpdate();
    }

    @Override
    public ItemStack getValue()
    {
        return itemStack;
    }

    @Override
    public IInventory getContainer()
    {
        return inventory;
    }

    public void reduceAmount(int val)
    {
        itemStack.stackSize -= val;
        sizeLeft -= val;
    }

    public SlotStackInventoryHolder getSplitElement(int elementAmount, int id, boolean fair)
    {
        SlotStackInventoryHolder element = new SlotStackInventoryHolder(this.itemStack, this.inventory, this.slot);
        int oldAmount = getSizeLeft();
        int amount = oldAmount / elementAmount;
        if (!fair)
        {
            int amountLeft = oldAmount % elementAmount;
            if (id < amountLeft)
            {
                amount++;
            }
        }

        element.sizeLeft = amount;
        return element;
    }
}