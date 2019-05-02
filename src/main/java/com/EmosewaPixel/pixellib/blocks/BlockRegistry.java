package com.EmosewaPixel.pixellib.blocks;

import com.EmosewaPixel.pixellib.PixelLib;
import com.EmosewaPixel.pixellib.materialSystem.lists.MaterialBlocks;
import com.EmosewaPixel.pixellib.materialSystem.lists.Materials;
import com.EmosewaPixel.pixellib.materialSystem.lists.TextureTypes;
import com.EmosewaPixel.pixellib.materialSystem.lists.ObjTypes;
import com.EmosewaPixel.pixellib.materialSystem.materials.IMaterialItem;
import com.EmosewaPixel.pixellib.materialSystem.materials.IngotMaterial;
import com.EmosewaPixel.pixellib.materialSystem.types.BlockType;
import com.EmosewaPixel.pixellib.materialSystem.types.ObjectType;
import com.EmosewaPixel.pixellib.materialSystem.types.TextureType;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.RegistryEvent;

import java.util.ArrayList;

public class BlockRegistry {
    private static ArrayList<Block> templates = new ArrayList<>();

    public static Block getGroupIcon() {
        return templates.get(0);
    }

    public static void registry(RegistryEvent.Register<Block> e) {
        for (com.EmosewaPixel.pixellib.materialSystem.materials.Material mat : Materials.getAll())
            for (ObjectType type : ObjTypes.getAll())
                if (type instanceof BlockType && type.isMaterialCompatible(mat) && !MaterialBlocks.contains(mat, type))
                    register(new MaterialBlock((IngotMaterial) mat, (BlockType) type), e);

        for (ObjectType objT : ObjTypes.getAll())
            if (objT instanceof BlockType)
                for (TextureType textureT : TextureTypes.getAll())
                    templates.add(register(new ModBlock(Block.Properties.create(Material.IRON), textureT.toString() + "_" + objT.getName(), 0), e));
    }

    public static void itemRegistry(RegistryEvent.Register<Item> e) {
        for (Block block : MaterialBlocks.getAllBlocks())
            if (block instanceof IMaterialItem)
                registerItemBlock(block, e);

        for (Block template : templates)
            registerItemBlock(template, e, false);
    }

    private static Block register(Block block, RegistryEvent.Register<Block> e) {
        e.getRegistry().register(block);
        return block;
    }

    private static void registerItemBlock(Block block, RegistryEvent.Register<Item> e) {
        registerItemBlock(block, e, true);
    }

    private static void registerItemBlock(Block block, RegistryEvent.Register<Item> e, boolean withGroup) {
        e.getRegistry().register(new ItemBlock(block, withGroup ? new Item.Properties().group(PixelLib.main) : new Item.Properties()) {
            @Override
            public ITextComponent getDisplayName(ItemStack stack) {
                return getBlock().getNameTextComponent();
            }
        }.setRegistryName(block.getRegistryName()));
    }
}