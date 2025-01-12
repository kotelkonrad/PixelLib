package com.EmosewaPixel.pixellib.proxy;

import com.EmosewaPixel.pixellib.items.MaterialItem;
import com.EmosewaPixel.pixellib.materialSystem.MaterialRegistry;
import com.EmosewaPixel.pixellib.materialSystem.lists.MaterialBlocks;
import com.EmosewaPixel.pixellib.materialSystem.lists.MaterialItems;
import com.EmosewaPixel.pixellib.materialSystem.materials.DustMaterial;
import com.EmosewaPixel.pixellib.materialSystem.materials.IMaterialItem;
import com.EmosewaPixel.pixellib.resourceAddition.FakeResourcePackFinder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientProxy implements IModProxy {
    public static void setup() {
        Minecraft.getInstance().getResourcePackList().addPackFinder(new FakeResourcePackFinder());
    }

    @Override
    public void enque(InterModEnqueueEvent e) {
        color();
    }

    @Override
    public void process(InterModProcessEvent e) {
    }

    @SubscribeEvent
    public static void onModelBaking(ModelBakeEvent e) {
        MaterialItems.getAllItems().stream().filter(item -> item instanceof IMaterialItem).forEach(item -> {
            if (((IMaterialItem) item).getObjType().hasTag(MaterialRegistry.SINGLE_TEXTURE_TYPE))
                e.getModelRegistry().put(new ModelResourceLocation(item.getRegistryName(), "inventory"), e.getModelRegistry().get(new ModelResourceLocation("pixellib:" + ((IMaterialItem) item).getObjType().getName(), "inventory")));
            else
                e.getModelRegistry().put(new ModelResourceLocation(item.getRegistryName(), "inventory"), e.getModelRegistry().get(new ModelResourceLocation("pixellib:" + ((IMaterialItem) item).getMaterial().getTextureType().toString() + "_" + ((IMaterialItem) item).getObjType().getName(), "inventory")));
        });

        MaterialBlocks.getAllBlocks().stream().filter(block -> block instanceof IMaterialItem).forEach(block -> {
            if (((IMaterialItem) block).getObjType().hasTag(MaterialRegistry.SINGLE_TEXTURE_TYPE)) {
                e.getModelRegistry().put(new ModelResourceLocation(block.getRegistryName(), ""), e.getModelRegistry().get(new ModelResourceLocation("pixellib:" + ((IMaterialItem) block).getObjType().getName(), "")));
                e.getModelRegistry().put(new ModelResourceLocation(block.getRegistryName(), "inventory"), e.getModelRegistry().get(new ModelResourceLocation("pixellib:" + ((IMaterialItem) block).getObjType().getName(), "inventory")));
            } else {
                e.getModelRegistry().put(new ModelResourceLocation(block.getRegistryName(), ""), e.getModelRegistry().get(new ModelResourceLocation("pixellib:" + ((IMaterialItem) block).getMaterial().getTextureType().toString() + "_" + ((IMaterialItem) block).getObjType().getName(), "")));
                e.getModelRegistry().put(new ModelResourceLocation(block.getRegistryName(), "inventory"), e.getModelRegistry().get(new ModelResourceLocation("pixellib:" + ((IMaterialItem) block).getMaterial().getTextureType().toString() + "_" + ((IMaterialItem) block).getObjType().getName(), "inventory")));
            }
        });
    }

    private void color() {
        MaterialItems.getAllItems().stream().filter(item -> item instanceof MaterialItem).forEach(item ->
                Minecraft.getInstance().getItemColors().register((ItemStack stack, int index) -> {
                    Item sItem = stack.getItem();
                    if (sItem instanceof IMaterialItem)
                        if (((IMaterialItem) sItem).getObjType().hasTag(MaterialRegistry.USES_UNREFINED_COLOR) && ((IMaterialItem) sItem).getMaterial() instanceof DustMaterial)
                            return ((DustMaterial) ((IMaterialItem) sItem).getMaterial()).getUnrefinedColor();
                        else
                            return ((IMaterialItem) sItem).getMaterial().getColor();
                    return -1;
                }, item)
        );

        MaterialBlocks.getAllBlocks().stream().filter(block -> block instanceof IMaterialItem).forEach(block -> {
            Minecraft.getInstance().getBlockColors().register((BlockState state, @Nullable IEnviromentBlockReader reader, @Nullable BlockPos pos, int index) -> {
                Block sBlock = state.getBlock();
                if (sBlock instanceof IMaterialItem && index == 0)
                    if (((IMaterialItem) sBlock).getObjType().hasTag(MaterialRegistry.USES_UNREFINED_COLOR) && ((IMaterialItem) sBlock).getMaterial() instanceof DustMaterial)
                        return ((DustMaterial) ((IMaterialItem) sBlock).getMaterial()).getUnrefinedColor();
                    else
                        return ((IMaterialItem) sBlock).getMaterial().getColor();
                return -1;
            }, block);

            Minecraft.getInstance().getItemColors().register((ItemStack stack, int index) -> {
                Block sBlock = Block.getBlockFromItem(stack.getItem());
                if (sBlock instanceof IMaterialItem && index == 0)
                    if (((IMaterialItem) sBlock).getObjType().hasTag(MaterialRegistry.USES_UNREFINED_COLOR) && ((IMaterialItem) sBlock).getMaterial() instanceof DustMaterial)
                        return ((DustMaterial) ((IMaterialItem) sBlock).getMaterial()).getUnrefinedColor();
                    else
                        return ((IMaterialItem) sBlock).getMaterial().getColor();
                return -1;
            }, block.asItem());
        });
    }
}