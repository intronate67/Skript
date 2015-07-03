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

import java.lang.reflect.Array;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.huntersharpe.skript.Skript;
import com.huntersharpe.skript.doc.Description;
import com.huntersharpe.skript.doc.Examples;
import com.huntersharpe.skript.doc.Name;
import com.huntersharpe.skript.doc.Since;
import com.huntersharpe.skript.effects.EffShoot;
import com.huntersharpe.skript.effects.EffSpawn;
import com.huntersharpe.skript.entity.EntityData;
import com.huntersharpe.skript.lang.Expression;
import com.huntersharpe.skript.lang.ExpressionType;
import com.huntersharpe.skript.lang.Literal;
import com.huntersharpe.skript.lang.SkriptParser.ParseResult;
import com.huntersharpe.skript.lang.util.SimpleExpression;

import ch.njol.util.Kleenean;

/**
 * @author Peter Güttinger
 */
@Name("Last Spawned Entity")
@Description("Holds the entity that was spawned most recently with the <a href='../effects/#EffSpawn'>spawn effect</a>, or shot with the <a href='../effects/#EffShoot'>shoot effect</a>. " +
		"Please note that even though you can spawn multiple mobs simultaneously (e.g. with 'spawn 5 creepers'), only the last spawned mob is saved and can be used. " +
		"If you spawn an entity and shoot a projectile you can however access both.")
@Examples({"spawn a priest",
		"set {%spawned priest%.healer} to true",
		"shoot an arrow from the last spawned entity",
		"ignite the shot projectile"})
@Since("1.3 (spawned entity), 2.0 (shot entity)")
public class ExprLastSpawnedEntity extends SimpleExpression<Entity> {
	static {
		Skript.registerExpression(ExprLastSpawnedEntity.class, Entity.class, ExpressionType.SIMPLE, "[the] [last[ly]] (0¦spawned|1¦shot) %*entitydata%");
	}
	
	boolean spawned;
	@SuppressWarnings("null")
	private EntityData<?> type;
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		type = ((Literal<EntityData<?>>) exprs[0]).getSingle();
		spawned = parseResult.mark == 0;
		return true;
	}
	
	@Override
	@Nullable
	protected Entity[] get(final Event e) {
		final Entity en = spawned ? EffSpawn.lastSpawned : EffShoot.lastSpawned;
		if (en == null)
			return null;
		if (!type.isInstance(en))
			return null;
		final Entity[] one = (Entity[]) Array.newInstance(type.getType(), 1);
		one[0] = en;
		return one;
	}
	
	@Override
	public boolean isSingle() {
		return true;
	}
	
	@Override
	public Class<? extends Entity> getReturnType() {
		return type.getType();
	}
	
	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return "the last " + (spawned ? "spawned" : "shot") + " " + type;
	}
	
}
