package soulboundarmory.mixin.mixin.roman;

import net.minecraft.util.text.LanguageMap;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @see LanguageMap
 */
@Mixin(targets = "net.minecraft.util.text.LanguageMap$1")
abstract class LanguageDummyMixin {}
