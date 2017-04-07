/*
 * node.java
 *
 * Created on July 17, 2006, 3:50 PM
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

package atarraya.element;

import atarraya.element.energymodel.*;
import atarraya.element.commmodel.*;
import java.awt.Point;
import java.util.Vector;
import atarraya.constants;

/**
 *
 * @author pedrow
 */
public class node implements constants{
    
    Vector<NodeNetworkConf> StructuresConf;
    
    routing_table TM_temp_routing;

    TMStructConfList the_TM_Struct_Conf_List;

    EnergyModel the_energy_model;       //Energy model of the nodes on this Node
    int energy_model_index;

    CommModel the_comm_model;       //Communication model of the nodes on this Node
    int comm_model_index;
    
    int id;
    int address;
    Point position;
    Point position_ini;
    
    int sortingmode;
    
    int numbrothers;
    
    int active_tree;
    
    int numtrees;
    int num_temp_trees;
    int current_tree;
    int count_neighbs_TM;
    int count_active_neighbs_TM;
    int TM_State;
   double TM_energy_step;
   double TM_next_energy_thrshld;
    
    double max_energy;
    double energy;
    double diameter;
    double radius;
    double sense_radius;

    double sensor_reading_delay;

    int packets_sent;
    int packets_received;
    int packets_sent_long;
    int packets_received_long;
    
    double weight1;
    double weight2;
    double metric;
    
    int mode;
    boolean alive;
    boolean sensing_covered;
    int list_position;
    boolean sensor_on;
    boolean radio_on;
    int energy_state;

    Vector<register> neighbors;
    
    /*
     * Variables for Sensing
     */
    double interquery_time;
    
    /*
     * Variables for the MST
     */
    
    int FragmentID;
    
    /*
     * Variables for the BFS
     */
    int state_BFS;
    int level_BFS;

    int node_state_BFS;

    /*
        Variables for motion protocol
     */
    
    boolean moving;

    /*
     * Variable for A3LiteX
     */

    PathSet the_set;

    /** Creates a new instance of node */
    public node() {
        
        id = -1;
        address = -1;
        energy = -1;
        max_energy = -1;
        diameter = -1;
        radius=-1;
        sense_radius = -1;

        sensor_reading_delay = 1; //1 second

        active_tree = 0;
        
        numtrees=0;
        num_temp_trees=0;
        current_tree=0;
        mode = 0;
        sortingmode=0;
        
        count_neighbs_TM=0;
        count_active_neighbs_TM=0;
        TM_State = TM_INACTIVE;
        
        numbrothers=0;
        
        weight1=0.5;
        weight2=0.5;
        metric = 0;
        
        neighbors =  new Vector<register>();
        
        packets_sent = 0;
        packets_received = 0;
        packets_sent_long = 0;
        packets_received_long = 0;
        
        alive = true;
        sensing_covered=false;
        list_position = 0;
        sensor_on = true; //The sensor should be started by the data-sensing protocol
        radio_on = true; //The radio is on, until determined by the TC protocols
        energy_state = STATE_READY; 
        
        
        StructuresConf = new Vector<NodeNetworkConf>();
        
        TM_temp_routing = new routing_table();
        
        the_TM_Struct_Conf_List = new TMStructConfList();
        
        interquery_time = 10.0;
        
        /*
        * Variables for the MST
        */
        FragmentID = 0;
        
        state_BFS=S_NOT_VISITED;
        level_BFS=-1;


        moving = false;
        energy_model_index = 0;

        the_set = new PathSet();

    }

    public PathSet getPathSet(){
        return the_set;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean motion_) {
        moving = motion_;
    }

    public boolean isSensing_covered() {
        int temp = getActiveTree();
        if(temp != -1)
            return this.getInfrastructure(temp).isSensing_covered();
        else
            return false;
    }

    public void setSensing_covered(boolean sensing_covered_) {
        int temp = getActiveTree();
        if(temp != -1)
            getInfrastructure(temp).setSensing_covered(sensing_covered_);
    }

    public boolean isSensing_covered(int tree) {
        return this.getInfrastructure(tree).isSensing_covered();
    }

    public void setSensing_covered(boolean sensing_covered_, int tree) {
        this.getInfrastructure(tree).setSensing_covered(sensing_covered_);
    }

    public int getList_position() {
        return list_position;
    }

    public void setList_position(int list_position) {
        this.list_position = list_position;
    }



    public node(double _radius){
        this(); 
        //Initialization of the network configuration array
        int i;
        for(i=0;i<MAX_TREES;i++){
            StructuresConf.add(new NodeNetworkConf(i, _radius));       
        }
        
        FragmentID = -1;
        position_ini = new Point(0,0);
    }
 
