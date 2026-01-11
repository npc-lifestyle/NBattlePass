package wtf.n1zamu.util;

import lombok.experimental.UtilityClass;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class PlayerCraftUtility {
    public int getMaxCraftAmount(CraftingInventory inv) {
        if (inv == null || inv.getResult() == null) {
            return 0;
        }
        int resultCount = inv.getResult().getAmount();
        int materialCount = Integer.MAX_VALUE;
        ItemStack[] matrix = inv.getMatrix();
        if (matrix == null) {
            return 0;
        }
        for (ItemStack is : matrix) {
            if (is == null) {
                continue;
            }
            if (is.getAmount() < materialCount) {
                materialCount = is.getAmount();
            }
        }
        return resultCount * materialCount;
    }

    public int fits(ItemStack stack, Inventory inv) {
        ItemStack[] contents = inv.getContents();
        int result = 0;
        for (ItemStack is : contents) {
            if (is == null) {
                result += stack.getMaxStackSize();
                continue;
            }
            if (!is.isSimilar(stack)) continue;
            result += Math.max(stack.getMaxStackSize() - is.getAmount(), 0);
        }
        return result;
    }
}
