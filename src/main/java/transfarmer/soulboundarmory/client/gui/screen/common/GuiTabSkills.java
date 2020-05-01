package transfarmer.soulboundarmory.client.gui.screen.common;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundCapability;
import transfarmer.soulboundarmory.client.i18n.Mappings;
import transfarmer.soulboundarmory.skill.Skill;
import transfarmer.soulboundarmory.skill.SkillLevelable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.SKILL_POINTS;

@SideOnly(CLIENT)
public class GuiTabSkills extends GuiTabSoulbound {
    protected static final TextureAtlasSprite BACKGROUND = GuiExtended.getSprite(Blocks.STONE, 5);
    protected static final ResourceLocation BACKGROUND_TEXTURE = GuiExtended.getTexture(BACKGROUND);
    protected static final ResourceLocation WINDOW = new ResourceLocation("textures/gui/advancements/window.png");
    protected static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/advancements/widgets.png");

    protected final Map<Skill, List<Integer>> skills;
    protected Skill selectedSkill;
    protected float chroma;
    protected int windowWidth;
    protected int windowHeight;
    protected int insideWidth;
    protected int insideHeight;
    protected int centerX;
    protected int centerY;
    protected int insideX;
    protected int insideY;
    protected int x;
    protected int y;

    public GuiTabSkills(final Capability<? extends SoulboundCapability> key, final List<GuiTab> tabs) {
        super(key, tabs);

        this.skills = new LinkedHashMap<>();
    }

    @Override
    protected String getLabel() {
        return Mappings.MENU_BUTTON_SKILLS;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.chroma = 1;
        this.windowWidth = 256;
        this.windowHeight = 192;
        this.insideWidth = this.windowWidth - 23;
        this.insideHeight = this.windowHeight - 27;
        this.centerX = Math.max(this.button.endX + this.windowWidth / 2 + 4, this.width / 2);
        this.centerY = Math.min(this.getXPBarY() - 16 - this.windowHeight / 2, this.height / 2);
        this.insideX = this.centerX - this.insideWidth / 2;
        this.insideY = this.centerY - this.insideHeight / 2;
        this.x = this.centerX - this.windowHeight / 2;
        this.y = this.centerY - this.windowWidth / 2;

        this.updateIcons();
    }

    @Override
    protected void addSliders() {
        super.addSliders();

        if (this.sliderAlpha.x < this.x + this.windowWidth) {
            for (final GuiSlider slider : this.sliders) {
                this.buttonList.remove(slider);
            }
        }
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        this.drawWindow(mouseX, mouseY, partialTicks);
        this.drawSkills(mouseX, mouseY);
    }

    public void drawWindow(final int mouseX, final int mouseY, final float partialTicks) {
        final int x = this.centerX - this.windowWidth / 2;
        final int y = this.centerY - this.windowHeight / 2;
        final int rightX = this.centerX + this.insideWidth / 2;

        this.setChroma(this.chroma);
        GlStateManager.enableBlend();

        TEXTURE_MANAGER.bindTexture(BACKGROUND_TEXTURE);
        GuiExtended.drawModalRectWithCustomSizedTexture(x + 4, y + 4, 0, 0, this.windowWidth - 8, this.windowHeight - 8, BACKGROUND.getIconWidth(), BACKGROUND.getIconHeight(), -250);

        this.setChroma(1F);
        TEXTURE_MANAGER.bindTexture(WINDOW);
        GuiExtended.drawVerticalInterpolatedTexturedRect(x, y, 0, 0, 22, 126, 140, this.windowWidth, this.windowHeight, -200);

        final int color = 0x404040;
        final int points = this.capability.getDatum(this.item, SKILL_POINTS);

        FONT_RENDERER.drawString(Mappings.MENU_BUTTON_SKILLS, x + 8, y + 6, color);

        if (points > 0) {
            final String text = String.format("%s: %d", Mappings.MENU_UNSPENT_POINTS, points);

            FONT_RENDERER.drawString(text, rightX - FONT_RENDERER.getStringWidth(text), y + 6, color);
        }

        final float delta = 20F * partialTicks / 255F;

        this.chroma = (this.isSkillSelected(mouseX, mouseY)
                ? Math.max(this.chroma - delta, 175F / 255F)
                : Math.min(this.chroma + delta, 1F)
        );
    }

