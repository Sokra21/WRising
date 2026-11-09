package net.Sokra21.WRising;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.Sokra21.WRising.Werewolf.WerewolfStage;
import net.Sokra21.WRising.Werewolf.ModAttachments;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.Sokra21.WRising.Werewolf.ModAttachments;
import net.Sokra21.WRising.Werewolf.WerewolfStage;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import java.util.Arrays;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;


// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(WRising.MODID)
public class WRising {
    public static final String MODID = "wrising";
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public WRising(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        net.Sokra21.WRising.Werewolf.ModAttachments.ATTACHMENTS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Esta talla ha cargado chaval");

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

        }
    @SubscribeEvent
    public void onPLayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        WerewolfStage stage = event.getEntity().getData(net.Sokra21.WRising.Werewolf.ModAttachments.STAGE);
        LOGGER.info("Stage of {}: {}", event.getEntity() .getName().getString(), stage);

    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {

        event.getDispatcher().register(
                Commands.literal("transform")
                        .requires(source -> source.hasPermission(2))
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            boolean newState = !player.getData(ModAttachments.TRANSFORMED);
                            player.setData(ModAttachments.TRANSFORMED, newState);
                            ctx.getSource().sendSuccess(() -> Component.literal(newState ? "Transformed" : "Human"), false);
                            return 1;
                        }));

        event.getDispatcher().register(
                Commands.literal("wrising")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("stage", StringArgumentType.word())
                                .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                                        Arrays.stream(WerewolfStage.values()).map(s -> s.name().toLowerCase()), builder))
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    String stageName = StringArgumentType.getString(ctx, "stage").toUpperCase();
                                    try {
                                        WerewolfStage stage = WerewolfStage.valueOf(stageName);
                                        player.setData(ModAttachments.STAGE, stage);
                                        ctx.getSource().sendSuccess(() -> Component.literal("Stage changed to " + stage), false);
                                        return 1;
                                    } catch (IllegalArgumentException e) {
                                        ctx.getSource().sendFailure(Component.literal("Invalid stage"));
                                        return 0;
                                    }
                                })));
    }
}