/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package atarraya.element.energymodel;

import atarraya.constants;
import atarraya.element.node;

/**
 *
 * @author Armando
 */
public class MICA2EnergyModel implements EnergyModel, constants {

    node the_node;

    // All values in mA
    // Info taken from the MPR-MIB Users Manual. Revision B, June 2006
    // Based on MPR400 (MICA2) system components current consumption

    public static final double PROCESSOR_CURRENT_FULLOP= 8;
    public static final double PROCESSOR_CURRENT_SLEEP= 0.008;

    public static final double RADIO_CURRENT_RX= 8;
    public static final double RADIO_CURRENT_TX= 12;
    public static final double RADIO_CURRENT_SLEEP= 0.002;

    public static final double LOGGER_MEMORY_WRITE= 15;
    public static final double LOGGER_MEMORY_READ= 4;
    public static final double LOGGER_MEMORY_SLEEP= 0.002;

    public static final double SENSOR_CURRENT_FULLOP= 5;
    public static final double SENSOR_CURRENT_SLEEP= 0.005;

    public static final double RADIO_TX_RX_TIME_BYTE=  4.166666667E-4; // 8 bits / 19200 bps
    public static final double RADIO_TX_AMP_BYTE= 20;
    public static final double RADIO_RX_AMP_BYTE= 15;




    public MICA2EnergyModel(node the_node_){
        the_node = the_node_;
    }

    public int getEnergyState(){
        return the_node.getEnergyState();
    }

    public void setEnergyState(int energy_state_){
        //energy_state = energy_state_;

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
                        //the_node.setRadioOn(true);
                        the_node.setRadioOn(false);
                        the_node.setSensorOn(false);
                        break;

                }

    }

public double calcEnergy(int oper, double time){

        double ener = 0;
        double  comm_radius = the_node.getActiveRadius();

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

                //If the node is idle during a period determined by the parameter time, it will consume energy.
                //The parameters for energy consumption are taken from the specifications of the MICA2 motes
            case OPER_IDLE:
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
                        return PROCESSOR_CURRENT_FULLOP+RADIO_CURRENT_RX+SENSOR_CURRENT_FULLOP;

                    case STATE_FULL_ACTIVE:
                        return PROCESSOR_CURRENT_FULLOP+RADIO_CURRENT_TX+SENSOR_CURRENT_SLEEP;

                    case STATE_ACTIVE:
                    case STATE_READY:
                        return PROCESSOR_CURRENT_FULLOP+RADIO_CURRENT_RX+SENSOR_CURRENT_SLEEP;

                    case STATE_MONITOR:
                    case STATE_JUST_RADIO:
                        return PROCESSOR_CURRENT_SLEEP+RADIO_CURRENT_RX+SENSOR_CURRENT_SLEEP;


                    case STATE_LOOK:
                    case STATE_SLEEP:
                        return PROCESSOR_CURRENT_SLEEP+RADIO_CURRENT_SLEEP+SENSOR_CURRENT_SLEEP;

                    default:
                        return 0;

                }

    }

}
