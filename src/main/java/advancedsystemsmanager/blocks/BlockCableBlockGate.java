package advancedsystemsmanager.blocks;

import advancedsystemsmanager.api.tileentities.IClusterTile;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.reference.Reference;
import advancedsystemsmanager.tileentities.TileEntityBlockGate;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockCableBlockGate extends BlockClusterElementBase<TileEntityBlockGate>
{
    public BlockCableBlockGate()
    {
        super(Names.CABLE_BLOCK_GATE, 3);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntityBlockGate gate = getTileEntity(world, x, y, z);

        if (gate != null)
        {
            int meta = gate.getMetadata() % ForgeDirection.VALID_DIRECTIONS.length;
            int direction = gate.getPlaceDirection();

            if (side == meta && side == direction)
            {
                return icons[0];
            } else if (side == meta)
            {
                return icons[1];
            } else if (side == direction)
            {
                return icons[2];
            }
        }

        return blockIcon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta)
    {
        return side == 3 ? icons[0] : blockIcon;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (player.isSneaking())
        {
            side = ForgeDirection.VALID_DIRECTIONS[side].getOpposite().ordinal();
        }

        TileEntityBlockGate breaker = getTileEntity(world, x, y, z);
        if (breaker != null && !breaker.isBlocked())
        {
            breaker.setPlaceDirection(side);
            return true;
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item)
    {
        int meta = BlockPistonBase.determineOrientation(world, x, y, z, entity);

        TileEntityBlockGate breaker = getTileEntity(world, x, y, z);
        if (breaker != null)
        {
            breaker.setMetaData(meta);
            breaker.setPlaceDirection(breaker.getMetadata());
        }
    }

    @Override
    public void registerBlockIcons(IIconRegister register)
    {
        super.registerBlockIcons(register);
        blockIcon = register.registerIcon(Reference.RESOURCE_LOCATION + ":cable_idle");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityBlockGate();
    }

    @Override
    public boolean isInstance(IClusterTile tile)
    {
        return tile instanceof TileEntityBlockGate;
    }
}
