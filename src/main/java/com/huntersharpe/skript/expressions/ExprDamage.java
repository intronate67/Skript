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

import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.eclipse.jdt.annotation.Nullable;

import com.huntersharpe.skript.ScriptLoader;
import com.huntersharpe.skript.Skript;
import com.huntersharpe.skript.bukkitutil.HealthUtils;
import com.huntersharpe.skript.classes.Changer.ChangeMode;
import com.huntersharpe.skript.doc.Description;
import com.huntersharpe.skript.doc.Events;
import com.huntersharpe.skript.doc.Examples;
import com.huntersharpe.skript.doc.Name;
import com.huntersharpe.skript.doc.Since;
import com.huntersharpe.skript.lang.Expression;
import com.huntersharpe.skript.lang.ExpressionType;
import com.huntersharpe.skript.lang.SkriptParser.ParseResult;
import com.huntersharpe.skript.lang.util.SimpleExpression;
import com.huntersharpe.skript.log.ErrorQuality;

import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

/**
 * @author Peter Güttinger
 */
@Name("Damage")
@Description("How much damage is done in a damage event, possibly ignoring armour, criticals and/or enchantments. Can be changed (remember that in Skript '1' is one full heart, not half a heart).")
@Examples({"increase the damage by 2"})
@Since("1.3.5")
@Events("damage")
public class ExprDamage extends SimpleExpression<Double> {
	static {
		Skript.registerExpression(ExprDamage.class, Double.class, ExpressionType.SIMPLE, "[the] damage");
	}
	
	@SuppressWarnings("null")
	private Kleenean delay;
	
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		if (!ScriptLoader.isCurrentEvent(EntityDamageEvent.class)) {
			Skript.error("The expression 'damage' can only be used in damage events", ErrorQuality.SEMANTIC_ERROR);
			return false;
		}
		delay = isDelayed;
		return true;
	}
	
	@Override
	@Nullable
	protected Double[] get(final Event e) {
		if (!(e instanceof EntityDamageEvent))
			return new Double[0];
		return new Double[] {HealthUtils.getDamage((EntityDamageEvent) e)};
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (delay != Kleenean.FALSE) {
			Skript.error("Can't change the damage anymore after the event has already passed");
			return null;
		}
		if (mode == ChangeMode.REMOVE_ALL)
			return null;
		return CollectionUtils.array(Number.class);
	}
	
	@Override
	public void change(final Event e, final @Nullable Object[] delta, final ChangeMode mode) throws UnsupportedOperationException {
		if (!(e instanceof EntityDamageEvent))
			return;
		double d = delta == null ? 0 : ((Number) delta[0]).doubleValue();
		switch (mode) {
			case SET:
			case DELETE:
				HealthUtils.setDamage((EntityDamageEvent) e, d);
				break;
			case REMOVE:
				d = -d;
				//$FALL-THROUGH$
			case ADD:
				HealthUtils.setDamage((EntityDamageEvent) e, HealthUtils.getDamage((EntityDamageEvent) e) + d);
				break;
			case REMOVE_ALL:
			case RESET:
				assert false;
		}
	}
	
	@Override
	public boolean isSingle() {
		return true;
	}
	
	@Override
	public Class<? extends Double> getReturnType() {
		return Double.class;
	}
	
	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return "the damage";
	}
	
}
