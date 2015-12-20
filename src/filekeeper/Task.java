package filekeeper;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileSystemLoopException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Task {

	private String			name;
	private Path			source;
	private Path			dest;
	private Boolean			fastMode;
	private boolean			isRuning;
	public Progress			progress	= new Progress();
	private static Logger	log			= LoggerFactory.getLogger(Task.class);

	public Task(Path source, Path dest, Boolean fastMode) {
		this.source = source;
		this.dest = dest;
		this.fastMode = fastMode;
		this.isRuning = false;
	}

	public Task() {
		this.isRuning = false;
	}

	public void sync() {
		System.out.println("Start to count files.");
		isRuning = true;
		progress.reset();
		progress.setMaxValue(getFileNamber(source));
		log.info("Total files in progress: " + progress.getMaxValue().toString());

		EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
		Copy c = new Copy(source, dest.resolve(source.getFileName()), progress, fastMode);
		try {
			Files.walkFileTree(source, opts, Integer.MAX_VALUE, c);
		} catch (IOException e) {
			log.error(e.toString(), e);
		}
		isRuning = false;
	}

	public void clear() {
		// удаление в dest файлов и каталогов отсутствующих в source
		System.out.println("Start to count files.");
		isRuning = true;
		progress.reset();
		progress.setMaxValue(getFileNamber(dest.resolve(source.getFileName())));
		log.info("Total files in progress: " + progress.getMaxValue().toString());

		EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
		Clear cl = new Clear(dest.resolve(source.getFileName()), source, progress);
		try {
			Files.walkFileTree(dest.resolve(source.getFileName()), opts, Integer.MAX_VALUE, cl);
		} catch (IOException e) {
			log.error(e.toString(), e);
		}
		isRuning = false;
	}

	private long getFileNamber(Path dir) {
		EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
		Count c = new Count();
		try {
			Files.walkFileTree(dir, opts, Integer.MAX_VALUE, c);
		} catch (IOException e) {
			log.error(e.toString(), e);
		}
		return Count.value;
	}

	static void copyFile(Path source, Path target) {
		CopyOption[] options = new CopyOption[] { COPY_ATTRIBUTES, REPLACE_EXISTING };
		try {
			Files.copy(source, target, options);
		} catch (IOException x) {
			log.error("Unable to copy:", source, x);
		}
	}

	public Path getSource() {
		return source;
	}

	public void setSource(Path source) {
		this.source = source;
	}

	public Path getDest() {
		return dest;
	}

	public void setDest(Path dest) {
		this.dest = dest;
	}

	public Boolean getFastMode() {
		return fastMode;
	}

	public void setFastMode(Boolean fastMode) {
		this.fastMode = fastMode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	static class Copy implements FileVisitor<Path> {
		private final Path		source;
		private final Path		target;
		private final boolean	fastMode;
		private Progress		progress;

		public Logger			log	= LoggerFactory.getLogger(Copy.class);

		Copy(Path source, Path target, Progress progress, boolean fastMode) {
			this.source = source;
			this.target = target;
			this.fastMode = fastMode;
			this.progress = progress;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
			// силно модифицированный метод который создаёт каталог если его нет

			Path newdir = target.resolve(source.relativize(dir));
			File f = new File(newdir.toString());
			if (!f.exists()) {
				try {
					f.mkdirs();
					log.info("Create new dir: " + newdir);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
			return CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
			if (!Lib.isSameFile(file, target.resolve(source.relativize(file)), fastMode)) {
				try {
					copyFile(file, target.resolve(source.relativize(file)));
					log.info("Copy file: " + file + " to " + target.resolve(source.relativize(file)));
				} catch (Exception e) {
					log.error(e.getMessage());

				}
			}
			progress.addProgress(1);
			System.out.println(progress.getCarentPersent() + " " + file.toString());
			return CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
			// fix up modification time of directory when done
			if (exc == null) {
				Path newdir = target.resolve(source.relativize(dir));
				try {
					FileTime time = Files.getLastModifiedTime(dir);
					Files.setLastModifiedTime(newdir, time);
				} catch (IOException x) {
					System.err.format("Unable to copy all attributes to: %s: %s%n", newdir, x);
				}
			}
			return CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) {
			if (exc instanceof FileSystemLoopException) {
				System.err.println("cycle detected: " + file);
			} else {
				System.err.format("Unable to copy: %s: %s%n", file, exc);
			}
			return CONTINUE;
		}
	}

	private static class Clear implements FileVisitor<Path> {
		private final Path	source;
		private final Path	target;
		private Progress	progress;

		public Logger		log	= LoggerFactory.getLogger(Clear.class);

		Clear(Path source, Path target, Progress progress) {
			this.source = source;
			this.target = target;
			this.progress = progress;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path file, IOException exc) throws IOException {

			if (Files.notExists(target.resolve(source.relativize(file)))) {
				log.info("Dir NOT find " + target.resolve(source.relativize(file)).toString());
				try {
					Files.delete(file);
					log.info("Dir deleted " + file);
				} catch (IOException e) {
					log.error(e.getMessage());
				} catch (Exception e) {
					log.error("Unexpected error: " + e.getMessage());
				}
			}

			return CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path file, BasicFileAttributes attrs) {
			// ни чего не делаем
			return CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			// удаляет файлы отсутствующие в источнике
			if (Files.notExists(target.resolve(source.relativize(file)))) {
				log.info("File NOT find " + target.resolve(source.relativize(file)).toString());
				try {
					Files.delete(file);
					log.info("File deleted " + file);
				} catch (IOException e) {
					log.error(e.getMessage());
				} catch (Exception e) {
					log.error("Unexpected error: " + e.getMessage());
				}
			}
			progress.addProgress(1);
			System.out.println(progress.getCarentPersent() + " " + file.toString());
			return CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) {
			if (exc instanceof FileSystemLoopException) {
				log.error("cycle detected: " + file);
			} else {
				log.error("Unable to delete: " + file.toString() + " :" + exc.getMessage());
			}
			return CONTINUE;
		}
	}

	static class Count implements FileVisitor<Path> {

		public static Long	value;

		Count() {
			value = 0L;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path arg0, IOException arg1) throws IOException {
			// ничего не делаем
			return CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path arg0, BasicFileAttributes arg1) throws IOException {
			// ничего не делаем
			return CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes arg1) throws IOException {
			// увеличиваем счётчик
			value++;
			return CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path arg0, IOException arg1) throws IOException {
			// ничего не делаем
			return CONTINUE;
		}

	}

}
