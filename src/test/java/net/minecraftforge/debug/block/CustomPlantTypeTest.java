/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ObjectHolder;

@Mod(CustomPlantTypeTest.MODID)
@Mod.EventBusSubscriber(bus = Bus.MOD)
public class CustomPlantTypeTest
{
    static final String MODID = "custom_plant_type_test";
    private static final String CUSTOM_SOIL_BLOCK = "test_custom_block";
    private static final String CUSTOM_PLANT_BLOCK = "test_custom_plant";

    @ObjectHolder(CUSTOM_SOIL_BLOCK)
    public static Block CUSTOM_SOIL;
    @ObjectHolder(CUSTOM_PLANT_BLOCK)
    public static Block CUSTOM_PLANT;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(new CustomBlock(), new CustomPlantBlock());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(new BlockItem(CUSTOM_SOIL, (new Item.Properties())).setRegistryName(MODID, CUSTOM_SOIL_BLOCK),
                new BlockItem(CUSTOM_PLANT, (new Item.Properties())).setRegistryName(MODID, CUSTOM_PLANT_BLOCK));
    }

    public static class CustomBlock extends Block
    {
        public CustomBlock()
        {
            super(Block.Properties.of(Material.STONE));
            this.setRegistryName(MODID, CUSTOM_SOIL_BLOCK);
        }

        @Override
        public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable)
        {
            PlantType type = plantable.getPlantType(world, pos.relative(facing));
            if (type != null && type == CustomPlantBlock.pt)
            {
                return true;
            }
            return super.canSustainPlant(state, world, pos, facing, plantable);
        }
    }

    public static class CustomPlantBlock extends FlowerBlock implements IPlantable
    {
        public static PlantType pt = PlantType.get("custom_plant_type");

        public CustomPlantBlock()
        {
            super(Effects.WEAKNESS, 9, Block.Properties.of(Material.PLANT).noCollission().sound(SoundType.GRASS));
            this.setRegistryName(MODID, CUSTOM_PLANT_BLOCK);
        }

        @Override
        public PlantType getPlantType(IBlockReader world, BlockPos pos)
        {
            return pt;
        }

        @Override
        public BlockState getPlant(IBlockReader world, BlockPos pos)
        {
            return defaultBlockState();
        }

        @Override
        public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos)
        {
            BlockState soil = world.getBlockState(pos.below());
            return soil.canSustainPlant(world, pos, Direction.UP, this);
        }

        @Override
        public boolean mayPlaceOn(BlockState state, IBlockReader worldIn, BlockPos pos)
        {
            Block block = state.getBlock();
            return block == CUSTOM_SOIL;
        }
    }
}
