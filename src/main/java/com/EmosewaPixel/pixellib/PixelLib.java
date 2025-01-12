package com.EmosewaPixel.pixellib;

import com.EmosewaPixel.pixellib.blocks.BlockRegistry;
import com.EmosewaPixel.pixellib.items.ItemRegistry;
import com.EmosewaPixel.pixellib.materialSystem.MaterialRegistry;
import com.EmosewaPixel.pixellib.materialSystem.materials.IMaterialItem;
import com.EmosewaPixel.pixellib.proxy.ClientProxy;
import com.EmosewaPixel.pixellib.proxy.IModProxy;
import com.EmosewaPixel.pixellib.proxy.ServerProxy;
import com.EmosewaPixel.pixellib.resourceAddition.DataAddition;
import com.EmosewaPixel.pixellib.resourceAddition.FakeDataPackFinder;
import com.EmosewaPixel.pixellib.resourceAddition.RecipeInjector;
import com.EmosewaPixel.pixellib.worldgen.OreGen;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PixelLib.ModId)
public class PixelLib {
    public static final String ModId = "pixellib";
    public static final Logger LOGGER = LogManager.getLogger();
    private static IModProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

    public static ItemGroup main = new ItemGroup(ModId) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(BlockRegistry.getGroupIcon());
        }
    };

    public PixelLib() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        MinecraftForge.EVENT_BUS.register(this);

        new MaterialRegistry();
    }

    private void clientSetup(final FMLClientSetupEvent eventi) {
        ClientProxy.setup();
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        proxy.enque(event);
        OreGen.register();
    }

    private void processIMC(final InterModProcessEvent event) {
        proxy.process(event);
        DataAddition.register();
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = ModId)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> e) {
            ItemRegistry.registry(e);
            BlockRegistry.itemRegistry(e);
        }

        @SubscribeEvent
        public static void onBlockRegistry(final RegistryEvent.Register<Block> e) {
            BlockRegistry.registry(e);
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = ModId)
    public static class GaneEvents {
        @SubscribeEvent
        public static void onServerAboutToStart(FMLServerAboutToStartEvent e) {
            e.getServer().getResourcePacks().addPackFinder(new FakeDataPackFinder());
            e.getServer().getResourceManager().addReloadListener(new RecipeInjector(e.getServer().getRecipeManager()));
        }

        @SubscribeEvent
        public static void fuelTime(FurnaceFuelBurnTimeEvent e) {
            Item item = e.getItemStack().getItem();
            if (item instanceof BlockItem) {
                Block block = Block.getBlockFromItem(item);
                if (block instanceof IMaterialItem)
                    if (!((IMaterialItem) block).getObjType().hasTag(MaterialRegistry.HAS_NO_FUEL_VALUE) && ((IMaterialItem) block).getObjType().getBucketVolume() != 0)
                        e.setBurnTime(((IMaterialItem) block).getObjType().getBucketVolume() / 144 * ((IMaterialItem) block).getMaterial().getStandardBurnTime());
            } else if (item instanceof IMaterialItem)
                if (!((IMaterialItem) item).getObjType().hasTag(MaterialRegistry.HAS_NO_FUEL_VALUE) && ((IMaterialItem) item).getObjType().getBucketVolume() != 0)
                    e.setBurnTime(((IMaterialItem) item).getObjType().getBucketVolume() / 144 * ((IMaterialItem) item).getMaterial().getStandardBurnTime());
        }
    }
}