package be.bluexin.rwbym.entity;

import be.bluexin.rwbym.Init.RWBYItems;
import be.bluexin.rwbym.ModLootTables;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;


public class EntityBlackStore extends EntityCreature implements INpc, IMerchant {
    World world = null;
    private MerchantRecipeList trades;
    private EntityPlayer buyingPlayer;

    public EntityBlackStore(World var3) {
        super(var3);
        world = var3;

        ItemStack is = new ItemStack(RWBYItems.whtefng, 1);
        this.setSize(1F, 1.5F);
        this.setItemStackToSlot(EntityEquipmentSlot.HEAD, is);
    }

    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(4, new EntityAIAttackMeleeWithRange(this, 1.0D, false, 0.5F));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(1, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2499999940395355D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(60.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
    }


    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        boolean flag = itemstack.getItem() == Items.NAME_TAG;
        if (flag) {
            itemstack.interactWithEntity(player, this, hand);
            return true;
        } else if(!flag){
            if (this.trades == null) {
                this.populateTradingList();
            }

            if (!this.world.isRemote && !this.trades.isEmpty()) {
                this.setCustomer(player);
                player.displayVillagerTradeGui(this);
            } else if (this.trades.isEmpty()) {
                return super.processInteract(player, hand);
            }

            return true;
        } else {
            return super.processInteract(player, hand);
        }
    }

    @Override

    public MerchantRecipeList getRecipes(EntityPlayer player) {

        if (trades == null || trades.isEmpty()) {

            populateTradingList();

        }

        return trades;

    }

    public boolean canBeLeashedTo(EntityPlayer player) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public void setRecipes(@Nullable MerchantRecipeList recipeList) {
    }

    protected void populateTradingList() {

        this.trades = new MerchantRecipeList();

        //buy//
        this.trades.add(new MerchantRecipe(new ItemStack(RWBYItems.lien500,2),new ItemStack(RWBYItems.rvnmask,1)));
        this.trades.add(new MerchantRecipe(new ItemStack(RWBYItems.lien500,1),new ItemStack(RWBYItems.whtefng,1)));
        this.trades.add(new MerchantRecipe(new ItemStack(RWBYItems.lien500,1),new ItemStack(RWBYItems.atlasknight,1)));
        this.trades.add(new MerchantRecipe(new ItemStack(RWBYItems.lien50,1),new ItemStack(RWBYItems.wallet,1)));
        this.trades.add(new MerchantRecipe(new ItemStack(RWBYItems.lien50,1),new ItemStack(RWBYItems.dustpouch,1)));
        this.trades.add(new MerchantRecipe(new ItemStack(RWBYItems.lien50,1),new ItemStack(RWBYItems.partspouch,1)));
        this.trades.add(new MerchantRecipe(new ItemStack(RWBYItems.lien500,10),new ItemStack(RWBYItems.coinr,1)));
        this.trades.add(new MerchantRecipe(new ItemStack(RWBYItems.lien500,10),new ItemStack(RWBYItems.coinw,1)));
        this.trades.add(new MerchantRecipe(new ItemStack(RWBYItems.lien500,10),new ItemStack(RWBYItems.coinb,1)));
        this.trades.add(new MerchantRecipe(new ItemStack(RWBYItems.lien500,10),new ItemStack(RWBYItems.coiny,1)));
        this.trades.add(new MerchantRecipe(new ItemStack(RWBYItems.lien500,10),new ItemStack(RWBYItems.coinjaune,1)));
        this.trades.add(new MerchantRecipe(new ItemStack(RWBYItems.lien500,10),new ItemStack(RWBYItems.coinnora,1)));
        this.trades.add(new MerchantRecipe(new ItemStack(RWBYItems.lien500,10),new ItemStack(RWBYItems.coin_ren,1)));
        this.trades.add(new MerchantRecipe(new ItemStack(RWBYItems.lien500,10),new ItemStack(RWBYItems.coin_ragora,1)));
        this.trades.add(new MerchantRecipe(new ItemStack(RWBYItems.lien500,10),new ItemStack(RWBYItems.scroll,1)));
        this.trades.add(new MerchantRecipe(new ItemStack(RWBYItems.lien500,10),new ItemStack(RWBYItems.scroll2,1)));
        this.trades.add(new MerchantRecipe(new ItemStack(RWBYItems.lien500,20),new ItemStack(RWBYItems.container,1)));
        //sell//


        // add as many trades as you want
    }

    public void verifySellingItem(ItemStack stack) {
        if (!this.world.isRemote && this.livingSoundTime > -this.getTalkInterval() + 20) {
            this.livingSoundTime = -this.getTalkInterval();
            this.playSound(stack.isEmpty() ? SoundEvents.ENTITY_VILLAGER_NO : SoundEvents.ENTITY_VILLAGER_YES, this.getSoundVolume(), this.getSoundPitch());
        }

    }



    public World getWorld() {
        return this.world;
    }

    public void useRecipe(MerchantRecipe recipe) {
        this.livingSoundTime = -this.getTalkInterval();
        this.playSound(SoundEvents.ENTITY_VILLAGER_YES, this.getSoundVolume(), this.getSoundPitch());
    }

    protected boolean canDespawn() {
        return false;
    }

    public BlockPos getPos() {
        return new BlockPos(this);
    }

    public void setCustomer(@Nullable EntityPlayer player) {
        this.buyingPlayer = player;
    }

    @Nullable
    public EntityPlayer getCustomer() {
        return this.buyingPlayer;
    }

    protected ResourceLocation getLootTable() {
        return null;
    }

    protected SoundEvent getAmbientSound() { return null; }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_VILLAGER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VILLAGER_DEATH;
    }
    @Override
    protected float getSoundVolume() {
        return 1.0F;
    }

}