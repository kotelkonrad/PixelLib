package com.EmosewaPixel.pixellib.materialSystem.materials;

import com.EmosewaPixel.pixellib.items.tools.ArmorMaterial;
import com.EmosewaPixel.pixellib.items.tools.ItemTier;
import com.EmosewaPixel.pixellib.materialSystem.MaterialRegistry;
import com.EmosewaPixel.pixellib.materialSystem.element.Element;
import com.EmosewaPixel.pixellib.materialSystem.lists.MaterialItems;
import com.EmosewaPixel.pixellib.materialSystem.types.ObjectType;
import com.EmosewaPixel.pixellib.materialSystem.types.TextureType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;

import java.util.function.Supplier;

//Dust Materials are the basic solid materials for which at least a dust and block is generated
public class DustMaterial extends Material {
    private HarvestTier harvestTier = null;
    private int processingMultiplier = 1;
    private DustMaterial refinedMaterial = null;
    private int unrefinedColor;

    public DustMaterial(String name, TextureType textureType, int color, int tier) {
        super(name, textureType, color, tier);
        setHarvestProperties(1.5f * (getTier() + 1), 1.5f * (getTier() + 1));
        unrefinedColor = color;
    }

    public DustMaterial addTags(String... tags) {
        super.addTags(tags);
        return this;
    }

    public DustMaterial setItemTier(IItemTier tier) {
        super.setItemTier(tier);
        return this;
    }

    public DustMaterial setItemTierStats(int harvestLevelIn, int maxUsesIn, float efficiencyIn, float attackDamageIn, int enchantabilityIn, Supplier<Ingredient> repairMaterialIn) {
        return setItemTier(new ItemTier(harvestLevelIn, maxUsesIn, efficiencyIn, attackDamageIn, enchantabilityIn, repairMaterialIn));
    }

    public DustMaterial setItemTierStats(int maxUsesIn, float efficiencyIn, float attackDamageIn, int enchantabilityIn, Supplier<Ingredient> repairMaterialIn) {
        return setItemTierStats(getTier() + 1, maxUsesIn, efficiencyIn, attackDamageIn, enchantabilityIn, repairMaterialIn);
    }

    public DustMaterial setArmorMaterial(IArmorMaterial mat) {
        super.setArmorMaterial(mat);
        return this;
    }

    public DustMaterial setArmorStats(int durability, int damageReduction, int enchantability, SoundEvent soundEvent, Supplier<Ingredient> repairMaterial, float toughness) {
        return setArmorMaterial(new ArmorMaterial(durability, damageReduction, enchantability, soundEvent, repairMaterial, getName(), toughness));
    }

    public DustMaterial setHarvestTier(HarvestTier harvestTier) {
        this.harvestTier = harvestTier;
        return this;
    }

    public DustMaterial setHarvestProperties(float hardness, float resistance, int level) {
        return setHarvestTier(new HarvestTier(hardness, resistance, level));
    }

    public DustMaterial setHarvestProperties(float hardness, float resistance) {
        return setHarvestProperties(hardness, resistance, getTier());
    }

    public DustMaterial blacklistTypes(ObjectType... types) {
        super.blacklistTypes(types);
        return this;
    }

    public DustMaterial setComposition(MaterialStack... stacks) {
        super.setComposition(stacks);
        return this;
    }

    public DustMaterial setElement(Element element) {
        super.setElement(element);
        return this;
    }

    public DustMaterial setSecondName(String name) {
        super.setSecondName(name);
        return this;
    }

    public DustMaterial setStandardBurnTime(int time) {
        super.setStandardBurnTime(time);
        return this;
    }

    public DustMaterial setProcessingMultiplier(int multiplier) {
        this.processingMultiplier = multiplier;
        return this;
    }

    public DustMaterial setRefinedMaterial(DustMaterial material) {
        this.refinedMaterial = material;
        return this;
    }

    public DustMaterial setUnrefinedColor(int color) {
        this.unrefinedColor = color;
        return this;
    }

    public HarvestTier getHarvestTier() {
        return harvestTier;
    }

    public int getProcessingMultiplier() {
        return processingMultiplier;
    }

    public DustMaterial getRefinedMaterial() {
        return refinedMaterial;
    }

    public int getUnrefinedColor() {
        return unrefinedColor;
    }

    public Item getDefaultItem() {
        return MaterialItems.getItem(this, MaterialRegistry.DUST);
    }
}