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

import org.bukkit.entity.LivingEntity;

import com.huntersharpe.skript.doc.Description;
import com.huntersharpe.skript.doc.Examples;
import com.huntersharpe.skript.doc.Name;
import com.huntersharpe.skript.doc.Since;
import com.huntersharpe.skript.expressions.base.SimplePropertyExpression;
import com.huntersharpe.skript.util.Timespan;

/**
 * @author Peter Güttinger
 */
@Name("Remaining Air")
@Description("How much time a player has left underwater before starting to drown.")
@Examples({"player's remaining air is less than 3 seconds:",
		"	send \"hurry, get to the surface!\" to the player"})
@Since("")
public class ExprRemainingAir extends SimplePropertyExpression<LivingEntity, Timespan> {
	static {
		register(ExprRemainingAir.class, Timespan.class, "remaining air", "livingentities");
	}
	
	@Override
	public Class<Timespan> getReturnType() {
		return Timespan.class;
	}
	
	@Override
	protected String getPropertyName() {
		return "remaining air";
	}
	
	@Override
	public Timespan convert(final LivingEntity e) {
		return Timespan.fromTicks(e.getRemainingAir());
	}
	
}
