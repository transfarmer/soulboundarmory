package soulboundarmory.skill.weapon.dagger;

import net.minecraft.util.Identifier;
import soulboundarmory.registry.Skills;
import soulboundarmory.skill.Skill;

public class ReturnSkill extends Skill {
    public ReturnSkill(Identifier identifier) {
        super(identifier);
    }

    @Override
    public void initDependencies() {
        this.dependencies.add(Skills.throwing);

        super.initDependencies();
    }

    @Override
    public int cost(boolean learned, int level) {
        return 2;
    }
}
