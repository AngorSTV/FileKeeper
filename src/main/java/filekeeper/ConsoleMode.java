package filekeeper;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleMode {

	String			args[];
	Path			source;
	Path			dest;
	Boolean			fastMode	= false;

	public Logger	log			= LoggerFactory.getLogger(ConsoleMode.class);

	public ConsoleMode(String args[]) {
		this.args = args;
	}

	public void run() {
		header();
		log.info("Is runing.");
		int argi = 0;

		// разбор коммандной строки
        while (argi < args.length) {
			String arg = args[argi];
			if (arg.equals("-s")) {
				source = Paths.get(args[argi + 1]);
			}
			if (arg.equals("-d")) {
				dest = Paths.get(args[argi + 1]);
			}
			if (arg.equals("-f")) {
				fastMode = true;
			}
			argi++;
		}

		if (source != null & dest != null) {
			Task task = new Task(source, dest, fastMode);
			log.info("Start sync. From " + task.getSource() + " to " + task.getDest());
			log.info("Fast mode " + fastMode);
			task.sync();

			log.info("Start clearing " + task.getDest());
			task.clear();

		} else {
			usage();
		}
		log.info("End.");
	}

	private void usage() {
		for (String arg : args) {
			log.error(arg);
		}
		System.err.println("Using:");
		System.err.println("       java -jar FileKeeper.jar -s sourcePath -d destination/targetPath [-f]");
		System.err.println("");
		System.err.println("Options:");
		System.err.println("");
		System.err.println("         -f \t fast mode by attrs only");
		System.exit(-1);

	}

	private void header() {
		System.out.println("");
		System.out.println("To keep your files in safe place. Version " + FileKeeper.version
				+ " (c) Angor Soft Video LLC");
		System.out.println("build " + FileKeeper.build);
		System.out.println("");
	}
}
