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

import org.bukkit.Location;

import com.huntersharpe.skript.doc.Description;
import com.huntersharpe.skript.doc.Examples;
import com.huntersharpe.skript.doc.Name;
import com.huntersharpe.skript.doc.Since;
import com.huntersharpe.skript.expressions.base.SimplePropertyExpression;

/**
 * @author Peter Güttinger
 */
@Name("Altitude")
@Description("Effectively an alias of 'y-<a href='#ExprCoordinate'>coordinate</a> of …', it represents the height of some object above bedrock.")
@Examples({"on damage:",
		"	altitude of the attacker is higher that the altitude of the victim",
		"	set damage to damage * 1.2"})
@Since("1.4.3")
public class ExprAltitude extends SimplePropertyExpression<Location, Double> {
	static {
		register(ExprAltitude.class, Double.class, "altitude[s]", "locations");
	}
	
	@SuppressWarnings("null")
	@Override
	public Double convert(final Location l) {
		return l.getY();
	}
	
	@Override
	protected String getPropertyName() {
		return "altitude";
	}
	
	@Override
	public Class<Double> getReturnType() {
		return Double.class;
	}
	
}