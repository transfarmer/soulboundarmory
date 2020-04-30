package transfarmer.soulboundarmory.skill;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.client.gui.screen.common.skill.GuiSkill;
import transfarmer.soulboundarmory.statistics.Skills;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;

import javax.annotation.Nonnull;
import java.util.List;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public interface Skill extends INBTSerializable<NBTTagCompound> {
    @Nonnull
    List<Skill> getDependencies();

    boolean hasDependencies();

    int getTier();

    int getCost();

    boolean isLearned();

    boolean canBeLearned();

    boolean canBeLearned(int points);

    void learn();

    ResourceLocation getTexture();

    GuiSkill getGUI();

    @NotNull
    String getModID();

    String getRegistryName();

    void setStorage(Skills storage, IItem item);

    @SideOnly(CLIENT)
    String getName();

    @SideOnly(CLIENT)
    List<String> getTooltip();
}