     //Just using basic parameters
    public node(int _id, Point _position, double _max_energy, double _radius, double _sense_radius){
        this(_radius);
        id = _id;
        position = _position;
        position_ini.setLocation(_position);
        max_energy = _max_energy;
        energy = _max_energy;
        radius = _radius;
        diameter = _radius*2;
        sense_radius = _sense_radius;
        the_energy_model = new SimpleEnergyModel(this);
    }
    
    
    //Using all parameters
    public node(int _id, Point _position, double _max_energy, double _radius, double _sense_radius, double interquery_time_, double _weight1, double _weight2, int moving_, int _the_energy_model, int _the_comm_model){
        this(_radius);
        id = _id;
        position = _position;
        position_ini.setLocation(_position);
        max_energy = _max_energy;
        energy = _max_energy;
        radius = _radius;
        diameter = _radius*2;
        sense_radius = _sense_radius;
        interquery_time = interquery_time_;
        weight1=_weight1;
        weight2=_weight2;
        if(moving_ == 1)
            moving=true;

        energy_model_index = _the_energy_model;

        switch(_the_energy_model){
            case SIMPLE_ENERGY_MODEL:
                the_energy_model = new SimpleEnergyModel(this);
                break;

            case MOTE_ENERGY_MODEL:
                the_energy_model = new MoteEnergyModel(this);
                break;

            case ACOS_ENERGY_MODEL:
                the_energy_model = new AcosEnergyModel(this);
                break;
                
            case WASPMOTE_ENERGY_MODEL:
                the_energy_model = new WaspmoteEnergyModel(this);
                break;
    
            case MICA2_ENERGY_MODEL:
                the_energy_model = new MICA2EnergyModel(this);
                break;
                
                
            default:
                the_energy_model = new SimpleEnergyModel(this);
                break;
                
        }
        
        comm_model_index = _the_comm_model;

        switch(_the_comm_model){
            case SIMPLE_COMM_MODEL:
                the_comm_model = new SimpleCommModel();
                break;
            
            case WASPMOTE_COMM_MODEL:
                the_comm_model = new WaspmoteCommModel();
                break;
    
            case MICA2_COMM_MODEL:
                the_comm_model = new MICA2CommModel();
                break;
                
                
            default:
                the_comm_model = new SimpleCommModel();
                break;
                
        }

    }

    public int getNodeStateBFS(){return node_state_BFS;}
    public void setNodeStateBFS(int s){node_state_BFS=s;}

    public int getStateBFS(){return state_BFS;}
    public void setStateBFS(int s){state_BFS=s;}
    
    public int getLevelBFS(){return level_BFS;}
    public void setLevelBFS(int l){level_BFS=l;}

    public void reset_infrastructure(int tree){
        StructuresConf.get(tree).reset_infrastructure();
    }
    
    public void reset_node(){
        int i;
        for(i=0;i<MAX_TREES;i++){
            getInfrastructure(i).reset_infrastructure();
        }
    }
    
    public int getEnergyModelIndex(){
        return this.energy_model_index;
    }

    public CommModel getCommModel(){
        return this.the_comm_model;
    }
    
    public void reset_all_node(){
        int i;
        for(i=0;i<MAX_TREES;i++){
            getInfrastructure(i).reset_infrastructure();
            getInfrastructure(i).setComm_radius(radius);
        }
        energy = max_energy;

        radio_on = true;
        sensor_on = true;
        energy_state = STATE_READY;

        position.setLocation(position_ini);
        active_tree = 0;

        numtrees=0;
        num_temp_trees=0;
        current_tree=0;
        mode = 0;
        sortingmode=0;

        count_neighbs_TM=0;
        count_active_neighbs_TM=0;
        TM_State = TM_INACTIVE;

        numbrothers=0;

        packets_sent = 0;
        packets_received = 0;
        packets_sent_long = 0;
        packets_received_long = 0;

        alive = true;
        sensing_covered=false;
        list_position = 0;

        TM_temp_routing.clearRoutingTable();
        the_TM_Struct_Conf_List.clearTMConfStructList();

    }

    public void setSensorOn(boolean sw){
        sensor_on = sw;
    }

    public boolean isSensorOn(){
        return sensor_on;
    }

    /****************************************************
     * TMStructConf Related Methods
     ****************************************************/
    
    public boolean isValidTMStructConfIndex(int index){
        return the_TM_Struct_Conf_List.isValidIndex(index);
    }
    
    public int getTM_State(int index) {
        if(isValidTMStructConfIndex(index))
            return the_TM_Struct_Conf_List.getTmConfStructure(index).getTMState();
        return TM_INACTIVE;
    }

    public void setTM_State(int index, int _TM_State) {
        if(isValidTMStructConfIndex(index))
            the_TM_Struct_Conf_List.getTmConfStructure(index).setTMState(_TM_State);
    }
    
    
    public void addTmConfStructure(int index){
        the_TM_Struct_Conf_List.addTmConfStructure(index);
    }
    
    public void removeTmConfStructure(int index){
        the_TM_Struct_Conf_List.removeTmConfStructure(index);
    }
    