    protected void drawSkills(final int mouseX, final int mouseY) {
        final Skill[] skills = this.skills.keySet().toArray(new Skill[0]);

        for (int i = 0, skillsLength = skills.length; i < skillsLength; i++) {
            final Skill skill = skills[i];

            if (this.isMouseOverSkill(skill, mouseX, mouseY)) {
                this.selectedSkill = skill;
            } else {
                this.drawSkill(skill, mouseX, mouseY);
            }

            if (i == skillsLength - 1 && this.selectedSkill != null) {
                this.drawSkill(this.selectedSkill, mouseX, mouseY);
            }
        }
    }

    protected void drawSkill(final Skill skill, final int mouseX, final int mouseY) {
        final List<Integer> positions = this.skills.get(skill);

        if (positions != null) {
            this.drawSkill(skill, mouseX, mouseY, positions.get(0), positions.get(1));
        }
    }

    protected void drawSkill(final Skill skill, final int mouseX, final int mouseY, int x, int y) {
        final int width = 16;
        final int height = 16;
        final int offsetV = skill.isLearned() ? 26 : 0;
        final int color;
        x -= width / 2;
        y -= height / 2;

        if (skill == this.selectedSkill) {
            this.setChroma(1);

            if (this.isMouseOverSkill(skill, mouseX, mouseY)) {
                this.drawTooltip(skill, x, y, offsetV);
            }

            color = 0xFFFFFFFF;
        } else {
            this.setChroma(this.chroma);
            this.zLevel -= 50;

            color = new Color(this.chroma, this.chroma, this.chroma).getRGB();
        }

        TEXTURE_MANAGER.bindTexture(WIDGETS);
        this.drawTexturedModalRect(x - 4, y - 4, 1, 155 - offsetV, 24, 24);

        skill.getGUI().render(x, y, color, this.zLevel);

        this.zLevel = 0;
    }

    protected void drawTooltip(final Skill skill, final int posX, final int posY, final int offsetV) {
        final String name = skill.getName();
        List<String> tooltip = skill.getTooltip();
        int barWidth = 36 + FONT_RENDERER.getStringWidth(name);

        if (tooltip != null) {
            int size = tooltip.size();

            if (size > 0) {
                final boolean learned = skill.isLearned();
                final boolean aboveCenter = posY > this.centerY;
                final int direction = aboveCenter ? -1 : 1;
                final int y = posY + (aboveCenter ? -16 : 14);
                final int textY = y + (aboveCenter ? -5 : 7);
                String string = "";

                if (!learned) {
                    final int cost = skill.getCost();
                    final String plural = cost > 1 ? Mappings.MENU_POINTS : Mappings.MENU_POINT;
                    string = String.format(Mappings.MENU_SKILL_LEARN_COST, cost, plural);
                } else if (skill instanceof SkillLevelable) {
                    string = String.format("%s %d", Mappings.MENU_LEVEL, ((SkillLevelable) skill).getLevel());
                }

                barWidth = 12 + Math.max(barWidth, 8 + FONT_RENDERER.getStringWidth(string));
                tooltip = GuiExtended.wrap(barWidth, tooltip.toArray(new String[0]));
                size = tooltip.size();
                barWidth = Math.max(barWidth, 8 + FONT_RENDERER.getStringWidth(tooltip.stream().max(Comparator.comparingInt(String::length)).get()));
                final int tooltipHeight = 1 + (1 + size) * FONT_RENDERER.FONT_HEIGHT;

                if (!learned || skill instanceof SkillLevelable) {
                    this.setChroma(1);

                    TEXTURE_MANAGER.bindTexture(WIDGETS);
                    GuiExtended.drawHorizontalInterpolatedTexturedRect(posX - 8, y + tooltipHeight, 0, 55, 2, 198, 200, barWidth, 20, this.zLevel);

                    FONT_RENDERER.drawString(string, posX - 3, textY + direction * (size + 1) * FONT_RENDERER.FONT_HEIGHT, 0x999999);
                }

                this.setChroma(1);

                TEXTURE_MANAGER.bindTexture(WIDGETS);
                GuiExtended.drawInterpolatedTexturedRect(posX - 8, y, 0, 55, 2, 57, 198, 73, 200, 75, barWidth, tooltipHeight, this.zLevel);

                for (int i = 0; i < size; i++) {
                    FONT_RENDERER.drawString(tooltip.get(i), posX - 3, textY + direction * i * FONT_RENDERER.FONT_HEIGHT, 0x999999);
                }
            }

            this.setChroma(1);

            TEXTURE_MANAGER.bindTexture(WIDGETS);
            GuiExtended.drawHorizontalInterpolatedTexturedRect(posX - 8, posY - 2, 0, 29 - offsetV, 2, 198, 200, barWidth, 20, this.zLevel);

            FONT_RENDERER.drawString(name, posX + 24, posY + 4, 0xFFFFFF);
        }
    }

