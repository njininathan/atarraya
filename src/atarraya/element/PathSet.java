/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package atarraya.element;

import java.util.Vector;
/**
 *
 * @author Pedro
 */
public class PathSet {

    Vector<path> the_set;

    public PathSet(Vector<path> the_set) {
        this.the_set = the_set;
    }

    public PathSet() {
        the_set = new Vector<path>();
    }

    public boolean addPath(path _path){

        if(_path!=null){
            //if(_path.getLength()>0){
                int i=0;
                boolean sw=true;

                while(sw && i<this.getLength())
                    if(path.areDisjoin(_path, this.getPath(i)))
                        i++;
                    else
                        sw=false;

                if(sw)
                    the_set.add(_path);

                return sw;
            //}
        }
        return false;
    }

    public void addPaths(String _paths){

        int i=0;
        boolean sw=true;
        String[] temp_data_array;

        temp_data_array = _paths.split("+");

        for(i=0;i<temp_data_array.length;i++){
            this.addPath(new path(temp_data_array[i]));
        }

    }

    public int getLength(){
        return the_set.size();
    }

    public path getPath(int index){
        if(index>=0 && index<this.getLength())
            return the_set.get(index);

        return null;
    }
    
    public path getLastPath(){
    
        return the_set.lastElement();
    }

    public String toString(){

        String the_string="";

        for(int i=0;i<this.getLength();i++){
            the_string=the_string+this.getPath(i).toString()+"+";
        }

        return the_string;
    }

    public String toString2(){

        String the_string="";

        for(int i=0;i<this.getLength();i++){
            the_string=the_string+this.getPath(i).toString()+"\n";
        }

        return the_string;
    }
    
}
