package ljrr;

import java.awt.Color;
import robocode.AdvancedRobot;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;

// VERSION: 
/**
 * @author Jan Limpens Gutiérrez
 * @author Jonathan Rosas
 * @author Emiliano Gomez
 *
 */

public class Jarvis extends AdvancedRobot {
// Hacemos una enumaracion conteniendo los nombres de todos los estados del
//robot
	private Estado estado;// La variable estado va a indicar el estado actual del robot
	private ScannedRobotEvent enemigo; // guarda datos del enemigo
	private int x = 1;

	private enum Estado {
		MOVIENDO, AJUSTANDO, DISPARANDO, HUYENDO;
	}

// el metodo principal del robot, donde toma desiciones de a cuerdo a su estado
	public void run() {
		inicializar();
		estado = Estado.MOVIENDO;
		while (true) {
			// colores al azar
			int R = (int) (Math.random() * 500);
			int G = (int) (Math.random() * 256);
			int B = (int) (Math.random() * 700);
			setColors(Color.getHSBColor(R, G, B), Color.getHSBColor(G, B, R), Color.getHSBColor(B, R, G),
					Color.getHSBColor(R, G, B), Color.getHSBColor(R, G, B));
			switch (estado) {
			case MOVIENDO: // controla el movimiento constante del robot

				if (vaAchocar() == true) {
					setBack(80);
					setTurnGunRight(20);
					execute();
				} else {
					setAhead(400);
					setTurnLeft(20);
					setAhead(400);
					setTurnRight(20);
					setTurnGunRight(-20);
					execute();
				}
				break;
			case AJUSTANDO: // realiza ajustes para apuntar al enemigo
				setTurnGunRight(getHeading() - getGunHeading() + enemigo.getBearing());
				execute();
				estado = Estado.DISPARANDO;
				break;
			case DISPARANDO: // dispara
				if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
					setFire(Math.min(400 / enemigo.getDistance(), 3));
				}
				estado = Estado.MOVIENDO;
				break;
			case HUYENDO: // realiza movimientos para esquivar
				setBack(50);
				setTurnLeft(40);
				execute();
				estado = Estado.MOVIENDO;
				break;
			default: // caso default para no caer en ciclos infinitos
				doNothing();
				break;
			}
		}
	}

	// Dar valores de configuracion de independencia de partes
	private void inicializar() {
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(false);
	}

// Checa si vio a un robot, para apuntarle
	public void onScannedRobot(ScannedRobotEvent event) {
		enemigo = event;
		estado = Estado.AJUSTANDO;
	}

// Checa si te dio una bala para huir
	public void onHitByBullet(HitByBulletEvent event) {
		if (getOthers() == 1) {
			setTurnRight((event.getBearing() + 120 )- 30);
			setBack(100);
			setTurnLeft(20);
		}
		execute();
		estado = Estado.HUYENDO;
	}

// checa si esta chocando con robots, para esquivarlos
	public void onHitRobot(HitRobotEvent event) {
		estado = Estado.HUYENDO;
	}

//funcion que nos avisa si va a chocar contra una pared (true) o no (false)
	private boolean vaAchocar() {
		double fieldHeight = getBattleFieldHeight();
		double fieldWidth = getBattleFieldWidth();
		boolean choca = false;
		if (getX() < 80 || getX() > 720) {
			choca = true;
		}
		if (getY() < 80 || getY() > 520) {
			choca = true;
		}
		return choca;
	}

	@Override
	public void onHitWall(HitWallEvent event) {
		back(10);
		setTurnLeft(30);
	}
}

