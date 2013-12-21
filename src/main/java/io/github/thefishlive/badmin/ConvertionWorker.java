package io.github.thefishlive.badmin;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.mojang.api.profiles.HttpProfileRepository;
import com.mojang.api.profiles.Profile;
import com.mojang.api.profiles.ProfileCriteria;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ConvertionWorker {

	private DataEntry entry;
	
	public boolean run(PreparedStatement statement) {
		HttpProfileRepository repo = new HttpProfileRepository();
		
		ProfileCriteria criteria = new ProfileCriteria(entry.getName(), "minecraft"); 
		Profile[] results = repo.findProfilesByCriteria(criteria);

		if (results.length == 0) {
			System.out.println("Could not find a id for " + entry.getName());
			return false;
		}

		Profile profile = results[0];
		
		try {
			BanTypeMapping mapping = BanTypeMapping.getTypeId(entry);
			
			statement.setString(1, profile.getId());
			statement.setInt(2, mapping.getNewMapping());
			statement.setString(3, entry.getReason());
			statement.setString(4, entry.getAdmin());
			statement.setString(5, mapping == BanTypeMapping.TEMPBAN ? "" + entry.getTemptime() : "");
			
			return mapping != BanTypeMapping.UNKNOWN;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		return false;
	}

}
