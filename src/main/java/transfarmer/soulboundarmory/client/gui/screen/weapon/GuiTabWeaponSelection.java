package transfarmer.soulboundarmory.client.gui.screen.weapon;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.Items;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulItemHelper;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiTab;
import transfarmer.soulboundarmory.client.gui.screen.common.GuiTabSoulbound;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.network.C2S.C2SItemType;
import transfarmer.soulboundarmory.util.ItemUtil;

import java.util.List;

import static transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider.WEAPONS;

public class GuiTabWeaponSelection extends GuiTabSoulbound {
    public GuiTabWeaponSelection(final List<GuiTab> tabs) {
        super(WEAPONS, tabs);
    }

    @Override
    protected String getLabel() {
        return Mappings.MENU_SELECTION;
    }

    @Override
    public void initGui() {
        super.initGui();

        final int width = 128;
        final int height = 20;
        final int xCenter = (this.width - width) / 2;
        final int yCenter = (this.height - height) / 2;
        final int ySep = 32;

        final GuiButton[] weaponButtons = {
                this.addButton(new GuiButton(0, xCenter, yCenter + (int) (1.5 * ySep), width, height, Mappings.SOUL_DAGGER_NAME)),
                this.addButton(new GuiButton(1, xCenter, yCenter + (int) (0.5 * ySep), width, height, Mappings.SOUL_SWORD_NAME)),
                this.addButton(new GuiButton(2, xCenter, yCenter - (int) (0.5 * ySep), width, height, Mappings.SOUL_GREATSWORD_NAME)),
                this.addButton(new GuiButton(3, xCenter, yCenter - (int) (1.5 * ySep), width, height, Mappings.SOUL_STAFF_NAME)),
        };

        if (SoulItemHelper.hasSoulWeapon(this.mc.player)) {
            weaponButtons[this.capability.getIndex()].enabled = false;
        } else if (this.capability.getItemType() != null && !ItemUtil.hasItem(Items.WOODEN_SWORD, this.mc.player)) {
            for (final GuiButton button : weaponButtons) {
                button.enabled = false;
            }
        }
    }


    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (!SoulItemHelper.hasSoulWeapon(this.mc.player)) {
            this.drawCenteredString(this.fontRenderer, Mappings.MENU_SELECTION,
                    Math.round(width / 2F), 40, 0xFFFFFF);
        }
    }

    @Override
    public void actionPerformed(final @NotNull GuiButton button) {
        super.actionPerformed(button);

        switch (button.id) {
            case 0:
            case 1:
            case 2:
            case 3:
                Main.CHANNEL.sendToServer(new C2SItemType(this.capability.getType(), this.capability.getItemType(button.id)));
                break;
        }
    }
}
