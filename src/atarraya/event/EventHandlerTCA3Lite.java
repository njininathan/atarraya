/*
 * EventHandlerA3.java
 *
 * Created on February 8, 2007, 2:48 PM
 *
This file is part of Atarraya.

Atarraya is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Atarraya is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Atarraya.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 Pedro M. Wightman
 */

package atarraya.event;

import atarraya.constants;
import atarraya.element.candidate;
import atarraya.element.node;
import atarraya.element.register;
import atarraya.atarraya_frame;

/**
 *
 * @author Pedro
 */


public class EventHandlerTCA3Lite implements EventHandlerSW,constants{
    
    atarraya_frame father;
    
    int type = TC_PROTOCOL;
    boolean TM_Selected;
    boolean SD_Selected;
    int TMType;
    boolean COMM_Selected;
    int temp_address;
    int temp_selectedTMprotocol;
    
    int temp_num_structures;
    
    int active;
    int inactive;
    int initial;
    int sleeping;
    
    double candidate_grouping_step;
            
    /** Creates a new instance of EventHandlerA3 */
    public EventHandlerTCA3Lite(atarraya_frame _father) {
        
        father = _father;    
        TM_Selected=false;
        SD_Selected=false;
        COMM_Selected=false;
        TMType = NO_TM;
        
        if(father.getVariable(SELECTED_TM_PROTOCOL)!=TM_PROTOCOL_NO_SELECTED)
            TM_Selected=true;
        
        if(father.getVariable(SELECTED_SD_PROTOCOL)!=SENSOR_PROTOCOL_NO_SELECTED)
            SD_Selected=true;
        
        if(father.getVariable(SELECTED_COMM_PROTOCOL)!=COMM_PROTOCOL_NO_SELECTED)
            COMM_Selected=true;
        
        temp_num_structures = (int)father.getVariable(NUMINFRASTRUCTURES);
        
        candidate_grouping_step = father.getVariable(CANDIDATE_GROUP_STEP);
        
        temp_selectedTMprotocol = (int)father.getVariable(SELECTED_TM_PROTOCOL);
        
        temp_address = 0;
    
        active = 0;
        inactive = 0;
        initial = 0;
        sleeping = 0;
        
    }
    
    public int getTMType(){
        return TMType;
    }
    
    public void setLabels(int _initial,int _active, int _inactive, int _sleeping){
        active=_active;
        inactive=_inactive;
        initial=_initial;
        sleeping = _sleeping;
    }
       
    public int getTreeID(){
        return father.getTreeID();
    }
    
    public int getSortingMode(){
        return (int)father.getVariable(SORTINGMODE);
    }
    
    public int getSimMode(){
        return (int)father.getVariable(SIMMODE);
    }    
    
    public int getbatchMode(){
        return (int)father.getVariable(BATCH_SIMULATION);
    }
    
 public node getNode(int id){
        return father.getNode(id);
    }
    
    public void pushEvent(event_sim e){
        father.pushEvent(e);
    }
    
    public void AddTreeLine(int s, int d, int t){
        if(getbatchMode()==1){}
            //father.AddTreeLine(s,d,t);
    }
    
    public void RemoveTreeLine(int s, int d, int t){
        if(getbatchMode()==1){}
            //father.RemoveTreeLine(s,d,t);
    }
    
    public void frame_repaint(){
        if(getbatchMode()==1)
            father.frame_repaint();
        
    }
 
    public void InvalidateAllEventsFromIDFromTimeTOfTypeTyOfCodeC(int id, double t, int ty, int c, int tree){
        father.InvalidateAllEventsFromIDFromTimeTOfTypeTyOfCodeC(id,t,ty,c,tree);
    }

    public void InvalidateRXAllEventsFromIDFromTimeTOfTypeTyOfCodeC(int id, double t, int ty, int c, int tree){
        father.InvalidateAllEventsFromIDFromTimeTOfTypeTyOfCodeC(id,t,ty,c,tree);
    }

    public void InvalidateAllEventsFromIDFromTimeTOfCodeC(int id, double t, int c, int tree){
        father.InvalidateAllEventsFromIDFromTimeTOfCodeC(id,t,c,tree);
    }
    
