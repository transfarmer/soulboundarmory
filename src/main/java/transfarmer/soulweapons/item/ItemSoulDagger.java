package transfarmer.soulweapons.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import transfarmer.soulweapons.entity.EntitySoulDagger;

import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulweapons.data.SoulWeaponDatum.SKILLS;
import static transfarmer.soulweapons.data.SoulWeaponType.DAGGER;

public class ItemSoulDagger extends ItemSoulWeapon {
    public ItemSoulDagger() {
        super(1, -2F, 3);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote) {
            if (player.getCapability(CAPABILITY, null).getDatum(SKILLS, DAGGER) >= 1) {
                final ItemStack itemStack = player.getHeldItem(hand);
                final EntitySoulDagger dagger = new EntitySoulDagger(world, player, itemStack);

                if (!player.isCreative()) {
                    player.inventory.deleteStack(itemStack);
                }

                dagger.shoot(player, player.rotationPitch, player.rotationYaw, 0, 1.5F, 1);
                world.spawnEntity(dagger);

                return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
            }
        }

        return new ActionResult<>(EnumActionResult.FAIL, player.getHeldItem(hand));
    }
}
