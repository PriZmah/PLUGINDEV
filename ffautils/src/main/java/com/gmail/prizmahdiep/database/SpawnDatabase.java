package com.gmail.prizmahdiep.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.gmail.prizmahdiep.objects.SpawnLocation;

public class SpawnDatabase 
{
    private Connection con;

    public SpawnDatabase(String path) 
    {
        try 
        {
            con = DriverManager.getConnection("jdbc:sqlite:"+ path);   
        } catch (SQLException e) 
        {
            e.printStackTrace();
        }

        try (Statement statement = con.createStatement())
        {
            statement.execute("CREATE TABLE IF NOT EXISTS spawns (" +
                                "name TEXT PRIMARY KEY" + 
                                "location_x REAL NOT NULL" + 
                                "location_y REAL NOT NULL" + 
                                "location_z REAL NOT NULL" +
                                "location_pitch REAL NOT NULL" + 
                                "location_yaw REAL NOT NULL" +
                                "location_world_name TEXT NOT NULL" +
                                "type TEXT NOT NULL)");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void closeConnection() throws SQLException
    {
        if (con != null & !con.isClosed())
            con.close();
    }

    public void addSpawn(SpawnLocation sl) throws SQLException
    {
        try (PreparedStatement prepared_statement = con.prepareStatement("INSERT INTO spawns (name, location_x, location_y, location_z, location_pitch, location_yaw, location_world_name, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"))
        {
            if (!spawnExists(sl.getName()))
            {
                prepared_statement.setString(1, sl.getName());
                prepared_statement.setDouble(2, sl.getLocation().getX());
                prepared_statement.setDouble(3, sl.getLocation().getY());
                prepared_statement.setDouble(4, sl.getLocation().getZ());
                prepared_statement.setDouble(5, sl.getLocation().getPitch());
                prepared_statement.setDouble(6, sl.getLocation().getYaw());
                prepared_statement.setString(7, sl.getLocation().getWorld().getName());   
                prepared_statement.setString(8, sl.getType());   
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void removeSpawn(String spawn_name) throws SQLException
    {
        try 
        {
            if (spawnExists(spawn_name))
            {
                PreparedStatement prepared_statement = con.prepareStatement("DELETE FROM spawns WHERE name = ?");
                prepared_statement.setString(1, spawn_name);
                prepared_statement.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    

    public boolean spawnExists(String spawn_name) throws SQLException
    {
        try (PreparedStatement prepared_statement = con.prepareStatement("SELECT * FROM spawns WHERE name = ?"))
        {
            prepared_statement.setString(1, spawn_name);
            ResultSet result_set = prepared_statement.executeQuery();
            return result_set.next();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public SpawnLocation getSpawn(String name) throws SQLException
    {
        try (PreparedStatement prepared_statement = con.prepareStatement("SELECT * FROM spaws WHERE name = ?"))
        {
            prepared_statement.setString(1, name);
            ResultSet result_set = prepared_statement.executeQuery();

            if (result_set.next())
            {
                String spawn_name = result_set.getString("name");
                double location_x = result_set.getDouble("location_x");
                double location_y = result_set.getDouble("location_y");
                double location_z = result_set.getDouble("location_z");
                double location_pitch = result_set.getDouble("location_pitch");
                double location_yaw = result_set.getDouble("location_yaw");
                String location_world_name = result_set.getString("location_world_name");
                String spawn_type = result_set.getString("type");

                Location spawn_location = new Location(Bukkit.getWorld(location_world_name), location_x, location_y, location_z, (float) location_yaw, (float) location_pitch);
                return new SpawnLocation(spawn_name, spawn_location, spawn_type);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public HashMap<String, SpawnLocation> getSpawns() throws SQLException
    {
        try (PreparedStatement prepared_statement = con.prepareStatement("SELECT * FROM spawns"))
        {
            HashMap<String, SpawnLocation> spawns = new HashMap<>();
            ResultSet result_set = prepared_statement.executeQuery();

            while (result_set.next())
            {
                String spawn_name = result_set.getString("name");
                double location_x = result_set.getDouble("location_x");
                double location_y = result_set.getDouble("location_y");
                double location_z = result_set.getDouble("location_z");
                double location_pitch = result_set.getDouble("location_pitch");
                double location_yaw = result_set.getDouble("location_yaw");
                String location_world_name = result_set.getString("location_world_name");
                String spawn_type = result_set.getString("type");

                Location spawn_location = new Location(Bukkit.getWorld(location_world_name), location_x, location_y, location_z, (float) location_yaw, (float) location_pitch);
                spawns.put(spawn_name, new SpawnLocation(spawn_name, spawn_location, spawn_type));
            }

            return spawns;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return new HashMap<>(0);
    }
    
}
