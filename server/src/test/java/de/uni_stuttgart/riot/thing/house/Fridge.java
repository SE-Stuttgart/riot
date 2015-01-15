package de.uni_stuttgart.riot.thing.house;

import de.uni_stuttgart.riot.thing.Thing;

public class Fridge extends Thing {

    public Fridge() {
        super("Fridge", 42L);
    }

    @Override
    protected void initProperties() {
       this.addProperty("temperature", 8);
       this.addProperty("State", true);
    }

    @Override
    protected void initActions() {
        
    }

    @Override
    protected void initEvents() {
        
    }
    
    public void turnOff(){
        this.changeProperty("State", false);
    }

    public void turnOn(){
        this.changeProperty("State", true);
    }
}
