package net.Sokra21.WRising.Werewolf;

import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import net.Sokra21.WRising.WRising;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, WRising.MODID);

    public static final Supplier<AttachmentType<WerewolfStage>> STAGE =
            ATTACHMENTS.register("stage", () ->
                    AttachmentType.builder(() -> WerewolfStage.HUMAN)
                            .serialize(Codec.STRING.xmap(WerewolfStage::valueOf, WerewolfStage::name))
                            .copyOnDeath()
                            .build());

    public static final Supplier<AttachmentType<Boolean>> TRANSFORMED =
            ATTACHMENTS.register("transformed", () ->
                    AttachmentType.builder(() -> false)
                            .serialize(Codec.BOOL)
                            .sync(ByteBufCodecs.BOOL)
                            .build());
}
