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

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import com.huntersharpe.skript.Skript;
import com.huntersharpe.skript.classes.Arithmetic;
import com.huntersharpe.skript.classes.ClassInfo;
import com.huntersharpe.skript.conditions.CondCompare;
import com.huntersharpe.skript.doc.Description;
import com.huntersharpe.skript.doc.Examples;
import com.huntersharpe.skript.doc.Name;
import com.huntersharpe.skript.doc.Since;
import com.huntersharpe.skript.lang.Expression;
import com.huntersharpe.skript.lang.ExpressionType;
import com.huntersharpe.skript.lang.Literal;
import com.huntersharpe.skript.lang.Variable;
import com.huntersharpe.skript.lang.SkriptParser.ParseResult;
import com.huntersharpe.skript.lang.util.SimpleExpression;
import com.huntersharpe.skript.log.ErrorQuality;
import com.huntersharpe.skript.registrations.Classes;
import com.huntersharpe.skript.util.Utils;

import ch.njol.util.Kleenean;

/**
 * @author Peter Güttinger
 */
@Name("Difference")
@Description("The difference between two values, e.g. <a href='../classes/#number'>numbers</a>, <a href='../classes/#date'>dates</a> or <a href='../classes/#time'>times</a>.")
@Examples({"difference between {command.%player%.lastuse} and now is smaller than a minute:",
		"  message \"You have to wait a minute before using this command again!\"",
		"  stop"})
@Since("1.4")
public class ExprDifference extends SimpleExpression<Object> {
	
	static {
		Skript.registerExpression(ExprDifference.class, Object.class, ExpressionType.COMBINED, "difference (between|of) %object% and %object%");
	}
	
	@SuppressWarnings("null")
	private Expression<?> first, second;
	
	@SuppressWarnings({"null", "rawtypes"})
	private Arithmetic math;
	@SuppressWarnings("null")
	private Class<?> relativeType;
	
	@SuppressWarnings({"unchecked", "null", "unused"})
	@Override
	public boolean init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		first = exprs[0];
		second = exprs[1];
		final ClassInfo<?> ci;
		if (first instanceof Variable && second instanceof Variable) {
			ci = Classes.getExactClassInfo(Double.class);
			first = first.getConvertedExpression(Double.class);
			second = second.getConvertedExpression(Double.class);
		} else if (first instanceof Literal<?> && second instanceof Literal<?>) {
			first = first.getConvertedExpression(Object.class);
			second = second.getConvertedExpression(Object.class);
			if (first == null || second == null)
				return false;
			ci = Classes.getSuperClassInfo(Utils.getSuperType(first.getReturnType(), second.getReturnType()));
		} else {
			if (first instanceof Literal<?>) {
				first = first.getConvertedExpression(second.getReturnType());
				if (first == null)
					return false;
			} else if (second instanceof Literal<?>) {
				second = second.getConvertedExpression(first.getReturnType());
				if (second == null)
					return false;
			}
			if (first instanceof Variable) {
				first = first.getConvertedExpression(second.getReturnType());
			} else if (second instanceof Variable) {
				second = second.getConvertedExpression(first.getReturnType());
			}
			assert first != null && second != null;
			ci = Classes.getSuperClassInfo(Utils.getSuperType(first.getReturnType(), second.getReturnType()));
		}
		assert ci != null;
		if (ci.getMath() == null) {
			Skript.error("Can't get the difference of " + CondCompare.f(first) + " and " + CondCompare.f(second), ErrorQuality.SEMANTIC_ERROR);
			return false;
		}
		math = ci.getMath();
		relativeType = ci.getMathRelativeType();
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	protected Object[] get(final Event e) {
		final Object f = first.getSingle(e), s = second.getSingle(e);
		if (f == null || s == null)
			return null;
		final Object[] one = (Object[]) Array.newInstance(relativeType, 1);
		one[0] = math.difference(f, s);
		return one;
	}
	
	@Override
	public Class<? extends Object> getReturnType() {
		return relativeType;
	}
	
	@Override
	public String toString(final @Nullable Event e, final boolean debug) {
		return "difference between " + first.toString(e, debug) + " and " + second.toString(e, debug);
	}
	
	@Override
	public boolean isSingle() {
		return true;
	}
	
}