/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 */
package ai.instance.battlefields;

import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.knownlist.Visitor;

import ai.GeneralNpcAI2;

/**
 * @author Rinzler
 */
@AIName("Pandaemonium_Tank_1") // 220824
public class Pandaemonium_Tank_1AI2 extends GeneralNpcAI2 {
	private boolean canThink = true;
	private AtomicBoolean isAggred = new AtomicBoolean(false);
	private AtomicBoolean startedEvent = new AtomicBoolean(false);

	@Override
	public boolean canThink() {
		return canThink;
	}

	@Override
	protected void handleCreatureMoved(Creature creature) {
		if (creature instanceof Player) {
			final Player player = (Player) creature;
			if (MathUtil.getDistance(getOwner(), player) <= 5) {
				if (startedEvent.compareAndSet(false, true)) {
					canThink = false;
					getSpawnTemplate().setWalkerId("302300001");
					WalkManager.startWalking(this);
					getOwner().setState(1);
					PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							if (!isAlreadyDead()) {
								despawn();
								giveItem();
								ItemService.addItem(player, 185000287, 1);
								spawn(834257, 1002.60626f, 1545.8975f, 242.58997f, (byte) 29); // Saranerk.
								spawn(220827, 1010.98220f, 1524.0913f, 242.58997f, (byte) 16); // Dreadgion_Turret_Carrier_C_Da.
							}
						}
					}, 306000);
				}
			}
		}
	}

	@Override
	protected void handleMoveArrived() {
		int point = getOwner().getMoveController().getCurrentPoint();
		super.handleMoveArrived();
		if (getNpcId() == 220824) { // Pandaemonium Tank A.
			if (point == 4) {
				spawn(220810, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
				spawn(220814, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
			} else if (point == 5) {
				spawn(220810, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
				spawn(220814, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
			} else if (point == 10) {
				spawn(220810, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
				spawn(220814, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
			} else if (point == 13) {
				spawn(220810, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
				spawn(220814, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
			}
		}
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isAggred.compareAndSet(false, true)) {
			switch (getNpcId()) {
			case 220824: // Pandaemonium Tank A.
				announcePandaemoniumTankA();
				break;
			}
		}
	}

	private void announcePandaemoniumTankA() {
		getPosition().getWorldMapInstance().doOnAllPlayers(
				new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							// The Ereshkigal Legion is attacking the chariot!
							PacketSendUtility.sendPacket(player,new SM_SYSTEM_MESSAGE(1403978));
						}
					}
				});
	}

	private void announcePandaemoniumTankADie() {
		getPosition().getWorldMapInstance().doOnAllPlayers(
				new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							// The transportation chariot was destroyed! You'll have to go back for another!
							PacketSendUtility.sendPacket(player,new SM_SYSTEM_MESSAGE(1403975));
						}
					}
				});
	}

	private void giveItem() {
		getPosition().getWorldMapInstance().doOnAllPlayers(
				new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							// Success! Use the energy core from the chariot to charge the defense turret!
							PacketSendUtility.sendPacket(player,new SM_SYSTEM_MESSAGE(1403976));
						}
					}
				});
	}

	@Override
	protected void handleDied() {
		announcePandaemoniumTankADie();
		spawn(834255, 1211.1102f, 1502.938f, 213.83618f, (byte) 6); // Koirunerk.
		super.handleDied();
	}

	private void despawn() {
		AI2Actions.deleteOwner(this);
	}
}
