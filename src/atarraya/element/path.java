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
public class path {

    private Vector<Integer> the_path;

    public path(){

        the_path = new Vector<Integer>();

    }

    public path(path _path){

        the_path = new Vector<Integer>(_path.getVector());

    }

    public path(String st_path){

        String[] temp_data_array;
        int val=0;

        the_path = new Vector<Integer>();

        temp_data_array = st_path.split("#");

        for(int i=0;i<temp_data_array.length;i++){
            try{
                val=Integer.parseInt(temp_data_array[i]);
                if(val!=-1)
                    the_path.add(val);
            }catch(Exception ex){ex.printStackTrace();
                                 i=temp_data_array.length;
                                 the_path.clear();}
        }


    }

    private Vector<Integer> getVector(){
        return the_path;
    }

    public void addElement(int reg){
        the_path.add(reg);
    }

    public int getLength(){
        return the_path.size();
    }

    public int getElement(int index){
        if(index>=0 && index<this.getLength())
            return the_path.get(index);

        return -1;
    }

    public static boolean areDisjoin(path _path, path _path2){

        int i=0,j=0;
        int size1 = _path.getLength();
        int size2 = _path2.getLength();
        boolean sw=true;

        while(i<size1 && sw){
            j=0;
            while(j<size2 && sw){

                if(_path.getElement(i)==_path2.getElement(j))
                    sw=false;

                j++;
            }
            i++;
        }

        return sw;
    }

    public String toString(){

        String the_string="";

        for(int i=0;i<this.getLength();i++){
            the_string=the_string+this.getElement(i)+"#";
        }

        return the_string;
    }

}
