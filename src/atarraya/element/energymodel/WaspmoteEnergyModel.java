/*
 * Libelium Waspmote Energy Consumption Model
 *
 * Created on September the 27th of 2010, 12:12 A.M.
 *
 * This file is part of Atarraya.
 *
 * Waspmote® is a registered trademark of Libelium Comunicaciones Distribuidas S.L.
 */

package atarraya.element.energymodel;

import atarraya.constants;
import atarraya.element.node;

/**
 *
 * @author Armando
 */
public class WaspmoteEnergyModel implements EnergyModel, constants{

    node the_node;

    // All values in mA
	// All data taken from the Waspmote Technical Guide and the Agriculture Board Technical Guide
	// http://www.libelium.com/documentation/waspmote/waspmote-technical_guide_eng.pdf
	// http://www.libelium.com/documentation/waspmote/agriculture-sensor-board_eng.pdf

    public static final double WASPMOTE_ON = 9.0;
    public static final double WASPMOTE_SLEEP = 0.062;
    public static final double WASPMOTE_DEEPSLEEP = 0.062;
    public static final double WASPMOTE_HIBERNATE = 0.0007;

    public static final double ZIGBEE_ON = 37.38;
    public static final double ZIGBEE_SLEEP = 0.23;
    public static final double ZIGBEE_OFF = 0.0;
    public static final double ZIGBEE_SENDING = 37.98;
    public static final double ZIGBEE_RECEIVING = 37.68;

	// There's no sensor idle mode because Waspmote turns the sensors power off when are not used.
    public static final double SENSOR_ON = 0.706; // Humidity 808H5H5 (0.7 mA) + Temperature MCP9700A (6 µA)
    public static final double SENSOR_OFF = 0.0; // Waspmote can disconnect sensor board power source.

    public static final double RADIO_TX_RX_TIME_BYTE= 2.083333333E-4; // 8 bits / 38400 bps
    public static final double RADIO_TX_AMP_BYTE= 20;
    public static final double RADIO_RX_AMP_BYTE= 15;




    public WaspmoteEnergyModel(node the_node_){
        the_node = the_node_;
    }

    public int getEnergyState(){
        return the_node.getEnergyState();
    }

    public void setEnergyState(int energy_state_){

        switch(energy_state_){

                    case STATE_ACTIVE:
                    case STATE_READY:
                    case STATE_MONITOR:
                        the_node.setRadioOn(true);
                        the_node.setSensorOn(true);
                        break;


                    case STATE_LOOK:
                        the_node.setRadioOn(false);
                        the_node.setSensorOn(true);
                        break;


                    case STATE_SLEEP:
                        the_node.setRadioOn(false);
                        the_node.setSensorOn(false);
                        break;


                    case STATE_JUST_RADIO:
                        the_node.setRadioOn(false);
                        the_node.setSensorOn(false);
                        break;

                }

    }

public double calcEnergy(int oper, double time){

        double ener = 0;

        switch(oper){
            case OPER_TX_REGULAR:
                //energy consumed by transmitting the message
                ener = (RADIO_TX_AMP_BYTE*RADIO_TX_RX_TIME_BYTE)*SIZE_SHORT_PACKETS;
                //energy consumed by being in Active mode so the message can be transmitted/received
                ener += (RADIO_TX_RX_TIME_BYTE*SIZE_SHORT_PACKETS)  * calcEnergyStateConsumption(STATE_FULL_ACTIVE);
                break;

            case OPER_TX_LONG:
                //energy consumed by transmitting the message
                ener = (RADIO_TX_AMP_BYTE*RADIO_TX_RX_TIME_BYTE)*SIZE_LONG_PACKETS;
                //energy consumed by being in Active mode so the message can be transmitted/received
                ener += (RADIO_TX_RX_TIME_BYTE*SIZE_LONG_PACKETS)  * calcEnergyStateConsumption(STATE_FULL_ACTIVE);
                break;

            case OPER_RX_REGULAR:
                //energy consumed by receving the message
                ener = (RADIO_RX_AMP_BYTE*RADIO_TX_RX_TIME_BYTE)*SIZE_SHORT_PACKETS;
                //energy consumed by being in Active mode so the message can be transmitted/received
                ener += (RADIO_TX_RX_TIME_BYTE*SIZE_SHORT_PACKETS)  * calcEnergyStateConsumption(STATE_ACTIVE);
                break;

            case OPER_RX_LONG:
                //energy consumed by receving the message
                ener = (RADIO_RX_AMP_BYTE*RADIO_TX_RX_TIME_BYTE)*SIZE_LONG_PACKETS;
                //energy consumed by being in Active mode so the message can be transmitted/received
                ener += (RADIO_TX_RX_TIME_BYTE*SIZE_LONG_PACKETS)  * calcEnergyStateConsumption(STATE_ACTIVE);
                break;

            case OPER_IDLE:
                //If the node is idle during a period determined by the parameter time, it will consume energy.
                ener = calcEnergyStateConsumption()*time;
                break;

            case OPER_READ_SENSOR:
                ener = calcEnergyStateConsumption(STATE_FULL_ACTIVE2)*time;
                break;

        }

        return ener/3600; // Converting amperes into amperes-hour.
    }

public double calcEnergyStateConsumption(){
    return calcEnergyStateConsumption(the_node.getEnergyState());
}

    public double calcEnergyStateConsumption(int energy_state_){

        switch(energy_state_){

                    case STATE_FULL_ACTIVE2:
                        return WASPMOTE_ON + ZIGBEE_RECEIVING + SENSOR_ON;

                    case STATE_FULL_ACTIVE:
                        return WASPMOTE_ON + ZIGBEE_SENDING + SENSOR_OFF;

                    case STATE_ACTIVE:
                    case STATE_READY:
                        return WASPMOTE_ON + ZIGBEE_ON + SENSOR_OFF;

                    case STATE_MONITOR:
                        return WASPMOTE_SLEEP + ZIGBEE_ON + SENSOR_OFF;

                    case STATE_LOOK:
                    case STATE_SLEEP:
                        return WASPMOTE_SLEEP + ZIGBEE_SLEEP + SENSOR_OFF;

                    case STATE_JUST_RADIO:
                        return WASPMOTE_HIBERNATE + ZIGBEE_ON + SENSOR_OFF;

                    default:
                        return 0;
                }
    }
}
