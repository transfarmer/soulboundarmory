package soulboundarmory.client.gui.screen;

import java.util.Map;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ReferenceLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceLinkedOpenHashMap;
import net.minecraft.util.Identifier;
import soulboundarmory.client.i18n.Translations;
import soulboundarmory.lib.gui.coordinate.Offset;
import soulboundarmory.lib.gui.widget.Widget;
import soulboundarmory.lib.gui.widget.scalable.ScalableWidget;
import soulboundarmory.skill.SkillInstance;

/**
 The skill tab; design (not code of course) blatantly copied from the advancement screen.
 */
public class SkillTab extends SoulboundTab {
    protected static final Identifier background = new Identifier("textures/block/andesite.png");

    protected final Map<SkillInstance, SkillWidget> skills = new Reference2ReferenceLinkedOpenHashMap<>();
    protected final ScalableWidget<?> window = new ScalableWidget<>().window().width(512).height(288)
        .text(text -> text.stroke().text(this.title).x(0, 8).y(6).color(0xEEEEEE))
        .text(text -> text.stroke().text(() -> this.pointText(this.container().item().skillPoints())).alignRight().alignUp().x(1, -15).y(25).color(0xEEEEEE));

    protected float chroma = 1;
    protected int insideWidth;
    protected int insideHeight;
    protected int insideCenterY;
    protected int insideX;
    protected int insideY;
    protected int insideEndX;
    protected int insideEndY;

    public SkillTab() {
        super(Translations.guiSkills);
    }

    @Override
    public void initialize() {
        if (!this.dim()) {
            this.chroma = 1;
        }

        this.window.x(Math.max(this.button.endX() + this.window.width() / 2 + 4, this.middleX())).y(Math.min(this.container().xpBar.y() - 16 - this.window.height() / 2, this.middleY())).center();
        this.insideWidth = this.window.width() - 18;
        this.insideHeight = this.window.height() - 27;
        this.insideCenterY = this.window.middleY() + 4;
        this.insideX = this.window.middleX() - this.insideWidth / 2;
        this.insideY = this.insideCenterY - this.insideHeight / 2;
        this.insideEndX = this.window.middleX() + this.insideWidth / 2;
        this.insideEndY = this.window.middleY() + this.insideHeight / 2;

        this.add(this.window);
        this.updateWidgets();

        if (!this.container().options.isEmpty()) {
            var option = this.container().options.get(0);

            if (option.x() < this.window.endX()) {
                this.remove(this.container().options);
            }
        }
    }

    @Override
    protected void render() {
        var delta = 20 * tickDelta() / 255F;
        this.chroma = this.dim() ? Math.max(this.chroma - delta, 175 / 255F) : Math.min(this.chroma + delta, 1);
        chroma(this.chroma);
        RenderSystem.enableBlend();
        this.renderBackground(background, this.insideX, this.insideY, this.insideWidth, this.insideHeight, (int) (128 * this.chroma));
    }

    private boolean dim() {
        return this.window.children().anyMatch(child -> child.tooltips.stream().anyMatch(Widget::isPresent));
    }

    private void updateWidgets() {
        var tierOrders = new Int2ReferenceLinkedOpenHashMap<int[]>();
        var skills = this.container().item().skills();

        for (var skill : skills) {
            var tier = skill.tier();
            var data = tierOrders.getOrDefault(tier, new int[]{0, 0, 0});

            if (!tierOrders.containsValue(data)) {
                tierOrders.put(tier, data);
            }

            data[1]++;
        }

        var width = 2;

        for (int tier : tierOrders.keySet()) {
            var tiers = tierOrders.get(tier)[1];

            if (tier != 0 && tiers != 0) {
                width += tiers - 1;
            }
        }

        for (var skill : skills) {
            var tier = skill.tier();
            var data = tierOrders.getOrDefault(tier, new int[]{0, 0, 0});
            data[0]++;

            if (!tierOrders.containsValue(data)) {
                tierOrders.put(tier, data);
            }

            var spacing = skill.hasDependencies() ? 48 : width * 24;
            var offset = (1 - data[1]) * spacing / 2;
            var x = offset + (data[0] - 1) * spacing;

            if (skill.hasDependencies()) {
                var dependencies = skill.dependencies;
                var total = 0;

                for (var other : dependencies) {
                    total += this.skills.get(other).x();
                }

                x += total / dependencies.size();
            } else {
                x += this.window.middleX();
            }

            this.skills.computeIfAbsent(skill, skil -> this.window.add(new SkillWidget(this, skil).size(24).center().offset(Offset.Type.ABSOLUTE))).x(x).y(this.insideY + 24 + 32 * tier);
        }
    }
}