    protected boolean isSkillSelected(final int mouseX, final int mouseY) {
        return this.getSelectedSkill(mouseX, mouseY) != null;
    }

    protected Skill getSelectedSkill(final int mouseX, final int mouseY) {
        for (final Skill skill : this.skills.keySet()) {
            if (this.isMouseOverSkill(skill, mouseX, mouseY)) {
                return skill;
            }
        }

        return null;
    }

    protected boolean isMouseOverSkill(final Skill skill, final int mouseX, final int mouseY) {
        final List<Integer> positions = this.skills.get(skill);

        return Math.abs(positions.get(0) - mouseX) <= 12 && Math.abs(positions.get(1) - mouseY) <= 12;
    }

    protected void setChroma(final float chroma) {
        GlStateManager.color(chroma, chroma, chroma);
    }

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        final Skill skill = this.getSelectedSkill(mouseX, mouseY);

        if (skill != null) {
            this.capability.upgradeSkill(this.item, skill);
        }
    }

    @Override
    public void refresh() {
        final float chroma = this.chroma;

        super.refresh();

        this.chroma = chroma;
    }

    protected void updateIcons() {
        this.skills.clear();

        final Map<Integer, List<Integer>> tierOrders = new HashMap<>();
        final List<Skill> skills = this.capability.getSkills(this.item);

        for (final Skill skill : skills) {
            final int tier = skill.getTier();
            final List<Integer> data = tierOrders.getOrDefault(tier, new ArrayList<>(Collections.nCopies(3, 0)));

            if (!tierOrders.containsValue(data)) {
                tierOrders.put(tier, data);
            }

            data.set(1, data.get(1) + 1);
        }

        int width = 2;

        for (final int tier : tierOrders.keySet()) {
            final int tiers = tierOrders.get(tier).get(1);

            if (tier != 0 && tiers != 0) {
                width += tiers - 1;
            }
        }

        for (final Skill skill : skills) {
            final int tier = skill.getTier();
            final List<Integer> data = tierOrders.getOrDefault(tier, new ArrayList<>(Collections.nCopies(3, 0)));
            data.set(0, data.get(0) + 1);

            if (!tierOrders.containsValue(data)) {
                tierOrders.put(tier, data);
            }

            final int spacing = !skill.hasDependencies() ? width * 24 : 48;
            final int offset = (1 - data.get(1)) * spacing / 2;
            int x = offset + (data.get(0) - 1) * spacing;

            final List<Skill> dependencies = skill.getDependencies();

            if (skill.hasDependencies()) {
                int total = 0;

                for (final Skill other : dependencies) {
                    total += this.skills.get(other).get(0);
                }

                x += total / dependencies.size();
            } else {
                x += this.centerX;
            }

            this.skills.put(skill, Arrays.asList(x, this.insideY + 24 + 32 * tier));
        }
    }
}
