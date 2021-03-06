package grisu.frontend.blender;

public class BlenderHelpers {

	public static String createCommandline(int startFrame, int endFrame,
			String pathToInputFiles, String blenderFilename, String format,
			String outputFilename) {

		String framesToCalculatePart;
		if (startFrame == endFrame) {
			framesToCalculatePart = " -f " + startFrame;
		} else {
			framesToCalculatePart = " -s " + startFrame + " -e " + endFrame
					+ " -a";
		}

		final String result = "blender " + "-b " + pathToInputFiles + "/"
				+ blenderFilename + " -F " + format.toString() + " -o "
				+ outputFilename + framesToCalculatePart;

		return result;

	}

}
