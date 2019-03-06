package be.bluexin.rwbym.entity;

import javax.annotation.Nullable;

import be.bluexin.rwbym.ModLootTables;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityLancer extends EntityMob
{
    protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.<Byte>createKey(EntityLancer.class, DataSerializers.BYTE);
    private EntityLiving owner;
    @Nullable
    private BlockPos boundOrigin;


    public EntityLancer(World worldIn)
    {
        super(worldIn);
        this.isImmuneToFire = true;
        this.moveHelper = new EntityLancer.AIMoveControl(this);
        this.setSize(0.5F, 2F);
        this.experienceValue = 3;
    }

    /**
     * Tries to move the entity towards the specified location.
     */
    public void move(MoverType type, double x, double y, double z)
    {
        super.move(type, x, y, z);
        this.doBlockCollisions();
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.noClip = true;
        super.onUpdate();
        this.noClip = false;
        this.setNoGravity(true);

    }

    protected void initEntityAI()
    {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(4, new EntityLancer.AIChargeAttack());
        this.tasks.addTask(8, new EntityLancer.AIMoveRandom());
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[] {EntityLancer.class}));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVillager.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(14.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(VEX_FLAGS, Byte.valueOf((byte)0));
    }

    public static void registerFixesVex(DataFixer fixer)
    {
        EntityLiving.registerFixesMob(fixer, EntityLancer.class);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);

        if (compound.hasKey("BoundX"))
        {
            this.boundOrigin = new BlockPos(compound.getInteger("BoundX"), compound.getInteger("BoundY"), compound.getInteger("BoundZ"));
        }

    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);

        if (this.boundOrigin != null)
        {
            compound.setInteger("BoundX", this.boundOrigin.getX());
            compound.setInteger("BoundY", this.boundOrigin.getY());
            compound.setInteger("BoundZ", this.boundOrigin.getZ());
        }

    }



    @Nullable
    public BlockPos getBoundOrigin()
    {
        return this.boundOrigin;
    }

    public void setBoundOrigin(@Nullable BlockPos boundOriginIn)
    {
        this.boundOrigin = boundOriginIn;
    }

    private boolean getVexFlag(int mask)
    {
        int i = ((Byte)this.dataManager.get(VEX_FLAGS)).byteValue();
        return (i & mask) != 0;
    }

    private void setVexFlag(int mask, boolean value)
    {
        int i = ((Byte)this.dataManager.get(VEX_FLAGS)).byteValue();

        if (value)
        {
            i = i | mask;
        }
        else
        {
            i = i & ~mask;
        }

        this.dataManager.set(VEX_FLAGS, Byte.valueOf((byte)(i & 255)));
    }

    public boolean isCharging()
    {
        return this.getVexFlag(1);
    }

    public void setCharging(boolean charging)
    {
        this.setVexFlag(1, charging);
    }


    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_VEX_AMBIENT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_VEX_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_VEX_HURT;
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return ModLootTables.Ursa;
    }




    class AIChargeAttack extends EntityAIBase
    {
        public AIChargeAttack()
        {
            this.setMutexBits(1);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute()
        {
            if (EntityLancer.this.getAttackTarget() != null && !EntityLancer.this.getMoveHelper().isUpdating() && EntityLancer.this.rand.nextInt(7) == 0)
            {
                return EntityLancer.this.getDistanceSq(EntityLancer.this.getAttackTarget()) > 4.0D;
            }
            else
            {
                return false;
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting()
        {
            return EntityLancer.this.getMoveHelper().isUpdating() && EntityLancer.this.isCharging() && EntityLancer.this.getAttackTarget() != null && EntityLancer.this.getAttackTarget().isEntityAlive();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting()
        {
            EntityLivingBase entitylivingbase = EntityLancer.this.getAttackTarget();
            Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
            EntityLancer.this.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1.0D);
            EntityLancer.this.setCharging(true);
            EntityLancer.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask()
        {
            EntityLancer.this.setCharging(false);
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void updateTask()
        {
            EntityLivingBase entitylivingbase = EntityLancer.this.getAttackTarget();

            if (EntityLancer.this.getEntityBoundingBox().intersects(entitylivingbase.getEntityBoundingBox()))
            {
                EntityLancer.this.attackEntityAsMob(entitylivingbase);
                EntityLancer.this.setCharging(false);
            }
            else
            {
                double d0 = EntityLancer.this.getDistanceSq(entitylivingbase);

                if (d0 < 9.0D)
                {
                    Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
                    EntityLancer.this.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1.0D);
                }
            }
        }
    }


    class AIMoveControl extends EntityMoveHelper
    {
        public AIMoveControl(EntityLancer vex)
        {
            super(vex);
        }

        public void onUpdateMoveHelper()
        {
            if (this.action == EntityMoveHelper.Action.MOVE_TO)
            {
                double d0 = this.posX - EntityLancer.this.posX;
                double d1 = this.posY - EntityLancer.this.posY;
                double d2 = this.posZ - EntityLancer.this.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                d3 = (double)MathHelper.sqrt(d3);

                if (d3 < EntityLancer.this.getEntityBoundingBox().getAverageEdgeLength())
                {
                    this.action = EntityMoveHelper.Action.WAIT;
                    EntityLancer.this.motionX *= 0.5D;
                    EntityLancer.this.motionY *= 0.5D;
                    EntityLancer.this.motionZ *= 0.5D;
                }
                else
                {
                    EntityLancer.this.motionX += d0 / d3 * 0.05D * this.speed;
                    EntityLancer.this.motionY += d1 / d3 * 0.05D * this.speed;
                    EntityLancer.this.motionZ += d2 / d3 * 0.05D * this.speed;

                    if (EntityLancer.this.getAttackTarget() == null)
                    {
                        EntityLancer.this.rotationYaw = -((float)MathHelper.atan2(EntityLancer.this.motionX, EntityLancer.this.motionZ)) * (180F / (float)Math.PI);
                        EntityLancer.this.renderYawOffset = EntityLancer.this.rotationYaw;
                    }
                    else
                    {
                        double d4 = EntityLancer.this.getAttackTarget().posX - EntityLancer.this.posX;
                        double d5 = EntityLancer.this.getAttackTarget().posZ - EntityLancer.this.posZ;
                        EntityLancer.this.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * (180F / (float)Math.PI);
                        EntityLancer.this.renderYawOffset = EntityLancer.this.rotationYaw;
                    }
                }
            }
        }
    }

    class AIMoveRandom extends EntityAIBase
    {
        public AIMoveRandom()
        {
            this.setMutexBits(1);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute()
        {
            return !EntityLancer.this.getMoveHelper().isUpdating() && EntityLancer.this.rand.nextInt(7) == 0;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting()
        {
            return false;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void updateTask()
        {
            BlockPos blockpos = EntityLancer.this.getBoundOrigin();

            if (blockpos == null)
            {
                blockpos = new BlockPos(EntityLancer.this);
            }

            for (int i = 0; i < 3; ++i)
            {
                BlockPos blockpos1 = blockpos.add(EntityLancer.this.rand.nextInt(15) - 7, EntityLancer.this.rand.nextInt(11) - 5, EntityLancer.this.rand.nextInt(15) - 7);

                if (EntityLancer.this.world.isAirBlock(blockpos1))
                {
                    EntityLancer.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);

                    if (EntityLancer.this.getAttackTarget() == null)
                    {
                        EntityLancer.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }

                    break;
                }
            }
        }
    }
}