    public routing_table getTM_temp_routing(int index) {
        if(isValidTMStructConfIndex(index))
            return the_TM_Struct_Conf_List.getTmConfStructure(index).getTMTable();
        
        return null;  
        
    }
    
    public routing_table getTM_temp_routingListIndex(int index) {
        return the_TM_Struct_Conf_List.getTmConfStructureListIndex(index).getTMTable();
    }
    
    public boolean isTMDead(){
    
        int i=0, tam = the_TM_Struct_Conf_List.getSize();
        
        while(i<tam){
            if(the_TM_Struct_Conf_List.getTmConfStructureListIndex(i).getTMState() == TM_DEAD)
                return true;
            else
                i++;
        }
        
        return false;
        
    }
    
    public void clearTMConfStructList(){
        the_TM_Struct_Conf_List.clearTMConfStructList();
    }
    
    /*
     * ***********************************************************
     */
    
    public int getCounter(int tree) {
        return getInfrastructure(tree).getCounter();
    }

    public void setCounter(int tree, int val) {
        getInfrastructure(tree).setCounter(val);
    }
    
    public void addCounter(int tree, int val) {
        getInfrastructure(tree).addCounter(val);
    }
    
    
    
    public int getCountDataMessages(int tree) {
        return getInfrastructure(tree).getCountDataMessages();
    }

    public void addCountDataMessages(int tree) {
        getInfrastructure(tree).addCountDataMessages();
    }
    
    public void SetInfrastructureStarted(int tree, boolean sw){
        getInfrastructure(tree).setStarted(sw);
    }
    
    public boolean IsInfrastructureStarted(int tree){
        return getInfrastructure(tree).isStarted();
    }
    
    public void setRadioOn(boolean sw){
        //getInfrastructure(active_tree).setRadioOn(sw);
        radio_on = sw;
    }
    
    public boolean isRadioOn(){
        //return getInfrastructure(active_tree).isRadioOn();
        return radio_on;
    }
    
    public boolean isRadioOn(int tree){
        return getInfrastructure(tree).isRadioOn();
    }
    
    public void setRadioOn(int tree, boolean sw){
        getInfrastructure(tree).setRadioOn(sw);
    }
    
    public routing_table getRoutingTable(int tree){
        return getInfrastructure(tree).getRoutingTable();
    }
    
    public boolean isRoutingActive(int tree){
        return getInfrastructure(tree).getRoutingTable().isRoutingActive();
    }
    
    public void setRoutingActive(int tree, boolean route_active){
        getInfrastructure(tree).getRoutingTable().setRoutingActive(route_active);
    }
    
    public boolean isAutoupdate(int tree) {
        return getInfrastructure(tree).isAutoupdate();
    }

    public void setAutoupdate(int tree, boolean _autoupdate) {
        getInfrastructure(tree).setAutoupdate(_autoupdate);
    }

    public void setDefaultGateway(int tree, int _default_gateway) {
        getInfrastructure(tree).setDefaultGateway(_default_gateway);
    }

    public int getDefaultGateway(int tree) {
        return getInfrastructure(tree).getDefaultGateway();
    }
    
    public int getGateway(int tree, int address){
        return getInfrastructure(tree).getRoutingTable().getGateway(address);
    }
    
    public boolean isGateway(int tree, int ID){
        return getInfrastructure(tree).isGateway(ID);
    }
    
    public NodeNetworkConf getInfrastructure(int tree){
        return this.StructuresConf.get(tree);
    }
    
    
    
    
    public void defineLabels(int initial, int active, int inactive, int sleeping){
        int i;
        for(i=0;i<MAX_TREES;i++){
            StructuresConf.get(i).defineLabels(initial, active, inactive, sleeping);       
        }
    }
    
    public int getLabel(int tree, int type_){
        return StructuresConf.get(tree).getLabel(type_);
    }
    
    public int getNeighbCountTM(){
        return count_neighbs_TM;
    }
    
    public void setNeighbCountTM(int cntm){
        count_neighbs_TM = cntm;
    }
    
    public void add2NeighbCountTM(){
        count_neighbs_TM++;
    }
    
    public int getActiveNeighbCountTM(){
        return count_active_neighbs_TM;
    }
    
    public void setActiveNeighbCountTM(int cntm){
        count_active_neighbs_TM = cntm;
    }
    
    public void add2ActiveNeighbCountTM(){
        count_active_neighbs_TM++;
    }
    
    
    //***********************************************
    //                              MST
    //***********************************************
    
    public void setFragmentID(int _FragmentID){
          FragmentID = _FragmentID;
    }
    
    public int getFragmentID(){
        return FragmentID;
    }
    
    //***********************************************
    
    public String getSensorReading(double time){
        useEnergy(OPER_READ_SENSOR, sensor_reading_delay);
        return "test_data from "+id+"@"+time;

    }
    
    public double getInterqueryTime(){
        return interquery_time;
    }
    
    public void setInterqueryTime(double iqt){
        interquery_time = iqt;
    }
    
