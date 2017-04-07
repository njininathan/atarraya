/*
 * Main.java
 *
 * Created on July 17, 2006, 2:56 PM
 *
 * This file is part of Atarraya.

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

 Copyright 2005-2009 Pedro M. Wightman
 *
 */

package atarraya;

import javax.swing.UIManager;

/**
 *
 * @author pedrow
 */
public class Main{
    
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try{
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){e.printStackTrace();}
        
        FrameLogo my_frame2 = new FrameLogo();
        my_frame2.setBounds(200,200,500,489);
        my_frame2.setVisible(true);
        
        atarraya_frame mySim = new atarraya_frame();
        mySim.setBounds(10,0,1100,700);
        
        try{
            java.lang.Thread.sleep(2000);
        }catch(Exception e){e.printStackTrace();}
        
        my_frame2.setVisible(false);
        
        mySim.setVisible(true);
        
        
    }
    
}
