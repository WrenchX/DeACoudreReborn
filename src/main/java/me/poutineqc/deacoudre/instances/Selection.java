package me.poutineqc.deacoudre.instances;

import org.bukkit.Location;
import org.bukkit.World;

public class Selection {

    private Location pos1;

    private Location pos2;

    public Selection() {
    }

    public void setPos1(Location loc) {
        pos1 = loc;
    }

    public void setPos2(Location loc) {
        pos2 = loc;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public World getWorld() {
        if (pos1.getWorld() != pos2.getWorld()) {
            return null;
        }
        return pos1.getWorld();

    }

    public Location getMinimumPoint() {
        //MIN X MIN Y MIN Z
        return new Location(this.getWorld(), Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
    }

    public Location getMaximumPoint() {
        return new Location(this.getWorld(), Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
    }

}
