package soulboundarmory.mixin.mixin.entity.attribute;

import java.util.UUID;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import soulboundarmory.util.AttributeModifierIdentifiers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityAttributeModifier.class)
abstract class AttributeModifierMixin {
    @Shadow
    @Final
    @Mutable
    private UUID id;

    @Inject(method = "<init>(Ljava/util/UUID;Ljava/lang/String;DLnet/minecraft/entity/ai/attributes/AttributeModifier$Operation;)V", at = @At("RETURN"))
    public void construct(UUID uuid, String name, double value, EntityAttributeModifier.Operation operation, CallbackInfo info) {
        UUID original = AttributeModifierIdentifiers.get(uuid);

        if (original != null) {
            this.id = original;
        }
    }
}
