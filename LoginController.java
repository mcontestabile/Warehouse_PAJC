package it.unibs.pajc.warehouse;

import it.unibs.pajc.utilities.UsefulStrings;

public class LoginController {
	private LoginModel model;

	public LoginController(String givenUsername, String givenPassword) {
		model = new LoginModel(givenUsername, givenPassword);
	}

	public boolean checkUser() throws Exception {
		if ((model.getUsername().equals(UsefulStrings.CUSTOMER_USERNAME) && model.getPassword().equals(UsefulStrings.CUSTOMER_PASSWORD)) ||
				(model.getUsername().equals(UsefulStrings.WAREHOUSEMAN_USERNAME) && model.getPassword().equals(UsefulStrings.WAREHOUSEMAN_PASSWORD)))
			return true;

		return false;
	}
	
	public LoginModel getUser() {
		return model;
	}
}
