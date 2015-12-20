package filekeeper;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirWatcher extends Thread {

	private Task			task;
	public boolean			listen;

	private WatchService	watchService	= null;
	private Logger			log				= LoggerFactory.getLogger(DirWatcher.class);

	public DirWatcher(Task task) {
		this.task = task;
		this.listen = true;
	}

	public void run() {

		try {
			watchService = task.getSource().getFileSystem().newWatchService();
			// dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE,
			// ENTRY_MODIFY);
			Files.walkFileTree(task.getSource(), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
					return FileVisitResult.CONTINUE;
				}
			});

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// Бесконечный цикл
		while (listen) {
			WatchKey key = null;
			try {
				// точка останова, ждем когда прийдёт обратный вызов
				key = watchService.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// Итерации для каждого события
			for (WatchEvent<?> event : key.pollEvents()) {
				Path file = (Path) event.context();
				switch (event.kind().name()) {
				case "OVERFLOW":
					System.out.println("We lost some events");
					break;
				case "ENTRY_CREATE":
					System.out.println("File " + file.toString() + " is created!");
					break;
				case "ENTRY_MODIFY":
					System.out.println("File " + file.getFileName().toString() + " is modified!");
					break;
				case "ENTRY_DELETE":
					log.info("File " + file.toString() + " is deleted!");
					break;
				}
			}
			// Сброс ключа важен для получения последующих уведомлений
			key.reset();
		}
	}
}