    public double getWeight(int index){
        
        double temp=0;
        
        switch(index){
            case WEIGHT_METRIC1:
                temp = weight1;
                break;
            
            case WEIGHT_METRIC2:
                temp = weight2;
                break;
                
            default:
                temp = 0.5;
                break;
        }
        return temp;
    }
    
    public void setWeight(int index, double val){
        switch(index){
            case WEIGHT_METRIC1:
                weight1=val;
                break;
            
            case WEIGHT_METRIC2:
                weight2=val;
                break;
            
        }
    }
    
   
    public void setActiveTree(int t){
        active_tree = t;
        radio_on = getInfrastructure(t).isRadioOn();
        energy_state = getInfrastructure(t).getEnergyState();
    }



    public int getActiveTree(){
        return active_tree;
    }
    
    public void incrementActiveTree(){
        int i=0;
        do{
            active_tree = (active_tree+1)%MAX_TREES;
            i++;
        }while(!StructuresConf.get(active_tree).isStarted() && i<MAX_TREES);
        // If the node still has the initial state is because that network has not been created
    }
    
    public int getNextActiveTree(){
        int temp = active_tree;
        int i=0;
        do{
            temp = (temp+1)%MAX_TREES;
            i++;
        }while(!StructuresConf.get(active_tree).isStarted() && i<MAX_TREES);
        // If the node still has the initial state is because that network has not been created
        return temp;
    }
    
    public void setAddress(int a){
        address = a;
    }
       
    public void setNumBrothers(int n){
        numbrothers = n;
    }
    
    
    public int getNumBrothers(){
        return numbrothers;
    }
    
    public void setMode(int m){
        mode = m;
    }
    
    public void setMetric(double _metric, int tree){
        getInfrastructure(tree).setMetric(_metric);
    }
    
    public double getMetric(int tree){
        return getInfrastructure(tree).getMetric();
    }
    
    
    public int getRole(int tree){
        return StructuresConf.get(tree).getRole();
    }
    
    public void setRole(int tree, int role){
        StructuresConf.get(tree).setRole(role);
    }
    
    
    public int getMode(){
        return mode;
    }
    
    public void setSortingMode(int m){
        sortingmode = m;
    }
    
    public int getSortingMode(){
        return sortingmode;
    }
    
    public void addCurrentTree(){
        current_tree = (current_tree+1)%numtrees;
    }
    
    public int getCurrentTree(){
        return current_tree;
    }
    
    public int getID(){
        return id;
    }
    
    public int getAddress(){
        return address;
    }
    
    public Point getPosition(){
        return position;
    }
    
    public int getLevel(int t){
        if(t>-1)
            //return levels[t];
            return StructuresConf.get(t).getLevel();
        
        return 0;
    }
    
    public void SortNeighbors(){
        java.util.Collections.sort(neighbors);
    }
    
    public double calcDistance(node e){
        double x = e.getPosition().getX();
        double y = e.getPosition().getY();
        
        long dist;
        
        dist = java.lang.Math.round(java.lang.Math.sqrt(java.lang.Math.pow(position.getX()-x,2)+java.lang.Math.pow(position.getY()-y,2)));
        
        return dist;
    }
    
    public void setDead(){
        int i;
        alive = false;
        for(i=0;i<MAX_TREES;i++){
            getInfrastructure(i).setState(S_DEAD);
        }
    }
    
    
    
    public boolean checkTMEnergy(){
    
        if(getEnergyRatio() <= TM_next_energy_thrshld){
            TM_next_energy_thrshld = java.lang.Math.min(energy-TM_energy_step, DEATH_THSLD_ENERGY);
            return true;
        }
        
        return false;
    }

    public double getTM_energy_step() {
        return TM_energy_step;
    }

    public void setTM_energy_step(double TM_energy_step_) {
        TM_energy_step = TM_energy_step_;
        TM_next_energy_thrshld = getEnergyRatio()-TM_energy_step+0.01;
    }
    
    public int getEnergyState(){
        return energy_state;
    }

    public void setEnergyState(int energy_state_){
        getInfrastructure(getActiveTree()).setEnergyState(energy_state_);
        the_energy_model.setEnergyState(energy_state_);
        energy_state = energy_state_;
    }


    public int getEnergyState(int tree){
        return getInfrastructure(tree).getEnergyState();

    }

    public void setEnergyState(int tree, int energy_state_){
        getInfrastructure(tree).setEnergyState(energy_state_);
        if(tree == getActiveTree())
            setEnergyState(energy_state_);
    }


    public double calcEnergy(int oper){

        return the_energy_model.calcEnergy(oper, 0);
    }

    public double calcEnergy(int oper, double time){

        return the_energy_model.calcEnergy(oper, time);
    }

    public double calcEnergyStateConsumption(){

        return the_energy_model.calcEnergyStateConsumption();

    }

