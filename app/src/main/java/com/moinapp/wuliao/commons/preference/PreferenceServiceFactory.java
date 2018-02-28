package com.moinapp.wuliao.commons.preference;

public class PreferenceServiceFactory {
	private static final String PREF_DEFULT = "moin_pref";
	public static final String MOIN_SETTINGS_PREFERENCE = "MoinSettings";

	private static IPreference sMock;

	public static void setMock(IPreference mock) {
		sMock = mock;
	}

	public static IPreference getService(String name) {
		if (sMock != null) {
			return sMock;
		}
		if(name == null || name.isEmpty()) name = PREF_DEFULT;
		return AndroidIPreference.getInstance(name);
	}
}
