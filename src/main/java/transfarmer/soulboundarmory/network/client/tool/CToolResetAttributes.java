package transfarmer.soulboundarmory.network.client.tool;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.capability.tool.SoulToolProvider;
import transfarmer.soulboundarmory.client.gui.SoulToolMenu;
import transfarmer.soulboundarmory.statistics.SoulDatum;
import transfarmer.soulboundarmory.statistics.SoulType;
import transfarmer.soulboundarmory.statistics.tool.SoulToolType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class CToolResetAttributes implements IMessage {
    private int index;

    public CToolResetAttributes() {}

    public CToolResetAttributes(final SoulType type) {
        this.index = type.getIndex();
    }

    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.index = buffer.readInt();
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.index);
    }

    public static final class Handler implements IMessageHandler<CToolResetAttributes, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final CToolResetAttributes message, final MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                final ISoulCapability capability = SoulToolProvider.get(Minecraft.getMinecraft().player);
                final SoulType type = SoulToolType.get(message.index);

                capability.addDatum(capability.getDatum(SoulDatum.SPENT_ATTRIBUTE_POINTS, type), SoulDatum.ATTRIBUTE_POINTS, type);
                capability.setDatum(0, SoulDatum.SPENT_ATTRIBUTE_POINTS, type);
                capability.setAttributes(new float[capability.getAttributeAmount()], type);
                Minecraft.getMinecraft().displayGuiScreen(new SoulToolMenu());
            });

            return null;
        }
    }
}