    public double calcEnergyStateConsumption(int energy_state){

        return the_energy_model.calcEnergyStateConsumption(energy_state);

    }

    
    public void addPacketsSent(int _code){
        if(_code==SON_RECOGNITION || _code==DATA)
            packets_sent_long++;
        else
            packets_sent++;
        
    }
    
    public int getPacketsSent(){
        return packets_sent;
    }
    
    public int getPacketsSentLong(){
        return packets_sent_long;
    }
    
    public void addPacketsReceived(int _code){
         if(_code==SON_RECOGNITION || _code==DATA)
            packets_received_long++;
        else
            packets_received++;
    }

    public void useEnergy(int oper, double time){
        energy = energy - calcEnergy(oper, time);
    }

    /*public void useEnergy(int oper, int tree){
        energy = energy - calcEnergy(oper, tree);
    }*/
    
    /*public void useEnergy2(int oper, int tree){
        energy = energy - 0.5*calcEnergy(oper, tree);
    }*/
    
    public int getPacketsReceivedLong(){
        return packets_received_long;
    }
    
    public int getPacketsReceived(){
        return packets_received;
    }
    
    public int getNumGateways(int tree){
        if(tree>-1)
            return StructuresConf.get(tree).getNumGateways();
        
        return -1;
    }
    
    public int getGatewayID(int tree, int index){
        if(tree>-1)
            return StructuresConf.get(tree).getGatewayID(index);
        
        return -1;
    }
    
    public int getGatewayAddress(int tree, int index){
        if(tree>-1)
            return StructuresConf.get(tree).getGatewayAddress(index);
        
        return -1;
    }
    
    public int getSinkAddress(int tree){
        //return parents[tree].getZ();
        return StructuresConf.get(tree).getSinkAddress();
    }
   
     
    public double getEnergy(){
        return energy;
    }
    
    public double getEnergyRatio(){
        return energy/max_energy;
    }
    
    //Update Structure
    public int getState(int t){
        if(t>-1)
            //return state_array[t];
            return StructuresConf.get(t).getState();
        
        return S_SLEEPING;
    }
    
    public double getDiameter(){
        return diameter;
    }
    public double getRadius(){
        return diameter/2.0;
    }

    public double getActiveRadius(){
        return StructuresConf.get(active_tree).getComm_radius();
    }

    public double getRadius(int tree){
        return StructuresConf.get(tree).getComm_radius();
    }
    
    public double getSense_Radius(){
        return sense_radius;
    }
    
    public double getSense_Diameter(){
        return sense_radius*2.0;
    }
    
    public int getNumNeighbors(){
        return neighbors.size();
    }
    
    public int getNeighborID(int n){
        return neighbors.get(n).getID();
    }

    public register getNeighbor(int n){
        return neighbors.get(n);
    }
    
    public double getNeighborDistance(int n){
        return neighbors.get(n).getDistance();
    }
    
    public int getNeighborIndex(int id){
        int i=0, tam = getNumNeighbors();
        boolean found = false;
        
        while(i<tam && !found){
            if(neighbors.get(i).getID()==id){
                found=true;
                return i;
            }
            else
                i++;
        }
        
        return -1;
    }
    
    public register getNeighborFromID(int id){
        int i=0, tam = getNumNeighbors();
        boolean found = false;
        
        while(i<tam && !found){
            if(neighbors.get(i).getID()==id){
                found=true;
                return neighbors.get(i);
            }
            else
                i++;
        }
        
        return null;
    }
    
   
    
    //Update structure
    public int getNumChildren(int tree){
        return StructuresConf.get(tree).getNumNeighbors();
    }
    
        
    public int getChildID(int tree, int i){
        return StructuresConf.get(tree).getNeighbor(i).getID();
    }
    
    //Update structure
    public candidate getChild(int tree, int i){
        return StructuresConf.get(tree).getNeighbor(i);
    }
    
    public candidate getChildFromID(int tree, int id){
        int i=0, tam = getNumChildren(tree);
        boolean found = false;
        
        while(i<tam && !found){
            if(getChild(tree,i).getID()==id){
                found=true;
                return getCandidate(tree,i);
            }
            else
                i++;
        }
        
        return null;
    }
    
    public int getChildIndexFromID(int tree, int id){
        int i=0, tam = getNumChildren(tree);
        boolean found = false;
        
        while(i<tam && !found){
            if(getChild(tree,i).getID()==id){
                found=true;
                return i;
            }
            else
                i++;
        }
        
        return -1;
    }
    
    //Update structure
    public int getNumCandidates(int tree){
        return StructuresConf.get(tree).getNumCandidates();
    }
    
    
    //Update structure
    public candidate getCandidate(int tree, int i){
        return StructuresConf.get(tree).getCandidate(i);
    }
    
    
    public candidate getCandidateFromID(int tree, int id){
        int i=0, tam = getNumCandidates(tree);
        boolean found = false;
        
        while(i<tam && !found){
            if(getCandidate(tree,i).getID()==id){
                found=true;
                return getCandidate(tree,i);
            }
            else
                i++;
        }
        
        return null;
    }
    
