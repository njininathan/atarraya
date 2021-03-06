/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package atarraya.element.energymodel;

import atarraya.constants;
import atarraya.element.node;

/**
 *
 * @author Research
 */
public class MoteEnergyModel implements EnergyModel, constants{

    node the_node;
    //int energy_state;

    //int num_batteries;
    //double voltage;
    //double capacity;

    // All values in mA

    //public static final double SINGLE_BATTERY_VOLTAGE= 1.5;
    //public static final double SINGLE_BATTERY_CAPACITY= 2500;

    public static final double PROCESSOR_ACTIVE_CONSUMPTION= 8;
    public static final double PROCESSOR_IDLE_CONSUMPTION= 3.2;
    public static final double PROCESSOR_SLEEP_CONSUMPTION= 0.103;
    

    public static final double SENSOR_ACTIVE_CONSUMPTION= 5;
    public static final double SENSOR_IDLE_CONSUMPTION= 0.7;
    public static final double SENSOR_SLEEP_CONSUMPTION= 0.005;

    public static final double RADIO_TX_CONSUMPTION= 12;
    public static final double RADIO_RX_CONSUMPTION= 7;
    public static final double RADIO_SLEEP_CONSUMPTION= 0.001;

    public static final double RADIO_TX_RX_TIME_BYTE= 416e-6;
    public static final double RADIO_TX_AMP_BYTE= 20;
    public static final double RADIO_RX_AMP_BYTE= 15;

    public static final double MEMORY_READ_CONSUMPTION= 6.2;
    public static final double MEMORY_WRITE_CONSUMPTION= 18.4;
    public static final double MEMORY_SLEEP_CONSUMPTION= 0.002;




    public MoteEnergyModel(node the_node_){
        the_node = the_node_;
        //energy_state = STATE_READY;

        //num_batteries = 2;

        //voltage = SINGLE_BATTERY_VOLTAGE * num_batteries;          //3 volts
        //capacity = SINGLE_BATTERY_CAPACITY * num_batteries;      //5000 mA-h
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
                //ener = (SIZE_SHORT_PACKETS*8)*(ELECT_ENERGY+(AMP_ENERGY*(comm_radius*comm_radius*java.lang.Math.PI)))/voltage;

                ener = (RADIO_TX_AMP_BYTE*RADIO_TX_RX_TIME_BYTE)*SIZE_SHORT_PACKETS;

                //energy consumed by being in Active mode so the message can be transmitted/received
                //ener += ((SIZE_SHORT_PACKETS*8)/(TX_RATE)) * calcEnergyStateConsumption(STATE_FULL_ACTIVE);

                ener += (RADIO_TX_RX_TIME_BYTE*SIZE_SHORT_PACKETS)  * calcEnergyStateConsumption(STATE_FULL_ACTIVE);

                break;

            case OPER_TX_LONG:
                //energy consumed by transmitting the message
                //ener = (SIZE_LONG_PACKETS*8)*(ELECT_ENERGY+(AMP_ENERGY*(comm_radius*comm_radius*java.lang.Math.PI)))/voltage;

                ener = (RADIO_TX_AMP_BYTE*RADIO_TX_RX_TIME_BYTE)*SIZE_LONG_PACKETS;

                //energy consumed by being in Active mode so the message can be transmitted/received
                //ener += ((SIZE_LONG_PACKETS*8)/(TX_RATE)) * calcEnergyStateConsumption(STATE_FULL_ACTIVE);

                ener += (RADIO_TX_RX_TIME_BYTE*SIZE_LONG_PACKETS)  * calcEnergyStateConsumption(STATE_FULL_ACTIVE);

                break;

            case OPER_RX_REGULAR:
                //energy consumed by receving the message
                //ener = (SIZE_SHORT_PACKETS*8)*ELECT_ENERGY;

                ener = (RADIO_RX_AMP_BYTE*RADIO_TX_RX_TIME_BYTE)*SIZE_SHORT_PACKETS;


                //energy consumed by being in Active mode so the message can be transmitted/received
                //ener += ((SIZE_SHORT_PACKETS*8)/(TX_RATE)) * calcEnergyStateConsumption(STATE_ACTIVE);

                ener += (RADIO_TX_RX_TIME_BYTE*SIZE_SHORT_PACKETS)  * calcEnergyStateConsumption(STATE_ACTIVE);
                break;

            case OPER_RX_LONG:
                //energy consumed by receving the message
                //ener = (SIZE_LONG_PACKETS*8)*ELECT_ENERGY;

                ener = (RADIO_RX_AMP_BYTE*RADIO_TX_RX_TIME_BYTE)*SIZE_LONG_PACKETS;

                //energy consumed by being in Active mode so the message can be transmitted/received
                //ener += ((SIZE_LONG_PACKETS*8)/(TX_RATE)) * calcEnergyStateConsumption(STATE_ACTIVE);

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
                        return PROCESSOR_ACTIVE_CONSUMPTION+RADIO_RX_CONSUMPTION+SENSOR_ACTIVE_CONSUMPTION;

                    case STATE_FULL_ACTIVE:
                        return PROCESSOR_ACTIVE_CONSUMPTION+RADIO_TX_CONSUMPTION+SENSOR_IDLE_CONSUMPTION;

                    case STATE_ACTIVE:
                        return PROCESSOR_ACTIVE_CONSUMPTION+RADIO_RX_CONSUMPTION+SENSOR_IDLE_CONSUMPTION;


                    case STATE_READY:
                        return PROCESSOR_IDLE_CONSUMPTION+RADIO_RX_CONSUMPTION+SENSOR_IDLE_CONSUMPTION;


                    case STATE_MONITOR:
                        return PROCESSOR_SLEEP_CONSUMPTION+RADIO_RX_CONSUMPTION+SENSOR_IDLE_CONSUMPTION;


                    case STATE_LOOK:
                        return PROCESSOR_SLEEP_CONSUMPTION+RADIO_SLEEP_CONSUMPTION+SENSOR_IDLE_CONSUMPTION;


                    case STATE_SLEEP:
                        return PROCESSOR_SLEEP_CONSUMPTION+RADIO_SLEEP_CONSUMPTION+SENSOR_SLEEP_CONSUMPTION;


                    case STATE_JUST_RADIO:
                        //return PROCESSOR_SLEEP_CONSUMPTION+RADIO_RX_CONSUMPTION+SENSOR_SLEEP_CONSUMPTION;
                        return PROCESSOR_SLEEP_CONSUMPTION+RADIO_SLEEP_CONSUMPTION+SENSOR_SLEEP_CONSUMPTION;


                    default:
                        return 0;

                }

    }

}