    public void InvalidateAllEventsFromIDFromTimeT(int id, double t, int tree){
        father.InvalidateAllEventsFromIDFromTimeT(id,t,tree);
    }
    
    public void InvalidateAllEventsFromIDFromTimeTOfTypeTy(int id, double t, int Ty, int tree){
        father.InvalidateAllEventsFromIDFromTimeTOfTypeTy(id,t,Ty,tree);
    }
    
 public void broadcast(double final_time, double current_time, int _sender, int _destination, int _code){
        father.broadcast(final_time, current_time, _sender, _destination, _code,"",-1,type);
    }

 public void broadcast(double final_time, double current_time, int _sender, int _destination, int _code, int _tree){
        father.broadcast(final_time, current_time, _sender, _destination, _code,"",_tree,type);
    }
 
 public void broadcast(double final_time, double current_time, int _sender, int _destination, int _code, String msg){
        father.broadcast(final_time, current_time, _sender, _destination, _code,msg,type);
    }
    
 public void broadcast(double final_time, double current_time, int _sender, int _destination, int _code, String msg, int _tree){
        father.broadcast(final_time, current_time,-1,-1, _sender, _destination, _code,msg,_tree,type);
    }
 
 public void broadcast(double final_time, double current_time, int _source, int _final_destination,int _sender, int _destination, int _code, String msg, int _tree){
        father.broadcast(final_time, current_time,_source,_final_destination, _sender, _destination, _code,msg,_tree,type);
    }
 
 public double getRandom(double max_val){
        return java.lang.Math.random()*max_val;
    }
 
 public void showMessage(String m){
        father.showMessage(m);
    }

  
    public void init_nodes(int _treeID){
        int i, tam=0;
        tam = (int)father.getVariable(NUMPOINTS);
        double _clock = father.getVariable(CLOCK);
        
        for(i=0;i<tam;i++){
            getNode(i).setState(initial,_treeID);
            getNode(i).SetInfrastructureStarted(_treeID,true);
            getNode(i).defineLabels(initial,active, inactive, sleeping);
            getNode(i).setAddress(i);
            
            if(TM_Selected){
                //stop all future event from the TM protocol
                pushEvent(new event_sim(_clock+getRandom(PROCESSING_DELAY), _clock, i, i, RESET_TM_PROTOCOL, "",_treeID,TM_PROTOCOL));
            }

            if(SD_Selected){
                //stop all future event from the sensor-data protocol
                pushEvent(new event_sim(_clock+getRandom(PROCESSING_DELAY), _clock, i, i, RESET_QUERY_SENSOR, "",_treeID,SENSOR_PROTOCOL));
            }
        
            
        }
        
    }
    
    public void init_node(int _treeID, int id_node){
        getNode(id_node).setState(initial,_treeID);
    }
    
    public void initial_event(int _id, int _treeID){
        
        getNode(_id).setLevel(0,_treeID);
        getNode(_id).addGateway(_treeID,_id,_id);
        getNode(_id).setSinkAddress(_treeID, _id);
        getNode(_id).setState(S_WAITING_ACTIVE,_treeID);
        
        double _clock = father.getVariable(CLOCK);
        pushEvent(new event_sim(_clock+getRandom(MAX_TX_DELAY_RANDOM), _clock, _id, _id, SEND_HELLO,"0@"+_id+"@"+_id,_treeID,type));
        
        if(TM_Selected){
            //stop all future event from the TM protocol
            pushEvent(new event_sim(_clock+getRandom(PROCESSING_DELAY), _clock, _id, _id, RESET_TM_PROTOCOL, "",_treeID,TM_PROTOCOL));
        }

        if(SD_Selected){
            //stop all future event from the sensor-data protocol
            pushEvent(new event_sim(_clock+getRandom(PROCESSING_DELAY), _clock, _id, _id, RESET_QUERY_SENSOR, "",_treeID,SENSOR_PROTOCOL));
        }
        
    }
    
    public boolean CheckIfDesiredFinalState(int s){
        if(s==active || s==inactive)
            return true;
        
        return false;
    }
    