    public int getCandidateIndexFromID(int tree, int id){
        int i=0, tam = getNumCandidates(tree);
        boolean found = false;
        
        while(i<tam && !found){
            if(getCandidate(tree,i).getID()==id){
                found=true;
                return i;
            }
            else
                i++;
        }
        
        return -1;
    }
    
    
    public String getNeighborsList(){
        String s=""+neighbors.size()+"@\n";
        int i;
    
        /*
         * Format of the list: <# of neighbors>@index-ID-metric*index-ID-metric*...
         */
        
        for(i=0;i<neighbors.size();i++){
            s=s+neighbors.get(i).toString2();
        }
    
        return s;
    }
    
    public int getFarthestNeighbor(){
        int i,numneigh,great=0;
        double great_d=0;
        
        numneigh = getNumNeighbors();
        
        for(i=0; i<numneigh ; i++){
        
            if(great_d < getNeighbor(i).getDistance()){
                great_d = getNeighbor(i).getDistance();
                great = i;
            }
        }
        
        return getNeighbor(great).getID();
    }
    
    public int getNumTrees(){
        int count_trees=0, i=0;

        for(i=0;i<MAX_TREES;i++)
            if(getInfrastructure(i).isActive())
                count_trees++;

        return count_trees;
    }
    
    public int getNumTempTrees(){
        return this.num_temp_trees;
    }
    
    public void addTempNumTree(int factor){
        //Factor will be -1 or +1.
        //It will represent the  number of trees in which the node is competing to become part of, but have not been selected or rejected
        num_temp_trees+= factor;
    }
    
    public void addNumTree(int factor){
        //Factor will be -1 or +1.
        //It will represent the  number of trees in which the node is competing to become part of, but have not been selected or rejected
        //numtrees+= factor;
    }
   
    
    public void addNeighbor(register n){
        neighbors.add(n);
    }
    
    //Update Structure
    public void addChild(int tree, candidate n){
        StructuresConf.get(tree).addNeighbor(n);
    }
    
    
    //Update Structure
    public void addCandidate(int tree, candidate n){
        StructuresConf.get(tree).addCandidate(n);
    }
    
    public boolean isAlive(){
        return alive;
    }
    
    public boolean isNeighbor(int id){
        int i, tam;
        boolean sw=false;
        i=0;
        
        while(i<neighbors.size()){
            if(id == neighbors.get(i).getID())
                sw=true;
            i++;
        }
        
        return sw;
    }
    
    public boolean isNeighbor(node e){
        double x = e.getPosition().getX();
        double y = e.getPosition().getY();
        
        long dist;
        
        dist = java.lang.Math.round(java.lang.Math.sqrt(java.lang.Math.pow(position.getX()-x,2)+java.lang.Math.pow(position.getY()-y,2)));
        
        if(dist<=getRadius()){
            return true;
        }
        
        return false;   
        
    }
    
    // Evaluating if a point is covered by a certain node in comm area
    public boolean covers(int x, int y, int mode){
        long dist;
        
        dist = java.lang.Math.round(java.lang.Math.sqrt(java.lang.Math.pow(position.getX()-x,2)+java.lang.Math.pow(position.getY()-y,2)));

        if(mode == 0){
            if(dist<=getInfrastructure(getActiveTree()).getComm_radius()){
                return true;
            }
        }else if(mode == 1){
                if(dist<=getSense_Radius()){
                return true;
            }
        }


        return false;
    }
    
    public boolean isNeighbor2(node e){
        double x = e.getPosition().getX();
        double y = e.getPosition().getY();
        
        long dist;
        
        dist = java.lang.Math.round(java.lang.Math.sqrt(java.lang.Math.pow(position.getX()-x,2)+java.lang.Math.pow(position.getY()-y,2)));
        
        if(dist<=getRadius()){
            neighbors.add(new register(e.getID(), e.getAddress(), dist, e.getEnergy(),MODE_DISTANCE,sortingmode,this.the_comm_model.calcBER(dist)));
            return true;
        }
        
        return false;
        
    }
    
    // Evaluating if a point is covered by a certain node in sesing area
    public boolean sense_covers(int x, int y){
        long dist;
        
        dist = java.lang.Math.round(java.lang.Math.sqrt(java.lang.Math.pow(position.getX()-x,2)+java.lang.Math.pow(position.getY()-y,2)));
        
        if(dist<= this.getSense_Radius()){
            return true;
        }
        
        return false;
    }
    
    public boolean isSenseNeighbor(node e){
        double x = e.getPosition().getX();
        double y = e.getPosition().getY();
        
        long dist;
        
        dist = java.lang.Math.round(java.lang.Math.sqrt(java.lang.Math.pow(position.getX()-x,2)+java.lang.Math.pow(position.getY()-y,2)));
        
        if(dist<=2*sense_radius)
            return true;
        
        return false;   
        
    }
    
    
    
