package de.blablubbabc.bungeeForwardCancelledChat.common.debug;

public class Debug {

	// Not volatile for fast access. However, if accessed from different threads, this relies on external
	// synchronization to take place occasionally in order for changes to this property to eventually get propagated.
	private static boolean debugging = false;

	public static boolean isDebugging() {
		return isDebugging(null);
	}

	public static boolean isDebugging(String option) {
		// TODO Options are not yet supported
		return debugging;
	}

	public static void setDebugging(boolean debugging) {
		Debug.debugging = debugging;
	}

	private Debug() {
	}
}