    public boolean CheckIfDesiredSleepingState(int s){
        if(s==sleeping)
            return true;
        
        return false;
    }
    
    public boolean verifIfNodeInCDS(int _id, int _treeID){
    
        if(getNode(_id).isActive(_treeID) || getNode(_id).isSink(_treeID) || _treeID==-1)
            return true;
        
        return false;
    }
    
    public int GetMessageSize(int code){
        
        if(code==SON_RECOGNITION || code==DATA)
            return SIZE_LONG_PACKETS;
            
        return SIZE_SHORT_PACKETS;
    }
    
    public void HandleEvent(event_sim e){
    
        int code = e.getCode();
        int sender = e.getSender();
        int source = e.getSource();
        int final_destination = e.getFinalDestination();
        int receiver = e.getReceiver();
        int destination = e.getDestination();
        double temp_clock = e.getTime();
        String temp_data = e.getData();
        int temp_tree = e.getTree();
        int temp_data_int=0;
        int temp_data_int2=0;
        int temp_data_int3=0;
        String[] temp_data_array = new String[50];
        String[] temp_data_array2;
        
        int numneighb,i,j,temp_ID;
        int index,tam;
        int min_child;
        register temp_reg;
        int tempdist;
        int temp_level=0;
        int temptam=0;
        int tempmax,nmax;
        int temp=0;
        int temp2=0;
        double temp_time;
        double temp_val_double=0.0;
        double temp_val_double1=0.0;
        double temp_val_double2=0.0;
        double temp_val_double3=0.0;
        double metric=0;
        double y;
        boolean sw=true;
        boolean sw2;
        candidate temp_cand;
        double primary_val=0;
        double secondary_val=0;
        
        switch(code){
        
            case INIT_NODE:
                init_node(temp_tree, receiver);
                break;
            
            case INIT_EVENT:
                initial_event(receiver,temp_tree);
                break;
            
            /*
             * This event is the when a node will start sending Hello message to its neighbors
             */
            case SEND_HELLO:
                
                getNode(sender).setRadioOn(temp_tree, true);

                getNode(sender).setEnergyState(temp_tree, STATE_READY);
                
                //The node will clean the candidates from the neighbor's hellos messages
                //getNode(sender).cleanCandidates(temp_tree);

                getNode(sender).setState(S_ACTIVE_CANDIDATE,temp_tree);

                //Sending Hello message to all neighbors
                broadcast(temp_clock+getRandom(MAX_TX_DELAY_RANDOM), temp_clock, sender, -1, HELLO, temp_data,temp_tree);

                //Final timeout for evaluating candidates and adopt children nodes
                pushEvent(new event_sim(temp_clock+60, temp_clock, sender, sender, PARENT_RECOG_TIME_OUT, "",temp_tree,type));
    
                
                break;

            case PARENT_RECOGNITION:

                if(destination == receiver && !getNode(receiver).isSleeping(temp_tree)){
                        //InvalidateAllEventsFromIDFromTimeTOfTypeTyOfCodeC(receiver, temp_clock, type, PARENT_RECOG_TIME_OUT,temp_tree);

                        if(getNode(receiver).getState(temp_tree) != S_ACTIVE){
                                InvalidateAllEventsFromIDFromTimeTOfTypeTyOfCodeC(receiver, temp_clock, type, PARENT_RECOG_TIME_OUT,temp_tree);
                                getNode(receiver).setParentState(temp_tree, true);

                                //Initiate the TM protocol only on the active nodes of the topology, except on the local
                                if(TM_Selected && (father.getTMType() != TM_ENERGY || getNode(sender).isSink(temp_tree))){
                                    //pushEvent(new event_sim(temp_clock+DELTA_TIME, temp_clock, receiver, receiver, INIT_EVENT, "",temp_tree,TM_PROTOCOL));
                                    pushEvent(new event_sim(temp_clock+DELTA_TIME, temp_clock, receiver, receiver, INIT_EVENT, "",getNode(receiver).getActiveTree(),TM_PROTOCOL));
                                }

                                //Initiate the sensor querying protocol only on the active nodes of the topology!!
                                if(SD_Selected  && getNode(receiver).getActiveTree() == temp_tree){
                                    pushEvent(new event_sim(temp_clock+DELTA_TIME, temp_clock, receiver, receiver, INIT_EVENT, "",temp_tree,SENSOR_PROTOCOL));
                                }

                                //Initiate the COMM  protocol only on the active nodes of the topology!!
                                if(COMM_Selected){
                                    pushEvent(new event_sim(temp_clock+DELTA_TIME, temp_clock, receiver, receiver, INIT_EVENT, "",temp_tree,COMM_PROTOCOL));
                                }
                        }

                }else{

                        if(getNode(receiver).getDefaultGateway(temp_tree) == destination){
                            InvalidateAllEventsFromIDFromTimeTOfTypeTyOfCodeC(receiver, temp_clock, type, PARENT_RECOGNITION,temp_tree);
                        }

                    }

                break;

            /*
             * This event is the when a node received a Hello message from its parent
             */
            case HELLO:

                if(!getNode(receiver).isSleeping(temp_tree)){

                    //Decompress the data in the message
                    temp_data_array = temp_data.split("@");

                    if(temp_data_array.length<3)
                        temp_data_int3=0;

                    try{
                        temp_data_int = Integer.parseInt(temp_data_array[0]);  //The level of the parent is comming in the data
                        temp_data_int2 = Integer.parseInt(temp_data_array[1]); //The sink address of this tree
                        temp_data_int3 = Integer.parseInt(temp_data_array[2]); //The address of this father
                    }catch(Exception ex){ex.printStackTrace();}

                    if(getNode(receiver).isNeighbor(sender) && getNode(receiver).getState(temp_tree) != S_ACTIVE && getNode(receiver).getState(temp_tree) !=S_ACTIVE_CANDIDATE){

                        if(getNode(receiver).getState(temp_tree) == initial){
                        //if(getNode(receiver).getDefaultGateway(temp_tree) == -1){

                            //Registering the sender as the default gateway
                            getNode(receiver).setState(S_WAITING_ACTIVE,temp_tree);
                            getNode(receiver).addGateway(temp_tree, sender, temp_data_int3);
                            getNode(receiver).setDefaultGateway(temp_tree, sender);
                            getNode(receiver).setSinkAddress(temp_tree, temp_data_int2);
                            getNode(receiver).setLevel(temp_data_int+1,temp_tree);        // Define the level


                            //Calculating the metric
                            temp_val_double = getNode(receiver).getInfrastructure(temp_tree).getComm_radius();

                            temp_val_double1 = getNode(receiver).getNeighborDistance(getNode(receiver).getNeighborIndex(sender));

                            //temp_val_double1 = getNode(receiver).calcDistance(getNode(sender));

                            temp_val_double2 = temp_val_double1/temp_val_double;

                            temp_val_double3 = java.lang.Math.max(getNode(receiver).getEnergyRatio(),0);

                            temp_data_int2 = getNode(receiver).getNumTrees();

                            if(temp_data_int2>0)
                                temp_val_double3 = temp_val_double3*java.lang.Math.pow(0.5, temp_data_int2);

                            metric = father.getVariable(WEIGHT_METRIC1)*temp_val_double2 + father.getVariable(WEIGHT_METRIC2)*temp_val_double3;

                            getNode(receiver).setMetric(metric, temp_tree);

                            // Schedules the Broadcast of the Hello Message
                            pushEvent(new event_sim(temp_clock+TIMEOUT_DELAY_SHORT+((1.0-metric)*30.0), temp_clock, receiver, receiver, SEND_HELLO, ""+getNode(receiver).getLevel(temp_tree)+"@"+getNode(receiver).getSinkAddress(temp_tree)+"@"+sender,temp_tree,type));

                            //broadcast(temp_clock+metric+getRandom(MAX_TX_DELAY_RANDOM), temp_clock, receiver, sender, PARENT_RECOGNITION, temp_data,temp_tree);
                            broadcast(temp_clock+(TIMEOUT_DELAY_SHORT+((1-metric)*30.0))/2.0, temp_clock, receiver, sender, PARENT_RECOGNITION, temp_data,temp_tree);

                        }else{

                     
                            //If the sender's parent is the receivers's parent too... if the nodes are brothers...
                            if(temp_data_int3 == getNode(receiver).getDefaultGateway(temp_tree)){
                                if(getNode(receiver).getState(temp_tree) == S_WAITING_ACTIVE){
                                    InvalidateAllEventsFromIDFromTimeTOfTypeTyOfCodeC(receiver, temp_clock, type, SEND_HELLO,temp_tree);
                                    getNode(receiver).setState(S_SLEEP_CANDIDATE,temp_tree);
                                    //getNode(receiver).setRadioOn(temp_tree, false);

                                    getNode(receiver).setEnergyState(temp_tree, STATE_JUST_RADIO);

                                    pushEvent(new event_sim(temp_clock+SECOND_PARENTHOOD_DELAY+((1-metric)*30.0), temp_clock, receiver, receiver, SEND_HELLO, ""+getNode(receiver).getLevel(temp_tree)+"@"+getNode(receiver).getSinkAddress(temp_tree)+"@"+getNode(receiver).getDefaultGateway(temp_tree),temp_tree,type));
                                }
                            }else{
                                if(getNode(receiver).getState(temp_tree) == S_WAITING_ACTIVE){
                                    InvalidateAllEventsFromIDFromTimeTOfTypeTyOfCodeC(receiver, temp_clock, type, SEND_HELLO,temp_tree);
                                    pushEvent(new event_sim(temp_clock+TIMEOUT_DELAY_SHORT+((1-metric)*30.0), temp_clock, receiver, receiver, SEND_HELLO, ""+getNode(receiver).getLevel(temp_tree)+"@"+getNode(receiver).getSinkAddress(temp_tree)+"@"+getNode(receiver).getDefaultGateway(temp_tree),temp_tree,type));
                                }
                            }

                        }
                    }else{

                        //If the receiver is the parent of one of the nodes
                        if(temp_data_int3 == receiver){

                            if(!getNode(receiver).isGateway(temp_tree, sender))
                                getNode(receiver).addGateway(temp_tree, sender, sender);
                        }

                    }
                }
                break;
            
                //if the node listenes to a node that has it as a parent, then it will remain active!!!
                
            
            /*
             * This event is the when a parent node's timeout to select children finished.
             */    
                case PARENT_RECOG_TIME_OUT:

                    if(getNode(sender).getState(temp_tree) != active)
                    {
                        getNode(sender).setState(sleeping,temp_tree);
                        //getNode(sender).setRadioOn(temp_tree, false);
                        //getNode(sender).setEnergyState(STATE_JUST_RADIO);

                        if(getNode(sender).getActiveTree() == temp_tree && temp_num_structures>1)
                            pushEvent(new event_sim(temp_clock+RADIO_OFF_DELAY_PER_TREE+getRandom(PROCESSING_DELAY), temp_clock, sender, sender, DELAYED_RADIO_OFF, "",temp_tree,type));
                        else
                            getNode(sender).setEnergyState(temp_tree, STATE_SLEEP);
                     }

                break;


                case DELAYED_RADIO_OFF:

                    //getNode(sender).setRadioOn(temp_tree, false);

                    temp_data_int=0;
                    temp_data_int2=0;

                    for(i=0;i<MAX_TREES;i++){
                        if(getNode(sender).IsInfrastructureStarted(i)){
                            temp_data_int++;
                            if(CheckIfDesiredFinalState(getNode(sender).getState(i)))
                                temp_data_int2++;
                        }
                    }

                    if(temp_data_int  == temp_data_int2)
                        getNode(sender).setEnergyState(temp_tree, STATE_SLEEP);
                    else
                        pushEvent(new event_sim(temp_clock+RADIO_OFF_DELAY_PER_TREE+getRandom(PROCESSING_DELAY), temp_clock, sender, sender, DELAYED_RADIO_OFF, "",temp_tree,type));

                break;

                
               
            default:
                break;
        }
        
    }
}
