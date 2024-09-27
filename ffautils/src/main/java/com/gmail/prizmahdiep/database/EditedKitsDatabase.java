package com.gmail.prizmahdiep.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import com.gmail.prizmahdiep.utils.KitBase64Util;

public class EditedKitsDatabase 
{
    private Connection con;
    
    public EditedKitsDatabase(String path)
    {
        try {
            con = DriverManager.getConnection("jdbc:sqlite:" + path);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        try (Statement statement = con.createStatement())
        {
            statement.execute("CREATE TABLE IF NOT EXISTS edited_kits (" +
                              "owner TEXT NOT NULL," +
                              "name TEXT NOT NULL," +
                              "contents TEXT NOT NULL," +
                              "PRIMARY KEY (owner, name))");
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

    public void addEditedKit(UUID p, String name, ItemStack[] contents) throws SQLException
    {
        try (PreparedStatement prepared_statement = con.prepareStatement("INSERT INTO edited_kits (owner, name, contents) VALUES (?, ?, ?)"))
        {
            prepared_statement.setString(1, p.toString());
            prepared_statement.setString(2, name);
            prepared_statement.setString(3, KitBase64Util.itemStackArrayToBase64(contents));
            prepared_statement.executeUpdate();
        }
        catch (SQLException | IllegalStateException e)
        {
            e.printStackTrace();
        }
    }

    public void modifyEditedKit(UUID p, String name, ItemStack[] contents) throws SQLException
    {
        try (PreparedStatement prepared_statement = con.prepareStatement("UPDATE edited_kits SET contents = ? WHERE name = ? AND owner = ?"))
        {
            prepared_statement.setString(1, KitBase64Util.itemStackArrayToBase64(contents));
            prepared_statement.setString(2, name);
            prepared_statement.setString(3, p.toString());
            prepared_statement.executeUpdate();
        }
        catch (IllegalStateException | SQLException e)
        {
            e.printStackTrace();
        }
    }

    public boolean editedKitExists(UUID p, String name) throws SQLException
    {
        try (PreparedStatement prepared_statement = con.prepareStatement("SELECT * FROM edited_kits WHERE owner = ? AND name = ?"))
        {
            prepared_statement.setString(1, p.toString());
            prepared_statement.setString(2, name);
            ResultSet result_set = prepared_statement.executeQuery();
            return result_set.next();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public void removeEditedKit(UUID p, String kit_name) throws SQLException
    {
        try 
        {
            if (editedKitExists(p, kit_name))
            {
                PreparedStatement prepared_statement = con.prepareStatement("DELETE FROM edited_kits WHERE name = ? AND owner = ?");
                prepared_statement.setString(1, kit_name);
                prepared_statement.setString(2, p.toString());
                prepared_statement.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }


    public ItemStack[] getEditedKit(UUID p, String name) throws SQLException
    {
        try (PreparedStatement prepared_statement = con.prepareStatement("SELECT contents FROM edited_kits WHERE owner = ? AND name = ?"))
        {
            prepared_statement.setString(1, p.toString());
            prepared_statement.setString(2, name);
            ResultSet result_set = prepared_statement.executeQuery();

            if (result_set.next())
            {
                String storage_base64 = result_set.getString("contents");
                return KitBase64Util.itemStackArrayFromBase64(storage_base64);
            }
        }
        catch (IOException | SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getPlayerEditedKitNames(UUID p) throws SQLException
    {
        List<String> a = new ArrayList<>();
        try (PreparedStatement preparedStatement = con.prepareStatement("SELECT name FROM edited_kits WHERE owner = ?"))
        {
            preparedStatement.setString(1, p.toString());
            ResultSet result_set = preparedStatement.executeQuery();

            while(result_set.next())
            {
                String name = result_set.getString("name");
                a.add(name);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return a;
    }

    public void removeAllEditedKits(String name) throws SQLException
    {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM edited_kits WHERE name = ?"))
        {
            ps.setString(1, name);
            ps.executeUpdate();
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }

    public void removeAllEditedKits(UUID p) throws SQLException
    {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM edited_kits WHERE owner = ?"))
        {
            ps.setString(1, p.toString());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void removeAllEditedKits(UUID p, String kit_name) throws SQLException
    {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM edited_kits WHERE owner = ? AND name = ?"))
        {
            ps.setString(1, p.toString());
            ps.setString(2, kit_name);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
