/*
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 * Copyright 2011-2014 Peter Güttinger
 * 
 */

package com.huntersharpe.skript.expressions;

import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.huntersharpe.skript.Skript;
import com.huntersharpe.skript.SkriptConfig;
import com.huntersharpe.skript.classes.Converter;
import com.huntersharpe.skript.doc.Description;
import com.huntersharpe.skript.doc.Examples;
import com.huntersharpe.skript.doc.Name;
import com.huntersharpe.skript.doc.Since;
import com.huntersharpe.skript.expressions.base.PropertyExpression;
import com.huntersharpe.skript.lang.Expression;
import com.huntersharpe.skript.lang.ExpressionType;
import com.huntersharpe.skript.lang.SkriptParser.ParseResult;
import com.huntersharpe.skript.registrations.Classes;

import ch.njol.util.Kleenean;

/**
 * @author Peter Güttinger
 */
@Name("Targeted Block")
@Description("The block at the crosshair. This regards all blocks that are not air as fully opaque, e.g. torches will be like a solid stone block for this expression.")
@Examples({"# A command to set the block a player looks at to a specific type:",
		"command /setblock <material>:",
		"    trigger:",
		"        set targeted block to argument"})
@Since("1.0")
public class ExprTargetedBlock extends PropertyExpression<Player, Block> {
	static {
		Skript.registerExpression(ExprTargetedBlock.class, Block.class, ExpressionType.COMBINED,
				"[the] target[ed] block[s] [of %players%]", "%players%'[s] target[ed] block[s]",
				"[the] actual[ly] target[ed] block[s] [of %players%]", "%players%'[s] actual[ly] target[ed] block[s]");
	}
	
	private boolean actualTargetedBlock;
	
	@Nullable
	private static Event last = null;
	private final static WeakHashMap<Player, Block> targetedBlocks = new WeakHashMap<Player, Block>();
	private static long blocksValidForTick = 0;
	
	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parser) {
		setExpr((Expression<Player>) exprs[0]);
		actualTargetedBlock = matchedPattern >= 2;
		return true;
	}
	
	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		if (e == null)
			return "the targeted block" + (getExpr().isSingle() ? "" : "s") + " of " + getExpr().toString(e, debug);
		return Classes.getDebugMessage(getAll(e));
	}
	
	@Nullable
	Block getTargetedBlock(final @Nullable Player p, final Event e) {
		if (p == null)
			return null;
		final long time = Bukkit.getWorlds().get(0).getFullTime();
		if (last != e || time != blocksValidForTick) {
			targetedBlocks.clear();
			blocksValidForTick = time;
			last = e;
		}
		if (!actualTargetedBlock && getTime() <= 0 && targetedBlocks.containsKey(p))
			return targetedBlocks.get(p);
//		if (e instanceof PlayerInteractEvent && p == ((PlayerInteractEvent) e).getPlayer() && (((PlayerInteractEvent) e).getAction() == Action.LEFT_CLICK_BLOCK || ((PlayerInteractEvent) e).getAction() == Action.RIGHT_CLICK_BLOCK)) {
//			targetedBlocks.put(((PlayerInteractEvent) e).getPlayer(), ((PlayerInteractEvent) e).getClickedBlock());
//			return ((PlayerInteractEvent) e).getClickedBlock();
//		}
		try {
			@SuppressWarnings("deprecation")
			Block b = p.getTargetBlock(null, SkriptConfig.maxTargetBlockDistance.value());
			if (b.getType() == Material.AIR)
				b = null;
			targetedBlocks.put(p, b);
			return b;
		} catch (final IllegalStateException ex) {// Bukkit my throw this (for no reason?)
			return null;
		}
	}
	
	@Override
	protected Block[] get(final Event e, final Player[] source) {
		return get(source, new Converter<Player, Block>() {
			@Override
			@Nullable
			public Block convert(final Player p) {
				return getTargetedBlock(p, e);
			}
		});
	}
	
	@Override
	public Class<Block> getReturnType() {
		return Block.class;
	}
	
	@Override
	public boolean isDefault() {
		return false;
	}
	
	@Override
	public boolean setTime(final int time) {
		super.setTime(time);
		return true;
	}
	
}
