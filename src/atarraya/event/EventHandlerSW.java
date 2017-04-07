/*
 * EventHandlerSW.java
 *
 * Created on February 8, 2007, 2:49 PM
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

/**
 *
 * @author Pedro
 */
public interface EventHandlerSW {
  
    public void HandleEvent(event_sim e);
    public void initial_event(int _id, int _treeID);
    public void init_nodes(int _treeID);
    public boolean CheckIfDesiredFinalState(int s);
    public boolean verifIfNodeInCDS(int _id, int _treeID);
    public boolean CheckIfDesiredSleepingState(int s);
    public int GetMessageSize(int code);
    public void setLabels(int initial_, int active_, int inactive_, int sleeping_);
    
    public int getTMType();
    
}
