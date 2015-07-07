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
 * Copyright 2011-2013 Peter Güttinger
 * 
 */

package com.huntersharpe.skript;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerJoinEvent;

import com.huntersharpe.skript.Updater.UpdateState;
import com.huntersharpe.skript.util.Task;

/**
 * @author Peter Güttinger & Hunter Sharpe
 *
 */
public class UpdateListener{
	
	@Subscribe
	public void onJoin(final PlayerJoinEvent e){
		if(e.getUser().hasPermission("skript.admin")){
			new Task(Skript.getInstance(), 0){
				@Override
				public void run(){
					Updater.stateLock.readLock().lock();
					try{
						final Player p = e.getUser();
						assert p != null;
						if((Updater.state == UpdateState.CHECKED_FOR_UPDATE || Updater.state == UpdateState.DOWNLOAD_ERROR) && Updater.latest.get() != null) {
							Skript.getInstance();
							Skript.info(p, "" + Updater.m_update_available);
						}
					} finally{
						Updater.stateLock.readLock().lock();
					}
				}
			};
		}
	}
	
}
