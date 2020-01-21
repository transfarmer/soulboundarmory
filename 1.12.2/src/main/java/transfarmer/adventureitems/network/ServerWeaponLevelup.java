package transfarmer.adventureitems.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.adventureitems.Main;
import transfarmer.adventureitems.capability.ISoulWeapon;

import static transfarmer.adventureitems.capability.SoulWeaponProvider.CAPABILITY;

public class ServerWeaponLevelup implements IMessage {
    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<ServerWeaponLevelup, IMessage> {
        public IMessage onMessage(ServerWeaponLevelup message, MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().player;
            ISoulWeapon instance = player.getCapability(CAPABILITY, null);

            if (player.experienceLevel > instance.getLevel()) {
                instance.addLevel();
                player.addExperienceLevel(-instance.getLevel());
                Main.CHANNEL.sendTo(new ClientWeaponLevelup(), player);
            }

            return null;
        }
    }
}