    //Updated structure
    public void RemCandidate(int tree){
        StructuresConf.get(tree).RemCandidate();
    }
    
    
    //Updated structure
    public void RemCandidateIndexC(int tree, int index){
        StructuresConf.get(tree).RemCandidateFromIndex(index);
    }
    
    //Updated Structure
    public void RemCandidateFromIDIF(int tree, int index){
        StructuresConf.get(tree).RemoveCandidateFromID(index);
    }
    
    
    //Update structure
    public void SortCandidates(int tree){
        StructuresConf.get(tree).SortCandidates();
    }
    
    //Update structure
    public void SortChildren(int tree){
        StructuresConf.get(tree).SortNeighbors();
    }
    
    //Update structure
    public boolean isChildren(int tree, int id){
        return StructuresConf.get(tree).isNeighbor(id);
    }
        
    
    //Update structure
    public boolean isCandidate(int tree, int id){
        return StructuresConf.get(tree).isCandidate(id);
    }
    
    public int where_am_I_sink(){
        int i=0;
       
        while(i<MAX_TREES && !isSink(i)){
            i++;
        }
        
        if(i<MAX_TREES)
            return i;
        
        return -1;
    }
    
    //Update structure
     public boolean isSink(int tree){
         if(tree==-1)
            return false;
        else{
             if(StructuresConf.get(tree).isSink())
                 return true;
        }
        return false;
    }
    
    public boolean isSink(){
        int i=0;
       
        while(i<MAX_TREES && !isSink(i)){
            i++;
        }
        if(i<MAX_TREES)
            return true;
        
        return false;
    }
    
    public boolean isSleeping(int tree){
        return StructuresConf.get(tree).isSleeping();
    }
    
    public boolean isInactive(int tree){
        return StructuresConf.get(tree).isInactive();
    }
    
    //Update structure
    public boolean isActive(int tree){
        if(tree==-1){
            if(node_state_BFS == S_ACTIVE_BFS)
                return true;
            else
                return false;
        }
        
        //return im_parent[tree];
        return StructuresConf.get(tree).isActive();
    }
    
    
    
    //Update structure
    public boolean isVisited(int tree){
        return StructuresConf.get(tree).isVisited();
    }
    
    
    //Update structure   
    public boolean isCovered(int tree){    
        
        //If all tress are being visualized
        if(tree==-1)
            return true;
        else{
            //If it has a gateway in the tree 
            if(StructuresConf.get(tree).isCovered())
                return true;
        }
        return false;
    }
    
    
    //Update structure
    public boolean isSelected(int tree){
        //if(state==S_SELECTED)
        if(StructuresConf.get(tree).isSelected())
            return true;
        
        return false;
    }
    
    
    //Update structure
    public void setLevel(int l,int t){
        //levels[t] = l;
        StructuresConf.get(t).setLevel(l);
    }
    
    
    public void setGatewayID(int tree, int index, int gwid){
        StructuresConf.get(tree).setGatewayID(index,gwid);
    }
    
    //Update structure
    public void setGatewayAddress(int tree, int index, int gwad){
        StructuresConf.get(tree).setGatewayAddress(index, gwad);
    }
    
    
    //Update structure
    public void setGateway(int tree, int index, int gwID, int gwADDR){
        
        StructuresConf.get(tree).setGatewayID(index, gwID);
        StructuresConf.get(tree).setGatewayAddress(index, gwADDR);
        addTempNumTree(1);
    }
    
    public void addGateway(int tree, int _ID, int _address){
        getInfrastructure(tree).addGateway(_ID, _address);
    }
    
    public void ClearGateways(int tree){
        getInfrastructure(tree).clear_gateways();
    }
    
    public void RemoveGatewayFromID(int tree, int id){
        getInfrastructure(tree).RemoveGatewayFromID(id);
    }
    
    //Update structure
    public void setSinkAddress(int tree, int sad){
        StructuresConf.get(tree).setSinkAddress(sad);
    }
    
    
    //Update structure
    public void setState(int s, int t){
        //state_array[t] = s;
        StructuresConf.get(t).setState(s);
    }
    
    //Update structure
    public void setParentState(int tree, boolean sw){
        //im_parent[tree]=sw;
        if(sw){
            StructuresConf.get(tree).setActiveNodeState();
            //addNumTree(1);
        }
        else
            StructuresConf.get(tree).setInactiveNodeState();
        addTempNumTree(-1);
    }
    
    //Update structure
    public void setRadius(int d){
        radius = d;
        diameter = 2*radius;
        int i;
        for(i=0;i<MAX_TREES;i++){
            StructuresConf.get(i).setComm_radius(d);
        }
    }
    
    //Update structure
    public void setRadius(int tree, double d){
        //radius = d;
        //diameter = 2*radius;
        StructuresConf.get(tree).setComm_radius(d);
    }
    
