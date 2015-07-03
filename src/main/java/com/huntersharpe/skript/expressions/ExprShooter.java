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
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.huntersharpe.skript.Skript;
import com.huntersharpe.skript.bukkitutil.ProjectileUtils;
import com.huntersharpe.skript.classes.Converter;
import com.huntersharpe.skript.classes.Changer.ChangeMode;
import com.huntersharpe.skript.doc.Description;
import com.huntersharpe.skript.doc.Examples;
import com.huntersharpe.skript.doc.Name;
import com.huntersharpe.skript.doc.Since;
import com.huntersharpe.skript.expressions.base.PropertyExpression;
import com.huntersharpe.skript.lang.Expression;
import com.huntersharpe.skript.lang.ExpressionType;
import com.huntersharpe.skript.lang.SkriptParser.ParseResult;

import ch.njol.util.Kleenean;

/**
 * @author Peter Güttinger
 */
@Name("Shooter")
@Description("The shooter of a projectile.")
@Examples({"shooter is a skeleton"})
@Since("1.3.7")
public class ExprShooter extends PropertyExpression<Projectile, LivingEntity> {
	static {
		Skript.registerExpression(ExprShooter.class, LivingEntity.class, ExpressionType.SIMPLE, "[the] shooter [of %projectile%]");
	}
	
	@SuppressWarnings({"unchecked", "null"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		setExpr((Expression<? extends Projectile>) exprs[0]);
		return true;
	}
	
	@Override
	protected LivingEntity[] get(final Event e, final Projectile[] source) {
		return get(source, new Converter<Projectile, LivingEntity>() {
			@Override
			@Nullable
			public LivingEntity convert(final Projectile p) {
				final Object o = ProjectileUtils.getShooter(p);
				if (o instanceof LivingEntity)
					return (LivingEntity) o;
				return null;
			}
		});
	}
	
	@Override
	@Nullable
	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return new Class[] {LivingEntity.class};
		return super.acceptChange(mode);
	}
	
	@Override
	public void change(final Event e, final @Nullable Object[] delta, final ChangeMode mode) {
		if (mode == ChangeMode.SET) {
			assert delta != null;
			for (final Projectile p : getExpr().getArray(e)) {
				assert p != null : getExpr();
				ProjectileUtils.setShooter(p, delta[0]);
			}
		} else {
			super.change(e, delta, mode);
		}
	}
	
	@Override
	public Class<LivingEntity> getReturnType() {
		return LivingEntity.class;
	}
	
	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return "the shooter" + (getExpr().isDefault() ? "" : " of " + getExpr().toString(e, debug));
	}
	
}
