package com.gmail.prizmahdiep.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.gmail.prizmahdiep.objects.Kit;
import com.gmail.prizmahdiep.objects.KitInterface;
import com.gmail.prizmahdiep.utils.KitBase64Util;

public class KitDatabase 
{
    private Connection con;
    
    public KitDatabase(String path)
    {
        try 
        {
            con = DriverManager.getConnection("jdbc:sqlite:" + path);
        } catch (SQLException e) 
        {
            e.printStackTrace();
        }
        
        try (Statement statement = con.createStatement())
        {
            statement.execute("CREATE TABLE IF NOT EXISTS kits (" + 
                                "name TEXT PRIMARY KEY," +
                                "storage TEXT NOT NULL," +
                                "effects TEXT NOT NULL," + 
                                "restorable INTEGER NOT NULL)");
        } catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }

    public void closeConnection() throws SQLException
    {
        if (con != null & !con.isClosed())
            con.close();
    }

    public void addKit(KitInterface k) throws SQLException
    {
        try (PreparedStatement prepared_statement = con.prepareStatement("INSERT INTO kits (name, storage, effects, restorable) VALUES (?, ?, ?, ?)"))
        {
            if (!kitExists(k.getName()))
            {
                prepared_statement.setString(1, k.getName());
                prepared_statement.setString(2, KitBase64Util.itemStackArrayToBase64(k.getInventory()));
                prepared_statement.setString(3, KitBase64Util.potionEffectCollectionToBase64(k.getPotionEffects()));
                prepared_statement.setBoolean(4, k.isRestorable());
                prepared_statement.executeUpdate();
                prepared_statement.close();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void removeKit(String kit_name) throws SQLException
    {
        try 
        {
            if (kitExists(kit_name))
            {
                PreparedStatement prepared_statement = con.prepareStatement("DELETE FROM kits WHERE name = ?");
                prepared_statement.setString(1, kit_name);
                prepared_statement.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public boolean kitExists(String kit_name) throws SQLException
    {
        try (PreparedStatement prepared_statement = con.prepareStatement("SELECT * FROM kits WHERE name = ?"))
        {
            prepared_statement.setString(1, kit_name);
            ResultSet result_set = prepared_statement.executeQuery();
            return result_set.next();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public Kit getKit(String name) throws SQLException
    {
        try (PreparedStatement prepared_statement = con.prepareStatement("SELECT * FROM kits WHERE name = ?"))
        {
            prepared_statement.setString(1, name);
            ResultSet result_set = prepared_statement.executeQuery();

            if (result_set.next())
            {
                String kit_name = result_set.getString("name");
                String storage_base64 = result_set.getString("storage");
                String effects_base64 = result_set.getString("effects");
                Boolean restorable = result_set.getBoolean("restorable");

                ItemStack[] storage = KitBase64Util.itemStackArrayFromBase64(storage_base64);
                Collection<PotionEffect> effects = KitBase64Util.potionEffectsFromBase64(effects_base64);

                return new Kit(kit_name, storage, effects, restorable);
            }
        }
        catch (SQLException | IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<String, KitInterface> getKits() throws SQLException
    {
        try (PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM kits"))
        {
            ResultSet rs = preparedStatement.executeQuery();
            HashMap<String, KitInterface> ret = new HashMap<>();

            while (rs.next())
            {
                String kit_name = rs.getString("name");
                String storage_base64 = rs.getString("storage");
                String effects_base64 = rs.getString("effects");
                boolean restorable = rs.getBoolean("restorable");

                ItemStack[] storage = KitBase64Util.itemStackArrayFromBase64(storage_base64);
                Collection<PotionEffect> effects = KitBase64Util.potionEffectsFromBase64(effects_base64);

                ret.put(kit_name, (KitInterface) new Kit(kit_name, storage, effects, restorable));
            }

            return ret;
        }
        catch (SQLException | IOException e)
        {
            e.printStackTrace();
        }
        return new HashMap<>(0);
    }
}