    public void setPos(int _x, int _y){
        position.x=_x;
        position.y=_y;
    }
    
    
    public void cleanNeighbors(){
        neighbors.clear();
    }
    
    public void cleanChildren(int tree){
        this.StructuresConf.get(tree).cleanNeighbors();
    }
    
    public void RemChildfromIndex(int tree, int index){
        getInfrastructure(tree).RemoveNeighborFromIndex(index);
    }
    
    public void RemChildfromID(int tree, int ID){
        getInfrastructure(tree).RemoveNeighborFromID(ID);
    }
    
    //Update structure
    public void cleanCandidates(int tree){
        this.StructuresConf.get(tree).cleanCandidates();
    }
    
    public int compareTo(Object obj){
        return -2;
    }
    
    public String toStringforDeployment(){
        
        int motion=0;
        
        if(moving)
            motion=1;

        String t=""+(int)position.getX()+"@"
            +(int)position.getY()+"@"
            +max_energy+"@"
            +radius+"@"
            +sense_radius+"@"
            +interquery_time+"@"
            +weight1+"@"
            +weight2+"@"
            +motion+"@"
            +energy_model_index+"@"
            +comm_model_index;
        
        return t;
    }
    
    
    
    public String toString4(int _treeID){
        int i=0, j=0, tam=0;
        String s="", r="";
       
        String t = "ID: "+id+"\n"
            +"Address: "+address+"\n"            
            +"X: "+(int)position.getX()+"\n"
            +"Y: "+(int)position.getY()+"\n"
            +"Energy: "+energy+"\n"
            +"Comm Radius: "+radius+"\n"
            +"Sense Radius: "+sense_radius+"\n"
            +"Active Infrastructure: "+ getActiveTree()+"\n"
            +"Pakets sent : "+ getPacketsSent()+"\n"
            +"Pakets received : "+ getPacketsReceived()+"\n"
            +"Pakets sent long: "+ getPacketsSentLong()+"\n"
            +"Pakets received long: "+ getPacketsReceivedLong()+"\n"

            +"---TM ROUTING TABLES---\n";

        
            for(i=0;i<the_TM_Struct_Conf_List.getSize();i++){
        
                t=t+"---TM  CONF STRUCTURE "+i+" ---\n"
                       +"TM Index: "+the_TM_Struct_Conf_List.getTmConfStructureListIndex(i).getIndex()+"\n"
                       +"TM State: "+the_TM_Struct_Conf_List.getTmConfStructureListIndex(i).getTMState()+"\n"
                        +"\n";
                tam = the_TM_Struct_Conf_List.getTmConfStructureListIndex(i).getTMTable().getRoutingTableSize();
                
                for(j=0;j<tam;j++){
                    t=t+"---ROUTING TABLE "+j+" ---\n"
                         +"Node "+j+" ID: "+ getTM_temp_routingListIndex(i).getRegister(j).getNodeAddress()+"\n"
                         +"Gateway "+j+" ID: "+ getTM_temp_routingListIndex(i).getRegister(j).getGatewayID()+"\n"
                         +"Number of Hops "+j+" : "+ getTM_temp_routingListIndex(i).getRegister(j).getNum_hops()+"\n"
                         +"Metric "+j+" : "+getTM_temp_routingListIndex(i).getRegister(j).getNodeMetric()+"\n\n";
                }
        
            }
        
        
            for(i=0;i<MAX_TREES;i++){
                 if(getInfrastructure(i).isStarted()){
                     if(isSink(i))
                        s="SINK NODE";
                    else
                        s="REGULAR NODE";

                     if(this.isRadioOn(i))
                        r="ON";
                    else
                        r="OFF";

                    t=t+"\n-----Structure "+i+"-------\n"
                    +"Status: "+s+"\n"
                    +"Radio status: "+r+"\n"
                    +"Sensing Covered: "+ isSensing_covered(i)+"\n"
                    +"Level: "+getLevel(i)+"\n"
                    +"Metric: "+getMetric(i)+"\n"
                    +"Counter: "+getCounter(i)+"\n"
                    +"---GATEWAYS---\n"
                    +"Default Gateway: "+getDefaultGateway(i)+"\n"
                    +"---PATHS---\n"
                    +getPathSet().toString2()+"\n";

                    for(j=0;j<getInfrastructure(i).getNumGateways();j++){
                        t=t+"Gateway "+j+" ID: "+ getInfrastructure(i).getGatewayID(j)+"\n"
                        +"Gateway "+j+" Address: "+getInfrastructure(i).getGatewayAddress(j)+"\n";
                    }

                    t=t+"\nSink Address: "+getSinkAddress(i)+"\n"
                    +"State: "+getState(i)+"\n"
                    +"Neighbors: "+getNeighborsList()+"\n"
                    +"Candidates: "+ StructuresConf.get(i).getCandidateList2()+"\n"
                    +"Children: "+ StructuresConf.get(i).getNeighborsList()+"\n";
                }
            }
        return t;
        
    }
  
    
    
    
    
    
}
